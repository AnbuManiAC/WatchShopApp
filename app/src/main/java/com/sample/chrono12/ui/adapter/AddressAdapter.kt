package com.sample.chrono12.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.AddressRvItemBinding


class AddressAdapter(
    private val onAddressButtonClickListener: OnClickAddressButton,
    private val addFromExisting: Boolean,
    private val chooseAddress: Boolean
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private lateinit var addressGroupWithAddress: AddressGroupWithAddress
    private val selectedIds = mutableSetOf<Int>()
    private var selectedAddressAndGroupId = Pair(0,0)
    private var hideEditAndDeleteButton = false

    fun setData(addressGroupWithAddress: AddressGroupWithAddress){
        this.addressGroupWithAddress = addressGroupWithAddress
    }

    fun setHideEditAndDeleteButton(status: Boolean){
        hideEditAndDeleteButton = status
    }

    fun getSelectedAddressId() = selectedAddressAndGroupId
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
                    } else {
                        selectedIds.remove(addressGroupWithAddress.addressList[position].addressId)
                    }
                }
            }
            if (chooseAddress) {
                binding.btnRemoveAddress.visibility = View.GONE
                binding.btnEditAddress.visibility = View.GONE
                val rbSelectAddress = binding.rbChoose
                val addressId = address.addressId
                val groupId =addressGroupWithAddress.addressGroup.addressGroupId
                rbSelectAddress.setOnClickListener {
                    selectedAddressAndGroupId = Pair(groupId, addressId)
                    notifyDataSetChanged()
                }
                rbSelectAddress.isChecked = selectedAddressAndGroupId.second == addressId
            }
            if(hideEditAndDeleteButton){
                binding.btnRemoveAddress.visibility = View.GONE
                binding.btnEditAddress.visibility = View.GONE
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
            binding.rbChoose.visibility = View.GONE
            binding.cbSelect.visibility = View.VISIBLE
            binding.cbSelect.isChecked = false
        } else if (chooseAddress) {
            binding.rbChoose.visibility = View.VISIBLE
            binding.rbChoose.isChecked = false
            binding.cbSelect.visibility = View.GONE
        } else {
            binding.btnEditAddress.visibility = View.VISIBLE
            binding.btnRemoveAddress.visibility = View.VISIBLE
            binding.cbSelect.visibility = View.GONE
            binding.rbChoose.visibility = View.GONE
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