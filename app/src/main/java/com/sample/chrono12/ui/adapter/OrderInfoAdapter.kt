package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Order
import com.sample.chrono12.databinding.AddressOrderInfoRvItemBinding

class OrderInfoAdapter: RecyclerView.Adapter<OrderInfoAdapter.OrderInfoViewHolder>(){

    private lateinit var orderInfoList: List<Order>

    fun setData(orderList: List<Order>){
        orderInfoList = orderList
    }

    inner class OrderInfoViewHolder(val binding: AddressOrderInfoRvItemBinding): RecyclerView.ViewHolder(binding.root){
        val orderId = binding.tvIndividualOrderId
        val originalPrice = binding.tvOrderOriginalPrice
        val currentPrice = binding.tvOrderCurrentPrice
        val address = binding.tvAddress
        val orderStatus = binding.tvOrderStatus
        fun bind(order: Order){
            orderId.text = binding.root.context.getString(R.string.order_id,order.orderId)
            address.text = order.addressInfo
            orderStatus.text = order.orderStatus
            currentPrice.text = "₹"+order.totalPrice.toInt().toString()
            originalPrice.apply {
                text = "₹"+order.actualTotal.toInt().toString()
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderInfoViewHolder {
        val binding = AddressOrderInfoRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderInfoViewHolder, position: Int) {
        holder.bind(orderInfoList[position])
    }

    override fun getItemCount(): Int {
        return orderInfoList.size
    }
}