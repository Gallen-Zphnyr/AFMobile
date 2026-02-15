package com.example.afmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.afmobile.R
import com.example.afmobile.data.CartItemWithProduct
import java.util.*

/**
 * Adapter for checkout items (read-only cart items)
 */
class CheckoutAdapter : ListAdapter<CartItemWithProduct, CheckoutAdapter.CheckoutViewHolder>(CheckoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout, parent, false)
        return CheckoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.checkoutProductImage)
        private val productName: TextView = itemView.findViewById(R.id.checkoutProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.checkoutProductPrice)
        private val productQuantity: TextView = itemView.findViewById(R.id.checkoutProductQuantity)
        private val productTotal: TextView = itemView.findViewById(R.id.checkoutProductTotal)

        fun bind(item: CartItemWithProduct) {
            productName.text = item.cartItem.productName
            productPrice.text = String.format(Locale.getDefault(), "₱%.2f", item.cartItem.productPrice)
            productQuantity.text = "Qty: ${item.cartItem.quantity}"
            productTotal.text = String.format(Locale.getDefault(), "₱%.2f", item.getTotalPrice())

            // Load image
            Glide.with(itemView.context)
                .load(item.cartItem.productImageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(productImage)
        }
    }

    class CheckoutDiffCallback : DiffUtil.ItemCallback<CartItemWithProduct>() {
        override fun areItemsTheSame(oldItem: CartItemWithProduct, newItem: CartItemWithProduct): Boolean {
            return oldItem.cartItem.id == newItem.cartItem.id
        }

        override fun areContentsTheSame(oldItem: CartItemWithProduct, newItem: CartItemWithProduct): Boolean {
            return oldItem == newItem
        }
    }
}
