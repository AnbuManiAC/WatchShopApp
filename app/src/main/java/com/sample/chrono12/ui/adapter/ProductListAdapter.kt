package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.models.ImageKey
import com.sample.chrono12.databinding.ProductRvItemBinding
import com.sample.chrono12.utils.ImageUtil

class ProductListAdapter(
    private val onClickListener: OnClickProduct
) : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {

    private lateinit var products: MutableList<ProductWithBrandAndImages>

    inner class ProductListViewHolder(val binding: ProductRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val itemName = binding.tvItemProductName
        private val itemBrand = binding.tvItemProductBrand
        private val itemCurrentPrice = binding.tvItemCurrentPrice
        private val itemOriginalPrice = binding.tvItemOriginalPrice
        private val itemOffPercent = binding.tvItemOffPercent
        private val itemRating = binding.rbItemRating
        private val itemImage = binding.ivItemProductImage
        fun bind(productWithBrandAndImages: ProductWithBrandAndImages) {
            val product = productWithBrandAndImages.productWithBrand.product
            val brand = productWithBrandAndImages.productWithBrand.brand
            val image = productWithBrandAndImages.images[0].imageUrl
            binding.root.setOnClickListener { onClickListener.onClick(product) }
            ImageUtil.loadImage(image, itemImage, ImageKey.MEDIUM)
            itemName.text = product.name
            itemBrand.text = brand.brandName
            itemCurrentPrice.text = "₹" + product.currentPrice.toInt().toString()
            itemRating.rating = product.totalRating!!
            if (product.originalPrice == product.currentPrice) {
                itemOriginalPrice.visibility = View.GONE
                itemOffPercent.visibility = View.GONE
            } else {
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
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun interface OnClickProduct {
        fun onClick(product: Product)
    }

    class DiffUtilCallback(private val oldList: List<ProductWithBrandAndImages>, private val newList: List<ProductWithBrandAndImages>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.productWithBrand.product.productId == newItem.productWithBrand.product.productId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setData(data: List<ProductWithBrandAndImages>) {
        this.products = data.toMutableList()
    }

    fun setNewData(newData: List<ProductWithBrandAndImages>) {
        val diffCallback = DiffUtilCallback(products, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }
}