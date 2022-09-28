package com.sample.chrono12.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FilterRvItemBinding
import java.util.*

class FilterAdapter(
    private val filters: List<String>,
    private val onFilterClickListener: OnClickFilter,
    private val onFilterChangeListener: OnFilterChangeListener
): RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private var selectedPosition = 0

    fun setSelectedPosition(position: Int){
        selectedPosition = position
        onFilterClickListener.onClick(filters[position])
    }

    inner class FilterViewHolder(val binding: FilterRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvFilterTitle = binding.tvFilterTitle
        fun bind(filterTitle: String, position: Int){
            tvFilterTitle.text = filterTitle
            tvFilterTitle.setBackgroundColor(Color.TRANSPARENT)
            tvFilterTitle.setOnClickListener {
                onFilterClickListener.onClick(filters[position])
                onFilterChangeListener.onChanged(position)
                notifyDataSetChanged()
            }
            if(selectedPosition == position){
                binding.root.setBackgroundColor(ResourcesCompat.getColor(binding.root.resources, R.color.filterBackGround, binding.root.context.theme))
            }
            else binding.root.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    interface OnClickFilter {
        fun onClick(filter: String)
    }

    fun interface OnFilterChangeListener {
        fun onChanged(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = FilterRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position], position)
    }

    override fun getItemCount(): Int {
        return filters.size
    }
}