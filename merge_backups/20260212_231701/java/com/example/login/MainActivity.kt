package com.example.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
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
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Sign Up Overlay elements
        val signUpLayout = findViewById<View>(R.id.signUpLayout)
        val overlayBackground = findViewById<View>(R.id.overlayBackground)
        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnCancelClose = findViewById<ImageView>(R.id.btnCancelClose)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        
        val regUsername = findViewById<EditText>(R.id.regUsername)
        val regEmail = findViewById<EditText>(R.id.regEmail)
        val regAddress = findViewById<EditText>(R.id.regAddress)
        val regPassword = findViewById<EditText>(R.id.regPassword)
        val regRePassword = findViewById<EditText>(R.id.regRePassword)
        
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Login Action
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            
            val savedEmail = sharedPreferences.getString("email", null)
            val savedPassword = sharedPreferences.getString("password", null)
            val savedAddress = sharedPreferences.getString("address", "Lipa, Batangas")

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            } else if (email == savedEmail && password == savedPassword) {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("USER_ADDRESS", savedAddress)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        // Show Sign Up Overlay
        signUpText.setOnClickListener {
            signUpLayout.visibility = View.VISIBLE
            overlayBackground.visibility = View.VISIBLE
        }

        // Helper to hide Sign Up Overlay
        fun hideSignUpOverlay() {
            signUpLayout.visibility = View.GONE
            overlayBackground.visibility = View.GONE
            // Clear fields when closing
            regUsername.text.clear()
            regEmail.text.clear()
            regAddress.text.clear()
            regPassword.text.clear()
            regRePassword.text.clear()
        }

        // Hide Sign Up Overlay
        btnCancel.setOnClickListener { hideSignUpOverlay() }
        btnCancelClose.setOnClickListener { hideSignUpOverlay() }
        overlayBackground.setOnClickListener { hideSignUpOverlay() }

        // Sign Up Action
        btnSignUp.setOnClickListener {
            val username = regUsername.text.toString().trim()
            val email = regEmail.text.toString().trim()
            val address = regAddress.text.toString().trim()
            val password = regPassword.text.toString().trim()
            val rePassword = regRePassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val editor = sharedPreferences.edit()
                editor.putString("username", username)
                editor.putString("email", email)
                editor.putString("address", address)
                editor.putString("password", password)
                editor.apply()
                
                Toast.makeText(this, "Account created for $username", Toast.LENGTH_SHORT).show()
                hideSignUpOverlay()
            }
        }
    }
}