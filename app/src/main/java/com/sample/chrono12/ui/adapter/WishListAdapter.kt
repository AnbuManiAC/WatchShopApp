package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.databinding.WishlistRvItemBinding

class WishListAdapter(
    val wishListWithProductInfoList: List<WishListWithProductInfo>,
    val onProductClickListerner: ProductListAdapter.OnClickProduct
): RecyclerView.Adapter<WishListAdapter.WishListViewHolder>() {

    inner class WishListViewHolder(val binding: WishlistRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val productName = binding.tvItemProductName
        private val brandName = binding.tvItemProductBrand
        private val rating = binding.rbItemRating
        private val currentPrice = binding.tvItemCurrentPrice
        private val originalPrice = binding.tvItemOriginalPrice
        private val offerPercent = binding.tvItemOffPercent
        val productImage = binding.ivItemProductImage
        fun bind(productWithBrandAndImages: ProductWithBrandAndImages){
            val product = productWithBrandAndImages.productWithBrand.product
            val brand = productWithBrandAndImages.productWithBrand.brand
            val image = productWithBrandAndImages.images[0].imageUrl
            binding.root.setOnClickListener { onProductClickListerner.onClick(product) }
            productImage.load(image)
            productName.text = product.name
            brandName.text = brand.brandName
            currentPrice.text = "₹"+product.currentPrice.toInt().toString()
            rating.rating = product.totalRating!!
            if(product.originalPrice==product.currentPrice){
                originalPrice.visibility = View.GONE
                offerPercent.visibility = View.GONE
            } else{
                originalPrice.apply {
                    text = "₹" + product.originalPrice.toInt().toString()
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                offerPercent.text =
                    "${((product.originalPrice - product.currentPrice) / product.originalPrice * 100).toInt()}% Off"
            }
            if(product.stockCount<=0){
                binding.btnAddToCart.visibility = View.GONE
            }
            else{
                binding.btnAddToCart.setOnClickListener {
                    Toast.makeText(binding.root.context, "Added to Cart clicked", Toast.LENGTH_SHORT).show()
                }
            }
            binding.btnDelete.setOnClickListener {
                Toast.makeText(binding.root.context, "Delete button clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishListViewHolder {
        val binding = WishlistRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WishListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishListViewHolder, position: Int) {
        holder.bind(wishListWithProductInfoList[position].productWithBrandAndImagesList)
    }

    override fun getItemCount(): Int {
        return wishListWithProductInfoList.size
    }


}
