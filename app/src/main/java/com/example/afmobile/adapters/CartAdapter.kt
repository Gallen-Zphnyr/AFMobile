package com.example.afmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afmobile.R
import com.example.afmobile.data.CartItemWithProduct
import com.google.android.material.button.MaterialButton
import java.util.Locale

/**
 * RecyclerView Adapter for Cart Items
 */
class CartAdapter(
    private val onQuantityChange: (String, Int) -> Unit,
    private val onRemoveClick: (String) -> Unit
) : ListAdapter<CartItemWithProduct, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view, onQuantityChange, onRemoveClick)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CartViewHolder(
        itemView: View,
        private val onQuantityChange: (String, Int) -> Unit,
        private val onRemoveClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        private val productName: TextView = itemView.findViewById(R.id.cartItemName)
        private val productPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        private val btnDecrease: MaterialButton = itemView.findViewById(R.id.btnCartDecrease)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        private val btnIncrease: MaterialButton = itemView.findViewById(R.id.btnCartIncrease)
        private val tvItemTotal: TextView = itemView.findViewById(R.id.tvCartItemTotal)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnCartRemove)
        private val tvStockWarning: TextView = itemView.findViewById(R.id.tvStockWarning)

        fun bind(item: CartItemWithProduct) {
            val cartItem = item.cartItem
            val product = item.product

            // Set product details
            productName.text = cartItem.productName
            productPrice.text = String.format(Locale.getDefault(), "₱%.2f", cartItem.productPrice)
            tvQuantity.text = cartItem.quantity.toString()
            tvItemTotal.text = String.format(Locale.getDefault(), "₱%.2f", item.getTotalPrice())

            // Load product image
            Glide.with(itemView.context)
                .load(cartItem.productImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(productImage)

            // Check stock availability
            val stockLevel = product?.stockLevel ?: 0
            val isAvailable = item.isAvailable()

            if (!isAvailable) {
                if (stockLevel == 0) {
                    tvStockWarning.text = "Out of stock"
                } else {
                    tvStockWarning.text = "Only $stockLevel available"
                }
                tvStockWarning.visibility = View.VISIBLE
                btnIncrease.isEnabled = false
            } else if (cartItem.quantity >= stockLevel) {
                tvStockWarning.text = "Max stock reached"
                tvStockWarning.visibility = View.VISIBLE
                btnIncrease.isEnabled = false
            } else {
                tvStockWarning.visibility = View.GONE
                btnIncrease.isEnabled = true
            }

            // Decrease button
            btnDecrease.isEnabled = cartItem.quantity > 1
            btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onQuantityChange(cartItem.id, cartItem.quantity - 1)
                }
            }

            // Increase button
            btnIncrease.setOnClickListener {
                if (stockLevel > 0 && cartItem.quantity < stockLevel) {
                    onQuantityChange(cartItem.id, cartItem.quantity + 1)
                }
            }

            // Remove button
            btnRemove.setOnClickListener {
                onRemoveClick(cartItem.id)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemWithProduct>() {
        override fun areItemsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem.cartItem.id == newItem.cartItem.id
        }

        override fun areContentsTheSame(
            oldItem: CartItemWithProduct,
            newItem: CartItemWithProduct
        ): Boolean {
            return oldItem == newItem
        }
    }
}
