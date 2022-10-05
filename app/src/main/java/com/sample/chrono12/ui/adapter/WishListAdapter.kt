package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.databinding.WishlistRvItemBinding

class WishListAdapter(
    val onProductClickListener: ProductListAdapter.OnClickProduct,
    val onDeleteClickListener: OnClickDelete,
    val onAddToCartClickListener: OnClickAddToCart

) : RecyclerView.Adapter<WishListAdapter.WishListViewHolder>() {

    private lateinit var wishListWithProductInfoList: MutableList<WishListWithProductInfo>

    inner class WishListViewHolder(val binding: WishlistRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productName = binding.tvItemProductName
        private val brandName = binding.tvItemProductBrand
        private val rating = binding.rbItemRating
        private val currentPrice = binding.tvItemCurrentPrice
        private val originalPrice = binding.tvItemOriginalPrice
        private val offerPercent = binding.tvItemOffPercent
        val productImage = binding.ivItemProductImage
        fun bind(productWithBrandAndImages: ProductWithBrandAndImages) {
            val product = productWithBrandAndImages.productWithBrand.product
            val brand = productWithBrandAndImages.productWithBrand.brand
            val image = productWithBrandAndImages.images[0].imageUrl
            binding.root.setOnClickListener { onProductClickListener.onClick(product) }
            productImage.load(image)
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
            binding.btnDelete.setOnClickListener {
                onDeleteClickListener.onDelete(product.productId)

            }
        }

        fun bindAddToCartButton(productId: Int) {

            onAddToCartClickListener.initButton(binding.btnAddToCart, productId)

            binding.btnAddToCart.setOnClickListener {
                onAddToCartClickListener.onClickAdd(it as MaterialButton, productId)
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
        holder.bindAddToCartButton(wishListWithProductInfoList[position].productWithBrandAndImagesList.productWithBrand.product.productId)
    }

    override fun getItemCount(): Int {
        return wishListWithProductInfoList.size
    }

    interface OnClickDelete {
        fun onDelete(productId: Int)
    }

    interface OnClickAddToCart {
        fun initButton(button: MaterialButton, productId: Int)
        fun onClickAdd(button: MaterialButton, productId: Int)
    }

    class DiffUtilCallback(
        private val oldList: List<WishListWithProductInfo>,
        private val newList: List<WishListWithProductInfo>
    ) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.wishList.wishListId == newItem.wishList.wishListId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setData(data: List<WishListWithProductInfo>) {
        this.wishListWithProductInfoList = data.toMutableList()
    }

    fun setNewData(newData: List<WishListWithProductInfo>) {
        val diffCallback = DiffUtilCallback(wishListWithProductInfoList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        wishListWithProductInfoList.clear()
        wishListWithProductInfoList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }
}


