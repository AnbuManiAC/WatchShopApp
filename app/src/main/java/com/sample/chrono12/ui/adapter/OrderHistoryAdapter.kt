package com.sample.chrono12.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.data.models.OrderInfo
import com.sample.chrono12.databinding.OrderHistoryRvItemBinding
import com.sample.chrono12.utils.DateUtil

class OrderHistoryAdapter(
    private val onOrderClickListener: OnClickOrder
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    private lateinit var orderHistoryList: List<OrderInfo>

    fun setData(orderInfo: List<OrderInfo>){
        orderHistoryList = orderInfo
    }

    inner class OrderHistoryViewHolder(val binding: OrderHistoryRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvOrderId = binding.tvOrderId
        private val tvOrderDateTime = binding.tvOrderDateTime
        private val tvOrderPrice = binding.tvOrderPrice
        private val tvNumOfProducts = binding.tvNumberOfProducts

        fun bind(order: OrderInfo) {
            if (order.orderCount > 1)
                tvOrderId.text = binding.root.context.getString(R.string.bulk_ord_id,order.bulkOrderId)
            else
                tvOrderId.text = binding.root.context.getString(R.string.order_id,order.orderId)
            tvOrderDateTime.text = binding.root.context.getString(R.string.date_time,DateUtil.getDateAndTime(order.timestamp))
            tvOrderPrice.text = binding.root.context.getString(R.string.total_price,order.currentPrice)
            tvNumOfProducts.text = binding.root.context.getString(R.string.no_of_products,order.productCount)
            binding.root.setOnClickListener { onOrderClickListener.onClick(order.bulkOrderId, order.orderId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val binding = OrderHistoryRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.bind(orderHistoryList[position])
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    interface OnClickOrder {
        fun onClick(bulkOrderId: Int, orderId: Int)
    }
}