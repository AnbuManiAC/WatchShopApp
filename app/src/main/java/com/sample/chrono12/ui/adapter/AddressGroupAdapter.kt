package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.AddressGroupRvItemBinding

class AddressGroupAdapter(
    private val onGroupClick: OnClickAddressGroup,
    private val onDeleteClickListener: OnClickDelete,
    private val chooseGroup: Boolean
) : RecyclerView.Adapter<AddressGroupAdapter.AddressGroupViewHolder>() {

    private lateinit var addressGroupList: List<AddressGroupWithAddress>
    private var selectedGroupId: Int = 0

    fun getSelectedGroupId() = selectedGroupId

    inner class AddressGroupViewHolder(val binding: AddressGroupRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val groupName = binding.tvAddressGroupName
        private val addressCount = binding.tvAddressCount
        fun bind(addressGroupWithAddress: AddressGroupWithAddress) {
            groupName.text = addressGroupWithAddress.addressGroup.groupName
            addressCount.text =
                "Addresses(" + addressGroupWithAddress.addressList.size.toString() + ")"
            binding.root.setOnClickListener { onGroupClick.onClick(addressGroupWithAddress) }
            if (chooseGroup) {
                binding.btnDelete.visibility = View.GONE
                binding.rbSelect.visibility = View.VISIBLE
                val groupId = addressGroupWithAddress.addressGroup.addressGroupId
                binding.rbSelect.setOnClickListener {
                    if(addressGroupWithAddress.addressList.isEmpty()){
                        Toast.makeText(binding.root.context, "Address group empty", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                    else{
                        selectedGroupId = groupId
                        notifyDataSetChanged()
                    }
                }
                binding.rbSelect.isChecked = selectedGroupId == groupId
            } else {
                binding.rbSelect.visibility = View.GONE
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnDelete.setOnClickListener {
                    onDeleteClickListener.onClick(
                        addressGroupWithAddress.addressGroup.addressGroupId
                    )
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressGroupViewHolder {
        val binding = AddressGroupRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressGroupViewHolder, position: Int) {
        holder.bind(addressGroupList[position])
    }

    override fun getItemCount(): Int {
        return addressGroupList.size
    }

    interface OnClickAddressGroup {
        fun onClick(addressGroupWithAddress: AddressGroupWithAddress)
    }

    interface OnClickDelete {
        fun onClick(addressGroupId: Int)
    }

    class DiffUtilCallback(private val oldList: List<AddressGroupWithAddress>, private val newList: List<AddressGroupWithAddress>) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.addressGroup.addressGroupId == newItem.addressGroup.addressGroupId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setData(data: List<AddressGroupWithAddress>) {
        this.addressGroupList = data
    }

    fun setNewData(newData: List<AddressGroupWithAddress>) {
        val diffCallback = DiffUtilCallback(addressGroupList, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        addressGroupList = newData
        diffResult.dispatchUpdatesTo(this)
    }

}