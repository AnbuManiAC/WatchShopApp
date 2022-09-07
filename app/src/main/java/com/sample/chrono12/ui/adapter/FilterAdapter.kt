package com.sample.chrono12.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.databinding.FilterRvItemBinding

class FilterAdapter(
    private val filters: List<String>,
    private val onFilterClickListener: OnClickFilter
): RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private var selectedPosition = 0

    inner class FilterViewHolder(val binding: FilterRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvFilterTitle = binding.tvFilterTitle
        fun bind(filterTitle: String, position: Int){
            tvFilterTitle.text = filterTitle
            tvFilterTitle.setBackgroundColor(Color.TRANSPARENT)
            tvFilterTitle.setOnClickListener {
                onFilterClickListener.onClick(filters[position])
                selectedPosition = position
                notifyDataSetChanged()
            }
            if(selectedPosition == position) binding.root.setBackgroundColor(Color.WHITE)
            else binding.root.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    interface OnClickFilter {
        fun onClick(filter: String)
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