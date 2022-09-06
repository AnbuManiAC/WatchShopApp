package com.sample.chrono12.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.AddressRvItemBinding


class AddressAdapter(
    private val addressGroupWithAddress: AddressGroupWithAddress,
    private val onAddressButtonClickListener: OnClickAddressButton,
    private val addFromExisting: Boolean
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private val selectedIds = mutableSetOf<Int>()

    fun getSelectedIds() = selectedIds

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val addressName = binding.tvAddressName
        private val doorAndStreet = binding.tvAddressDoorAndStreet
        private val cityStatePincode = binding.tvAddressCityStateAndPincode
        private val landmark = binding.tvAddressLandmark
        private val mobile = binding.tvAddressMobile
        fun bind(addressGroupWithAddress: AddressGroupWithAddress, position: Int) {
            val address = addressGroupWithAddress.addressList[position]
            addressName.text = address.contactName
            doorAndStreet.text = address.addressLine1.split("___").joinToString(", ")
            landmark.text = address.addressLine2
            cityStatePincode.text = address.city + ", " + address.state + " - " + address.pincode
            mobile.text = "Mobile Number : " + address.contactNumber.toString()
            if (addressGroupWithAddress.addressGroup.groupName == "default") {
                binding.btnRemoveAddress.text = "Delete"
            } else {
                binding.btnRemoveAddress.text = "Remove"
            }
            binding.btnEditAddress.setOnClickListener {
                onAddressButtonClickListener.onClickEdit(
                    address.addressId
                )
            }
            binding.btnRemoveAddress.setOnClickListener {
                onAddressButtonClickListener.onClickRemove(
                    address.addressId,
                    addressGroupWithAddress.addressGroup.addressGroupId,
                    addressGroupWithAddress.addressGroup.groupName
                )
            }
            if (addFromExisting) {
                binding.cbSelect.setOnCheckedChangeListener { compoundButton, isChecked ->
                    if (isChecked) {
                        selectedIds.add(addressGroupWithAddress.addressList[position].addressId)
                        Log.d("SELECTED", selectedIds.toString())
                    } else {
                        selectedIds.remove(addressGroupWithAddress.addressList[position].addressId)
                        Log.d("SELECTED", selectedIds.toString())
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = AddressRvItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        if (addFromExisting) {
            binding.btnEditAddress.visibility = View.GONE
            binding.btnRemoveAddress.visibility = View.GONE
            binding.cbSelect.visibility = View.VISIBLE
            binding.cbSelect.isChecked = false
        } else {
            binding.btnEditAddress.visibility = View.VISIBLE
            binding.btnRemoveAddress.visibility = View.VISIBLE
            binding.cbSelect.visibility = View.GONE
        }
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addressGroupWithAddress, position)

    }

    override fun getItemCount(): Int {
        return addressGroupWithAddress.addressList.size
    }

    interface OnClickAddressButton {
        fun onClickRemove(addressId: Int, addressGroupId: Int, addressGroupName: String)
        fun onClickEdit(addressId: Int)
    }
}