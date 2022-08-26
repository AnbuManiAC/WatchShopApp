package com.sample.chrono12.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import coil.load
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.utils.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class CategoriesAdapter(
    private val categories: List<SubCategory>,
    private val onClickListener: OnClickCategory
): RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>()  {

    inner class CategoryViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val tvCategoryName = view.findViewById<TextView>(R.id.tvCategoryName)
        val ivCategoryImage = view.findViewById<ImageView>(R.id.ivCategory)

        fun bind(category: SubCategory){
            view.setOnClickListener { onClickListener.onClick(category) }
            tvCategoryName.text = category.name
            ivCategoryImage.load(category.imageUrl){
                crossfade(true)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories_rv_item, parent, false)
        return CategoryViewHolder(view)
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