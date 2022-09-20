package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.models.ImageKey
import com.sample.chrono12.databinding.AddressOrderInfoRvItemBinding
import com.sample.chrono12.databinding.CartRvItemBinding
import com.sample.chrono12.databinding.CategoriesRvItemBinding
import com.sample.chrono12.utils.ImageUtil

class CategoriesAdapter(
    private val categories: List<SubCategory>,
    private val onClickListener: OnClickCategory
): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>()  {

    inner class CategoryViewHolder(val binding: CategoriesRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvCategoryName = binding.tvCategoryName
        private val ivCategoryImage = binding.ivCategory

        fun bind(category: SubCategory){
            binding.root.setOnClickListener { onClickListener.onClick(category) }
            tvCategoryName.text = category.name
            ImageUtil.loadImage(category.imageUrl, ivCategoryImage, ImageKey.SMALL)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoriesRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun interface OnClickCategory{
        fun onClick(category: SubCategory)
    }
}