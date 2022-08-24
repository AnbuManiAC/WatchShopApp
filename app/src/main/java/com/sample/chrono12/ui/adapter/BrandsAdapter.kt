package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.SubCategory

class BrandsAdapter(
    private val categories: List<ProductBrand>,
    private val onClickListener: OnClickBrand
): RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>()  {

    inner class BrandViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val tvCategoryName = view.findViewById<TextView>(R.id.tvCategoryName)
        val ivCategoryImage = view.findViewById<ImageView>(R.id.ivCategory)

        fun bind(brand: ProductBrand){
            view.setOnClickListener { onClickListener.onClick(brand) }
            tvCategoryName.text = brand.brandName
            ivCategoryImage.load(brand.brandImageUrl){
                crossfade(true)
                placeholder(R.mipmap.ic_app_icon_round)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categories_rv_item, parent, false)
        return BrandViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    fun interface OnClickBrand{
        fun onClick(brand: ProductBrand)
    }
}