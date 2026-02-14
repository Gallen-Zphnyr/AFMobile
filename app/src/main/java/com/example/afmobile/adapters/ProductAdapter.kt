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
import com.example.afmobile.data.Product

/**
 * RecyclerView Adapter for Products
 */
class ProductAdapter(
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view, onProductClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(
        itemView: View,
        private val onProductClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productCategory: TextView = itemView.findViewById(R.id.productCategory)
        private val productStock: TextView = itemView.findViewById(R.id.productStock)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = String.format("₱%.2f", product.price)
            productCategory.text = product.category
            productStock.text = "Stock: ${product.stockLevel}"

            // Load image with Glide
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(productImage)

            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
