package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.databinding.ProductRvItemBinding
import java.net.URL

class ProductListAdapter(
    private val productWithBrandAndImagesList: List<ProductWithBrandAndImages>,
    private val onClickListener: OnClickProduct
):  RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>(){

    inner class ProductListViewHolder(val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        val itemName = binding.tvItemProductName
        val itembrand = binding.tvItemProductBrand
        val itemCurrentPrice = binding.tvItemCurrentPrice
        val itemOriginalPrice = binding.tvItemOriginalPrice
        val itemOffPercent = binding.tvItemOffPercent
        val itemRating = binding.rbItemRating
        val itemImage = binding.ivItemProductImage
        fun bind(productWithBrandAndImages: ProductWithBrandAndImages){
            val product = productWithBrandAndImages.productWithBrand.product
            val brand = productWithBrandAndImages.productWithBrand.brand
            val image = productWithBrandAndImages.images[0].imageUrl
            binding.root.setOnClickListener { onClickListener.onClick(product) }
            itemImage.load(image)
            itemName.text = product.name
            itembrand.text = brand.brandName
            itemCurrentPrice.text = "₹"+product.currentPrice.toInt().toString()
            itemRating.rating = product.totalRating!!
            if(product.originalPrice==product.currentPrice){
                itemOriginalPrice.visibility = View.GONE
                itemOffPercent.visibility = View.GONE
            } else{
                itemOriginalPrice.apply {
                    text = "₹" + product.originalPrice.toInt().toString()
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                itemOffPercent.text =
                    "${((product.originalPrice - product.currentPrice) / product.originalPrice * 100).toInt()}% Off"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding = ProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.bind(productWithBrandAndImagesList[position])
    }

    override fun getItemCount(): Int {
        return productWithBrandAndImagesList.size
    }

    fun interface OnClickProduct{
        fun onClick(product: Product)
    }
}