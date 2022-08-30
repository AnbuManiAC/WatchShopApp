package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.databinding.AddressRvItemBinding


class AddressAdapter(
    private val addressList: List<Address>,
    private val onAddressButtonClickListener: OnClickAddressButton
): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val addressName = binding.tvAddressName
        private val doorAndStreet = binding.tvAddressDoorAndStreet
        private val cityStatePincode = binding.tvAddressCityStateAndPincode
        private val landmark = binding.tvAddressLandmark
        private val mobile = binding.tvAddressMobile
        fun bind(address: Address){
            addressName.text = address.contactName
            doorAndStreet.text = address.addressLine1
            landmark.text = address.addressLine2
            cityStatePincode.text = address.city +", "+ address.state +" - "+ address.pincode
            mobile.text = "Mobile Number : "+address.contactNumber.toString()
            binding.btnEditAddress.setOnClickListener { onAddressButtonClickListener.onClickEdit(address) }
            binding.btnRemoveAddress.setOnClickListener { onAddressButtonClickListener.onClickRemove(address) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = AddressRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    interface OnClickAddressButton {
        fun onClickRemove(address: Address)
        fun onClickEdit(address: Address)
    }
}