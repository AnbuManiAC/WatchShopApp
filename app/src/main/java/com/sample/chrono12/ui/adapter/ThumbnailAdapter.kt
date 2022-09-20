package com.sample.chrono12.ui.adapter


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.sample.chrono12.R
import com.sample.chrono12.data.models.ImageKey
import com.sample.chrono12.databinding.ImageThumbnailItemBinding
import com.sample.chrono12.utils.ImageUtil
import java.io.InputStream
import java.net.URL

class ThumbnailAdapter(
    private val imageList: List<String>,
    val onThumbnailClick: (String) -> Unit
): RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder>() {

    private var selectedPosition: Int = 0

    fun updateSelectedPosition(position: Int){
        selectedPosition = position
        notifyDataSetChanged()
    }

    inner class ThumbnailViewHolder(val binding: ImageThumbnailItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String) {
            ImageUtil.loadImage(image,binding.ivThumbnail, ImageKey.SMALL)
            binding.clThumbnail.setOnClickListener {
                onThumbnailClick(image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val binding = ImageThumbnailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.bind(imageList[position])
        if(selectedPosition == position) {
            holder.binding.clThumbnail.setBackgroundResource(R.drawable.selected_border)
        } else {
            holder.binding.clThumbnail.setBackgroundResource(R.drawable.default_border)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}