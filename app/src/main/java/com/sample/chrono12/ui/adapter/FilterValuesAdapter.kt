package com.sample.chrono12.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.databinding.FilterValueRvItemBinding

class FilterValuesAdapter : RecyclerView.Adapter<FilterValuesAdapter.FilterValueViewModel>() {

    private var filterValues = HashMap<Int, String>()
    private val selectedFilterIds = mutableSetOf<Int>()

    fun setData(filterValuesMap: HashMap<Int, String>){
        filterValues = filterValuesMap
    }

    fun clearCheckBox(){
        selectedFilterIds.clear()
    }

    fun getSelectedCheckBoxIds() = selectedFilterIds

    inner class FilterValueViewModel(val binding: FilterValueRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvFilterValue = binding.cbFilterValue
        fun bind(key: Int, value: String) {
            tvFilterValue.text = value
            tvFilterValue.isChecked = selectedFilterIds.contains(key)

            tvFilterValue.setOnCheckedChangeListener { button, isChecked ->
                if(button.isPressed){
                    if (isChecked) selectedFilterIds.add(key)
                    else selectedFilterIds.remove(key)
                }
                Log.d("SELECTED", selectedFilterIds.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterValueViewModel {
        val binding = FilterValueRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterValueViewModel(binding)
    }

    override fun onBindViewHolder(holder: FilterValueViewModel, position: Int) {
        val keys = filterValues.keys.toList()
        val values = filterValues.values.toList()
        holder.bind(keys[position], values[position])
    }

    override fun getItemCount(): Int {
        return filterValues.size
    }
}