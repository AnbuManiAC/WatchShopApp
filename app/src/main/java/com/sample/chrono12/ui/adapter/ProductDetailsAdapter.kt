package com.sample.chrono12.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.databinding.ProductDetailsRvItemBinding

class ProductDetailsAdapter(
    private val productDetail: List<ProductDetail>
): RecyclerView.Adapter<ProductDetailsAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(private val binding: ProductDetailsRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvDetailContent = binding.tvDetailContent
        private val tvDetailTitle = binding.tvDetailTitle
        fun bind(productDetail: ProductDetail){
            tvDetailTitle.text = productDetail.title
            tvDetailContent.text = productDetail.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = ProductDetailsRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(productDetail[position])
    }

    override fun getItemCount(): Int {
        return productDetail.size
    }
}