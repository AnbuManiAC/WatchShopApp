package com.sample.chrono12.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.databinding.FilterValueRvItemBinding

class FilterValuesAdapter(val filterCheckedListener: OnFilterCheckedListener) :
    RecyclerView.Adapter<FilterValuesAdapter.FilterValueViewModel>() {

    private var filterValues = HashMap<Int, String>()
    private var selectedFilterIds = setOf<Int>()

    fun setData(filterValuesMap: HashMap<Int, String>) {
        filterValues = filterValuesMap
    }

    fun clearCheckBox() {
        selectedFilterIds = emptySet()
    }

    inner class FilterValueViewModel(val binding: FilterValueRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvFilterValue = binding.cbFilterValue
        fun bind(key: Int, value: String) {
            tvFilterValue.text = value
            tvFilterValue.isChecked = selectedFilterIds.contains(key)

            tvFilterValue.setOnCheckedChangeListener { button, isChecked ->
                if (button.isPressed) {
                    filterCheckedListener.onChecked(key, isChecked)
                }
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

    fun setSelectedFilterIds(selectedIds: Set<Int>) {
        selectedFilterIds = selectedIds
    }

    fun interface OnFilterCheckedListener {
        fun onChecked(filterId: Int, isChecked: Boolean)
    }
}