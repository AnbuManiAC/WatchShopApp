package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.databinding.FragmentSearchBinding
import com.sample.chrono12.databinding.SuggestionRvItemBinding


class SuggestionAdapter(
    val onClickSuggestion: OnClickSuggestion
): RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    private lateinit var suggestions : MutableList<SearchSuggestion>

    inner class SuggestionViewHolder(binding: SuggestionRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val tvSuggestion = binding.tvSuggestion
        private val ibRemove = binding.ibRemove
        private val ivSearch = binding.ivSearchIcon

        fun bind(suggestionHistory: SearchSuggestion){
            tvSuggestion.text = suggestionHistory.suggestion
            if (suggestionHistory.userId!=null) {
                ivSearch.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources,R.drawable.ic_history,null))
                ibRemove.visibility = View.VISIBLE
            }else{
                ivSearch.setImageDrawable(ResourcesCompat.getDrawable(itemView.resources,R.drawable.ic_search_icon,null))
                ibRemove.visibility = View.GONE
            }
            ibRemove.setOnClickListener {
                onClickSuggestion.onClickRemove(suggestionHistory,adapterPosition)
            }
            tvSuggestion.setOnClickListener {
                onClickSuggestion.onClick(suggestionHistory)
            }
        }
    }

    fun removeSuggestion(position: Int){
        suggestions.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnClickSuggestion{
        fun onClick(suggestion: SearchSuggestion)

        fun onClickRemove(suggestionHistory: SearchSuggestion, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val binding = SuggestionRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class DiffUtilCallback(private val oldList: List<SearchSuggestion>, private val newList: List<SearchSuggestion>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.searchSuggestionId == newItem.searchSuggestionId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setData(data: List<SearchSuggestion>) {
        this.suggestions = data.toMutableList()
    }

    fun setNewData(newData: List<SearchSuggestion>) {
        val diffCallback = DiffUtilCallback(suggestions, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        suggestions.clear()
        suggestions.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }
}