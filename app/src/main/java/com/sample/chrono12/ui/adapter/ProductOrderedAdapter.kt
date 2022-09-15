package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.data.entities.relations.OrderedProductInfo
import com.sample.chrono12.databinding.ProductRvItemBinding

class ProductOrderedAdapter(
    val onProductClickListener: ProductListAdapter.OnClickProduct
) : RecyclerView.Adapter<ProductOrderedAdapter.ProductOrderedViewHolder>() {

    private lateinit var productOrderedList: List<OrderedProductInfo>

    fun setData(productList: List<OrderedProductInfo>) {
        productOrderedList = productList
    }

    inner class ProductOrderedViewHolder(val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productName = binding.tvItemProductName
        private val brandName = binding.tvItemProductBrand
        private val rating = binding.rbItemRating
        private val currentPrice = binding.tvItemCurrentPrice
        private val originalPrice = binding.tvItemOriginalPrice
        private val offerPercent = binding.tvItemOffPercent
        private val productQuantity = binding.tvItemQuantity
        val productImage = binding.ivItemProductImage
        fun bind(orderedProductInfo: OrderedProductInfo) {
            val productWithBrandAndImages = orderedProductInfo.productWithBrandAndImages
            val product = productWithBrandAndImages.productWithBrand.product
            val brand = productWithBrandAndImages.productWithBrand.brand
            val image = productWithBrandAndImages.images[0].imageUrl
            binding.root.setOnClickListener { onProductClickListener.onClick(product) }
            productImage.load(image)
            with(productQuantity) {
                visibility = View.VISIBLE
                text = "Quantity : " + orderedProductInfo.productOrdered.quantity
            }
            productName.text = product.name
            brandName.text = brand.brandName
            currentPrice.text = "₹" + product.currentPrice.toInt().toString()
            rating.rating = product.totalRating!!
            if (product.originalPrice == product.currentPrice) {
                originalPrice.visibility = View.GONE
                offerPercent.visibility = View.GONE
            } else {
                originalPrice.apply {
                    text = "₹" + product.originalPrice.toInt().toString()
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                offerPercent.text =
                    "${((product.originalPrice - product.currentPrice) / product.originalPrice * 100).toInt()}% Off"
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductOrderedViewHolder {
        val binding = ProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductOrderedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductOrderedViewHolder, position: Int) {
        holder.bind(productOrderedList[position])
    }

    override fun getItemCount(): Int {
        return productOrderedList.size
    }
}