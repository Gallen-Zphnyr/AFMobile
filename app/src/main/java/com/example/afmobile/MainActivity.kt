package com.example.afmobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        // Initialize Firebase Auth and Functions
        auth = Firebase.auth
        functions = Firebase.functions

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Login UI elements
        val signUpText = findViewById<TextView>(R.id.signUpText)
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPasswordText)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Sign Up Overlay elements
        val signUpLayout = findViewById<View>(R.id.signUpLayout)
        val overlayBackground = findViewById<View>(R.id.overlayBackground)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        
        val regUsername = findViewById<EditText>(R.id.regUsername)
        val regEmail = findViewById<EditText>(R.id.regEmail)
        val regPassword = findViewById<EditText>(R.id.regPassword)
        val regRePassword = findViewById<EditText>(R.id.regRePassword)

        // Login Action
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple clicks
            btnLogin.isEnabled = false

            // Sign in with Firebase
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    Toast.makeText(this@MainActivity, "Welcome back!", Toast.LENGTH_SHORT).show()
                    // Navigate to Home Activity
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    btnLogin.isEnabled = true
                }
            }
        }

        // Show Sign Up Overlay
        signUpText.setOnClickListener {
            signUpLayout.visibility = View.VISIBLE
            overlayBackground.visibility = View.VISIBLE
        }

        // Hide Sign Up Overlay
        btnCancel.setOnClickListener {
            signUpLayout.visibility = View.GONE
            overlayBackground.visibility = View.GONE
        }

        // Sign Up Action
        btnSignUp.setOnClickListener {
            val username = regUsername.text.toString().trim()
            val email = regEmail.text.toString().trim()
            val password = regPassword.text.toString()
            val rePassword = regRePassword.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button to prevent multiple clicks
            btnSignUp.isEnabled = false

            // Create account with Firebase
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    // Create user with Firebase Auth
                    val result = auth.createUserWithEmailAndPassword(email, password).await()

                    // Call Cloud Function to create user profile
                    val data = hashMapOf(
                        "uid" to result.user?.uid,
                        "username" to username,
                        "email" to email
                    )

                    try {
                        functions.getHttpsCallable("createUserProfile")
                            .call(data)
                            .await()
                    } catch (e: Exception) {
                        // Profile creation failed, but auth succeeded
                        Toast.makeText(this@MainActivity, "Warning: Profile setup incomplete - ${e.message}", Toast.LENGTH_LONG).show()
                    }

                    Toast.makeText(this@MainActivity, "Account created for $username!", Toast.LENGTH_SHORT).show()
                    signUpLayout.visibility = View.GONE
                    overlayBackground.visibility = View.GONE

                    // Clear input fields
                    regUsername.text.clear()
                    regEmail.text.clear()
                    regPassword.text.clear()
                    regRePassword.text.clear()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Sign up failed: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    btnSignUp.isEnabled = true
                }
            }
        }

        // Forgot Password Action
        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Redirecting to password recovery...", Toast.LENGTH_SHORT).show()
        }
    }
}