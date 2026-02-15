package com.example.afmobile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.afmobile.adapters.CheckoutAdapter
import com.example.afmobile.data.CartItemWithProduct
import com.example.afmobile.data.FirebaseUser
import com.example.afmobile.viewmodels.CartViewModel
import com.example.afmobile.viewmodels.OrderViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var cartViewModel: CartViewModel
    private lateinit var orderViewModel: OrderViewModel

    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDeliveryName: TextView
    private lateinit var tvDeliveryAddress: TextView
    private lateinit var tvDeliveryPhone: TextView
    private lateinit var btnEditAddress: Button
    private lateinit var tvSubtotal: TextView
    private lateinit var tvDeliveryFee: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnPlaceOrder: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var cartItems: List<CartItemWithProduct> = emptyList()
    private var userName: String = ""
    private var userEmail: String = ""
    private var deliveryAddress: String = ""
    private var phoneNumber: String = ""

    private val DELIVERY_FEE = 50.0

    private val TAG = "CheckoutActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize ViewModels
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        // Initialize views
        initializeViews()

        // Setup toolbar
        setupToolbar()

        // Load user data
        loadUserData()

        // Load cart items
        loadCartItems()

        // Setup listeners
        setupListeners()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.checkoutRecyclerView)
        tvDeliveryName = findViewById(R.id.tvDeliveryName)
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)
        tvDeliveryPhone = findViewById(R.id.tvDeliveryPhone)
        btnEditAddress = findViewById(R.id.btnEditAddress)
        tvSubtotal = findViewById(R.id.tvCheckoutSubtotal)
        tvDeliveryFee = findViewById(R.id.tvCheckoutDeliveryFee)
        tvTotal = findViewById(R.id.tvCheckoutTotal)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        progressBar = findViewById(R.id.checkoutProgressBar)

        // Setup RecyclerView
        checkoutAdapter = CheckoutAdapter()
        recyclerView.adapter = checkoutAdapter
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Checkout"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun loadUserData() {
        auth.currentUser?.let { user ->
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userDoc = withContext(Dispatchers.IO) {
                        firestore.collection("users")
                            .document(user.uid)
                            .get()
                            .await()
                    }

                    val firebaseUser = userDoc.toObject(FirebaseUser::class.java)
                    firebaseUser?.let { userData ->
                        userName = userData.username
                        userEmail = userData.email
                        deliveryAddress = userData.address ?: "No address set"
                        phoneNumber = userData.phoneNumber ?: "No phone number"

                        updateDeliveryInfo()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading user data: ${e.message}")
                    Toast.makeText(this@CheckoutActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateDeliveryInfo() {
        tvDeliveryName.text = userName
        tvDeliveryAddress.text = deliveryAddress
        tvDeliveryPhone.text = phoneNumber

        // Validate address
        if (deliveryAddress.isEmpty() || deliveryAddress == "No address set") {
            btnPlaceOrder.isEnabled = false
            btnPlaceOrder.text = "Please set delivery address"
        } else {
            btnPlaceOrder.isEnabled = true
            btnPlaceOrder.text = "Place Order"
        }
    }

    private fun loadCartItems() {
        progressBar.visibility = View.VISIBLE

        cartViewModel.cartItemsWithProducts.observe(this) { items ->
            cartItems = items
            checkoutAdapter.submitList(items)
            updatePricing()
            progressBar.visibility = View.GONE
        }

        cartViewModel.loadCartItems()
    }

    private fun updatePricing() {
        val subtotal = cartItems.sumOf { it.getTotalPrice() }
        val total = subtotal + DELIVERY_FEE

        tvSubtotal.text = String.format(Locale.getDefault(), "₱%.2f", subtotal)
        tvDeliveryFee.text = String.format(Locale.getDefault(), "₱%.2f", DELIVERY_FEE)
        tvTotal.text = String.format(Locale.getDefault(), "₱%.2f", total)
    }

    private fun setupListeners() {
        btnEditAddress.setOnClickListener {
            // Navigate to address picker
            Toast.makeText(this, "Edit address from Profile page", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnPlaceOrder.setOnClickListener {
            if (validateCheckout()) {
                showOrderConfirmation()
            }
        }
    }

    private fun validateCheckout(): Boolean {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (deliveryAddress.isEmpty() || deliveryAddress == "No address set") {
            Toast.makeText(this, "Please set your delivery address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (phoneNumber.isEmpty() || phoneNumber == "No phone number") {
            Toast.makeText(this, "Please set your phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun showOrderConfirmation() {
        val total = cartItems.sumOf { it.getTotalPrice() } + DELIVERY_FEE

        AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage(
                "Total Amount: ₱%.2f\n\n".format(total) +
                "Delivery Address:\n$deliveryAddress\n\n" +
                "After placing the order, you will need to click 'Pay' to confirm payment."
            )
            .setPositiveButton("Place Order") { _, _ ->
                placeOrder()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun placeOrder() {
        progressBar.visibility = View.VISIBLE
        btnPlaceOrder.isEnabled = false

        orderViewModel.createOrder(
            cartItems = cartItems,
            deliveryAddress = deliveryAddress,
            phoneNumber = phoneNumber,
            userName = userName,
            userEmail = userEmail
        ) { result ->
            progressBar.visibility = View.GONE

            result.onSuccess { orderId ->
                // Order created successfully - show payment dialog
                showPaymentDialog(orderId)
            }.onFailure { exception ->
                btnPlaceOrder.isEnabled = true
                Toast.makeText(
                    this,
                    "Failed to create order: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPaymentDialog(orderId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_payment, null)
        val tvOrderId = dialogView.findViewById<TextView>(R.id.tvPaymentOrderId)
        val tvPaymentTotal = dialogView.findViewById<TextView>(R.id.tvPaymentTotal)
        val btnPay = dialogView.findViewById<MaterialButton>(R.id.btnPay)
        val btnCancelPayment = dialogView.findViewById<Button>(R.id.btnCancelPayment)

        val total = cartItems.sumOf { it.getTotalPrice() } + DELIVERY_FEE

        tvOrderId.text = "Order ID: $orderId"
        tvPaymentTotal.text = String.format(Locale.getDefault(), "₱%.2f", total)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnPay.setOnClickListener {
            dialog.dismiss()
            processPayment(orderId)
        }

        btnCancelPayment.setOnClickListener {
            dialog.dismiss()
            showPaymentCancellationDialog(orderId)
        }

        dialog.show()
    }

    private fun processPayment(orderId: String) {
        progressBar.visibility = View.VISIBLE

        orderViewModel.markOrderAsPaid(orderId) { result ->
            progressBar.visibility = View.GONE

            result.onSuccess {
                // Clear cart after successful payment
                clearCart()

                // Show success dialog
                showPaymentSuccessDialog()
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Payment failed: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun clearCart() {
        CoroutineScope(Dispatchers.Main).launch {
            cartViewModel.clearCart { success ->
                if (!success) {
                    Log.e(TAG, "Failed to clear cart after order")
                }
            }
        }
    }

    private fun showPaymentSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Payment Successful")
            .setMessage(
                "Your payment has been received!\n\n" +
                "Your order is now waiting for admin approval. " +
                "Once approved, it will be prepared for shipment.\n\n" +
                "You can track your order in the Orders tab."
            )
            .setPositiveButton("View Orders") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPaymentCancellationDialog(orderId: String) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Payment")
            .setMessage("Your order has been created but not paid. You can pay later from the Orders page.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
