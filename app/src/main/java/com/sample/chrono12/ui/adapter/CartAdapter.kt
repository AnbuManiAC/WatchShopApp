package com.sample.chrono12.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.WishListWithProductInfo
import com.sample.chrono12.databinding.CartRvItemBinding

class CartAdapter(
    val onProductClickListener: ProductListAdapter.OnClickProduct,
    val onDeleteClickListener: OnClickDelete,
    val onQuantityClickListener: OnClickQuantity

) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private lateinit var cartWithProductInfoList: MutableList<CartWithProductInfo>

    private var hideDeleteButton = false
    fun setHideDeleteButton(status: Boolean) {
        hideDeleteButton = status
    }

    inner class CartViewHolder(val binding: CartRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val productName = binding.tvItemProductName
        private val brandName = binding.tvItemProductBrand
        private val rating = binding.rbItemRating
        private val currentPrice = binding.tvItemCurrentPrice
        private val originalPrice = binding.tvItemOriginalPrice
        private val offerPercent = binding.tvItemOffPercent
        private val productImage = binding.ivItemProductImage
        private val productQuantity = binding.tvQuantity
        private val btnQuantityPlus = binding.btnIncQuantity
        private val btnQuantityMinus = binding.btnDecQuantity

        fun bindProduct(productWithBrandAndImages: ProductWithBrandAndImages) {
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

            if (hideDeleteButton) binding.btnDelete.visibility = View.GONE

        }

        fun bindProductQuantity(product: Product, quantity: Int) {
            productQuantity.text = quantity.toString()
            if (productQuantity.text.toString().toInt() == 1) {
                btnQuantityMinus.alpha = 0.7F
                btnQuantityMinus.isEnabled = false
            }
            btnQuantityMinus.setOnClickListener {
                val currentValue = productQuantity.text.toString().toInt()
                if (onQuantityClickListener.onClickMinus(
                        product,
                        currentValue
                    )
                ) {
                    productQuantity.text = (currentValue - 1).toString()

                } else {
                    btnQuantityMinus.alpha = 0.7F
                    btnQuantityMinus.isEnabled = false
                }
            }
            btnQuantityPlus.setOnClickListener {
                val currentValue = productQuantity.text.toString().toInt()
                if (onQuantityClickListener.onClickPlus(
                        product,
                        quantity
                    )
                ) {
                    productQuantity.text = (currentValue + 1).toString()
                }
            }
            productQuantity.setOnClickListener(null)

            binding.btnDelete.setOnClickListener {
                onDeleteClickListener.onDelete(product.productId, quantity)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bindProduct(cartWithProductInfoList[position].productWithBrandAndImagesList)
        holder.bindProductQuantity(
            cartWithProductInfoList[position].productWithBrandAndImagesList.productWithBrand.product,
            cartWithProductInfoList[position].cart.quantity
        )
    }

    override fun getItemCount(): Int {
        return cartWithProductInfoList.size
    }

    interface OnClickDelete {
        fun onDelete(productId: Int, quantity: Int)
    }

    interface OnClickQuantity {
        fun onClickPlus(product: Product, quantity: Int): Boolean
        fun onClickMinus(product: Product, quantity: Int): Boolean
    }

    class DiffUtilCallback(
        private val oldList: List<CartWithProductInfo>,
        private val newList: List<CartWithProductInfo>
    ) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.cart.cartId == newItem.cart.cartId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setData(data: List<CartWithProductInfo>) {
        this.cartWithProductInfoList = data.toMutableList()
    }

    fun setNewData(newData: List<CartWithProductInfo>) {
        val diffCallback = DiffUtilCallback(cartWithProductInfoList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cartWithProductInfoList.clear()
        cartWithProductInfoList.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

}
