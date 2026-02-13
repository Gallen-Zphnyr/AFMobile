package com.example.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        val email = sharedPreferences.getString("email", "user@email.com")
        val userAddress = sharedPreferences.getString("address", "Lipa, Batangas") ?: "Lipa, Batangas"

        findViewById<TextView>(R.id.profile_name).text = username
        findViewById<TextView>(R.id.profile_email).text = email

        findViewById<RelativeLayout>(R.id.your_orders_layout).setOnClickListener {
            startActivity(Intent(this, MyOrdersActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.my_cart_layout).setOnClickListener {
            startActivity(Intent(this, MyCartActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.my_address_layout).setOnClickListener {
            showAddressDialog(userAddress)
        }

        findViewById<RelativeLayout>(R.id.payment_methods_layout).setOnClickListener {
            Toast.makeText(this, "Payment Methods Clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<RelativeLayout>(R.id.settings_layout).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.help_support_layout).setOnClickListener {
            showHelpSupportDialog()
        }
    }

    private fun showAddressDialog(address: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_my_address, null)
        val tvDisplayAddress = dialogView.findViewById<TextView>(R.id.tvDisplayAddress)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        tvDisplayAddress.text = address

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showHelpSupportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_help_support, null)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}