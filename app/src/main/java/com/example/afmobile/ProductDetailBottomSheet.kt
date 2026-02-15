package com.example.afmobile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.afmobile.data.Product
import com.example.afmobile.viewmodels.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * Bottom Sheet Dialog for Product Details
 * Shows product information with quantity selector and purchase options
 */
class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var product: Product? = null
    private var quantity = 1

    private lateinit var cartViewModel: CartViewModel

    // Views
    private lateinit var btnClose: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productCategory: TextView
    private lateinit var productStock: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    private lateinit var btnDecrease: MaterialButton
    private lateinit var btnIncrease: MaterialButton
    private lateinit var tvQuantity: TextView
    private lateinit var tvStockAvailable: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnAddToCart: MaterialButton
    private lateinit var btnBuyNow: MaterialButton

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductDetailBottomSheet {
            val fragment = ProductDetailBottomSheet()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product as java.io.Serializable)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            product = it.getSerializable(ARG_PRODUCT) as? Product
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_product_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        // Initialize views
        initViews(view)

        // Populate product data
        product?.let { displayProduct(it) }

        // Setup click listeners
        setupClickListeners()
    }

    private fun initViews(view: View) {
        btnClose = view.findViewById(R.id.btnClose)
        productImage = view.findViewById(R.id.productDetailImage)
        productName = view.findViewById(R.id.productDetailName)
        productCategory = view.findViewById(R.id.productDetailCategory)
        productStock = view.findViewById(R.id.productDetailStock)
        productPrice = view.findViewById(R.id.productDetailPrice)
        productDescription = view.findViewById(R.id.productDetailDescription)
        btnDecrease = view.findViewById(R.id.btnDecrease)
        btnIncrease = view.findViewById(R.id.btnIncrease)
        tvQuantity = view.findViewById(R.id.tvQuantity)
        tvStockAvailable = view.findViewById(R.id.tvStockAvailable)
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice)
        btnAddToCart = view.findViewById(R.id.btnAddToCart)
        btnBuyNow = view.findViewById(R.id.btnBuyNow)
    }

    private fun displayProduct(product: Product) {
        // Load product image
        Glide.with(this)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(productImage)

        // Set product details
        productName.text = product.name
        productCategory.text = product.category
        productStock.text = "Stock: ${product.stockLevel}"
        productPrice.text = String.format(Locale.getDefault(), "₱%.2f", product.price)
        productDescription.text = product.description
        tvStockAvailable.text = "${product.stockLevel} available"

        // Set initial quantity
        quantity = 1
        tvQuantity.text = quantity.toString()
        updateTotalPrice()

        // Enable/disable buttons based on stock
        updateButtonStates()
    }

    private fun setupClickListeners() {
        // Close button
        btnClose.setOnClickListener {
            dismiss()
        }

        // Decrease quantity
        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
                updateTotalPrice()
                updateButtonStates()
            }
        }

        // Increase quantity
        btnIncrease.setOnClickListener {
            product?.let { prod ->
                if (quantity < prod.stockLevel) {
                    quantity++
                    tvQuantity.text = quantity.toString()
                    updateTotalPrice()
                    updateButtonStates()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Maximum stock reached",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Add to Cart button
        btnAddToCart.setOnClickListener {
            product?.let { prod ->
                if (quantity > prod.stockLevel) {
                    Toast.makeText(
                        requireContext(),
                        "Not enough stock available",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Add to cart via ViewModel
                cartViewModel.addToCart(prod, quantity) { success ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "Added ${quantity}x ${prod.name} to cart!",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to add to cart. Please sign in.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // Buy Now button
        btnBuyNow.setOnClickListener {
            product?.let { prod ->
                if (quantity > prod.stockLevel) {
                    Toast.makeText(
                        requireContext(),
                        "Not enough stock available",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // TODO: Navigate to checkout
                val totalPrice = prod.price * quantity
                Toast.makeText(
                    requireContext(),
                    "Buying ${quantity}x ${prod.name} for ₱%.2f".format(totalPrice),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }

    private fun updateTotalPrice() {
        product?.let { prod ->
            val totalPrice = prod.price * quantity
            tvTotalPrice.text = String.format(Locale.getDefault(), "₱%.2f", totalPrice)
        }
    }

    private fun updateButtonStates() {
        product?.let { prod ->
            // Disable decrease button if quantity is 1
            btnDecrease.isEnabled = quantity > 1

            // Disable increase button if quantity equals stock
            btnIncrease.isEnabled = quantity < prod.stockLevel

            // Disable buy/cart buttons if no stock
            val hasStock = prod.stockLevel > 0
            btnAddToCart.isEnabled = hasStock
            btnBuyNow.isEnabled = hasStock

            if (!hasStock) {
                btnAddToCart.text = "Out of Stock"
                btnBuyNow.text = "Out of Stock"
            } else {
                btnAddToCart.text = "Add to Cart"
                btnBuyNow.text = "Buy Now"
            }
        }
    }
}
