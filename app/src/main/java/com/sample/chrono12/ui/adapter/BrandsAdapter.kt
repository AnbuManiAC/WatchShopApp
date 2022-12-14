package com.sample.chrono12.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.models.ImageKey
import com.sample.chrono12.databinding.CategoriesRvItemBinding
import com.sample.chrono12.utils.ImageUtil

class BrandsAdapter(
    private var brands: List<ProductBrand>,
    private val onClickListener: OnClickBrand
): RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>()  {

    inner class BrandViewHolder(val binding: CategoriesRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvCategoryName = binding.tvCategoryName
        private val ivCategoryImage = binding.ivCategory

        fun bind(brand: ProductBrand){
            binding.root.setOnClickListener { onClickListener.onClick(brand) }
            tvCategoryName.text = brand.brandName
            ImageUtil.loadImage(brand.brandImageUrl, ivCategoryImage, ImageKey.SMALL)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val binding = CategoriesRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(brands[position])
    }

    override fun getItemCount(): Int {
        return brands.size

    }

    fun interface OnClickBrand{
        fun onClick(brand: ProductBrand)
    }

     class DiffUtilCallback(private val oldList: List<ProductBrand>, private val newList: List<ProductBrand>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.brandId == newItem.brandId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setNewData(newData: List<ProductBrand>) {
        val diffCallback = DiffUtilCallback(brands, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        brands = newData
        diffResult.dispatchUpdatesTo(this)
    }
}