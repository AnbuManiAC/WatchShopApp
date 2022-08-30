package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.AddressGroupRvItemBinding

class AddressGroupAdapter(
    private val addressGroupList: List<AddressGroupWithAddress>,
    private val onAddressGroupButtonClickListener: OnClickAddressGroupButton
): RecyclerView.Adapter<AddressGroupAdapter.AddressGroupViewHolder>() {

    inner class AddressGroupViewHolder(val binding: AddressGroupRvItemBinding): RecyclerView.ViewHolder(binding.root){
        private val groupName = binding.tvAddressGroupName
        private val addressCount = binding.tvAddressCount
        fun bind(addressGroup: AddressGroupWithAddress){
            groupName.text = "Group name : "+addressGroup.addressGroup.groupName
            addressCount.text = "Number of Addresses = "+addressGroup.addressList.size.toString()
            binding.btnDeleteAddressGroup.setOnClickListener{
                onAddressGroupButtonClickListener.onClickRemove(addressGroup)
            }
            binding.btnEditAddressGroup.setOnClickListener{
                onAddressGroupButtonClickListener.onClickEdit(addressGroup)
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

    interface OnClickAddressGroupButton {
        fun onClickEdit(addressGroup: AddressGroupWithAddress)
        fun onClickRemove(addressGroup: AddressGroupWithAddress)
    }

}