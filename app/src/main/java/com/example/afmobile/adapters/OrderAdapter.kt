package com.example.afmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.afmobile.R
import com.example.afmobile.data.Order
import com.example.afmobile.data.OrderStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying orders
 */
class OrderAdapter(
    private val onOrderClick: (Order) -> Unit,
    private val onPayClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view, onOrderClick, onPayClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        itemView: View,
        private val onOrderClick: (Order) -> Unit,
        private val onPayClick: (Order) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val cardView: MaterialCardView = itemView.findViewById(R.id.orderCardView)
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        private val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val tvOrderItems: TextView = itemView.findViewById(R.id.tvOrderItems)
        private val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        private val btnPayOrder: MaterialButton = itemView.findViewById(R.id.btnPayOrder)

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

        fun bind(order: Order) {
            tvOrderId.text = "Order #${order.id.take(8)}"
            tvOrderDate.text = dateFormat.format(Date(order.createdAt))
            tvOrderStatus.text = order.getStatusDescription()

            // Display item count
            val itemCount = order.items.sumOf { it.quantity }
            tvOrderItems.text = if (itemCount == 1) "1 item" else "$itemCount items"

            tvOrderTotal.text = String.format(Locale.getDefault(), "₱%.2f", order.totalAmount)

            // Set status color
            val statusColor = when (order.orderStatus) {
                OrderStatus.PENDING -> itemView.context.getColor(R.color.status_pending)
                OrderStatus.PAID -> itemView.context.getColor(R.color.status_paid)
                OrderStatus.APPROVED -> itemView.context.getColor(R.color.status_approved)
                OrderStatus.SHIPPED -> itemView.context.getColor(R.color.status_shipped)
                OrderStatus.DELIVERED -> itemView.context.getColor(R.color.status_delivered)
                OrderStatus.CANCELLED -> itemView.context.getColor(R.color.status_cancelled)
            }
            tvOrderStatus.setTextColor(statusColor)

            // Show pay button only if order can be paid
            if (order.canBePaid()) {
                btnPayOrder.visibility = View.VISIBLE
                btnPayOrder.setOnClickListener {
                    onPayClick(order)
                }
            } else {
                btnPayOrder.visibility = View.GONE
            }

            // Click on card to view details
            cardView.setOnClickListener {
                onOrderClick(order)
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}
