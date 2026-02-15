package com.example.afmobile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.afmobile.data.FirebaseUser
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PhoneNumberActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var etPhoneNumber: EditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val TAG = "PhoneNumberActivity"

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Setup toolbar
        setupToolbar()

        // Initialize views
        initializeViews()

        // Load current phone number
        loadCurrentPhoneNumber()

        // Setup listeners
        setupListeners()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "My Phone Number"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initializeViews() {
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnSave = findViewById(R.id.btnSavePhone)
        btnCancel = findViewById(R.id.btnCancelPhone)
        progressBar = findViewById(R.id.phoneProgressBar)
    }

    private fun loadCurrentPhoneNumber() {
        auth.currentUser?.let { user ->
            progressBar.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userDoc = withContext(Dispatchers.IO) {
                        firestore.collection("users")
                            .document(user.uid)
                            .get()
                            .await()
                    }

                    val firebaseUser = userDoc.toObject(FirebaseUser::class.java)
                    firebaseUser?.phoneNumber?.let { phone ->
                        etPhoneNumber.setText(phone)
                    }

                    progressBar.visibility = View.GONE
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading phone number: ${e.message}")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            savePhoneNumber()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun savePhoneNumber() {
        val phoneNumber = etPhoneNumber.text.toString().trim()

        // Validate phone number
        if (phoneNumber.isEmpty()) {
            etPhoneNumber.error = "Please enter a phone number"
            return
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            etPhoneNumber.error = "Please enter a valid phone number"
            Toast.makeText(this, "Phone number should start with + and contain 10-15 digits", Toast.LENGTH_LONG).show()
            return
        }

        // Save to Firestore
        auth.currentUser?.let { user ->
            progressBar.visibility = View.VISIBLE
            btnSave.isEnabled = false

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(Dispatchers.IO) {
                        firestore.collection("users")
                            .document(user.uid)
                            .update(
                                mapOf(
                                    "phoneNumber" to phoneNumber,
                                    "updatedAt" to FieldValue.serverTimestamp()
                                )
                            )
                            .await()
                    }

                    Toast.makeText(this@PhoneNumberActivity, "Phone number saved successfully!", Toast.LENGTH_SHORT).show()

                    // Return result
                    setResult(RESULT_OK)
                    finish()

                } catch (e: Exception) {
                    Log.e(TAG, "Error saving phone number: ${e.message}")
                    Toast.makeText(this@PhoneNumberActivity, "Failed to save phone number", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    btnSave.isEnabled = true
                }
            }
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Basic validation: starts with + and has 10-15 digits
        val phoneRegex = "^\\+[1-9]\\d{9,14}$".toRegex()
        return phoneRegex.matches(phone)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
