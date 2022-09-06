package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.AddressGroupRvItemBinding
import java.util.*

class AddressGroupAdapter(
    private val addressGroupList: List<AddressGroupWithAddress>,
    private val onGroupClick: OnClickAddressGroup,
    private val onDeleteClickListener: OnClickDelete
): RecyclerView.Adapter<AddressGroupAdapter.AddressGroupViewHolder>() {

    inner class AddressGroupViewHolder(val binding: AddressGroupRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val groupName = binding.tvAddressGroupName
        private val addressCount = binding.tvAddressCount
        fun bind(addressGroupWithAddress: AddressGroupWithAddress){
                groupName.text = addressGroupWithAddress.addressGroup.groupName
                addressCount.text = "Addresses("+addressGroupWithAddress.addressList.size.toString()+")"
                binding.root.setOnClickListener { onGroupClick.onClick(addressGroupWithAddress) }
                binding.btnDelete.setOnClickListener { onDeleteClickListener.onClick(addressGroupWithAddress.addressGroup.addressGroupId) }
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

}