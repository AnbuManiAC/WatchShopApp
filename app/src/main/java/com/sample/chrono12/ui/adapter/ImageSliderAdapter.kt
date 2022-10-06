package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.data.models.ImageKey
import com.sample.chrono12.databinding.ImageSliderItemBinding
import com.sample.chrono12.utils.ImageUtil

class ImageSliderAdapter(
    private val imageList: List<String>
): RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    class ImageViewHolder(private val binding: ImageSliderItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(image: String){
            binding.ivImages.load(image)
            ImageUtil.loadImage(image,binding.ivImages, ImageKey.LARGE)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageSliderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}