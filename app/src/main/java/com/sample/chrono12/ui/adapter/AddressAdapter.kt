package com.sample.chrono12.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.databinding.AddressRvItemBinding


class AddressAdapter(
    private val onAddressButtonClickListener: OnClickAddressButton,
    private val addFromExisting: Boolean,
    private val chooseAddress: Boolean
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private lateinit var addressGroup: AddressGroup
    private lateinit var addresses:  List<Address>

    private var selectedAddressIds = setOf<Int>()
    private var selectedAddressId = 0
    private var hideEditAndDeleteButton = false
    private lateinit var onSelectionChangeListener: OnSelectionChangeListener
    private lateinit var onAddressCheckedListener: OnAddressCheckedListener

    fun setOnSelectionChangeListener(listener: OnSelectionChangeListener){
        onSelectionChangeListener = listener
    }

    fun setOnAddressCheckedListener(listener: OnAddressCheckedListener){
        onAddressCheckedListener = listener
    }

    fun setSelectedAddressId(addressId: Int){
        selectedAddressId = addressId
    }

    fun setSelectedAddressIds(addressIds: Set<Int>){
        selectedAddressIds = addressIds
    }

    fun setHideEditAndDeleteButton(status: Boolean){
        hideEditAndDeleteButton = status
    }

    fun getSelectedIds() = selectedAddressIds

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val addressName = binding.tvAddressName
        private val doorAndStreet = binding.tvAddressDoorAndStreet
        private val cityStatePincode = binding.tvAddressCityStateAndPincode
        private val landmark = binding.tvAddressLandmark
        private val mobile = binding.tvAddressMobile
        fun bind(address: Address) {
            addressName.text = address.contactName
            doorAndStreet.text = address.addressLine1.split("___").joinToString(", ")
            if(address.addressLine2.isEmpty()){
                landmark.visibility = View.GONE
            }else{
                landmark.text = address.addressLine2
            }
            cityStatePincode.text = address.city + ", " + address.state + " - " + address.pincode
            mobile.text = "Mobile Number : " + address.contactNumber
            if (addressGroup.groupName == "default") {
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
                   addressGroup.addressGroupId,
                    addressGroup.groupName
                )
            }
            if (addFromExisting) {
                binding.cbSelect.isChecked = selectedAddressIds.contains(address.addressId)
                binding.cbSelect.setOnCheckedChangeListener { button, isChecked ->
                    if(button.isPressed){
                        onAddressCheckedListener.onChecked(address.addressId, isChecked)
                    }
//                    if (isChecked) {
//                        selectedIds.add(address.addressId)
//                    } else {
//                        selectedIds.remove(address.addressId)
//                    }
                }
            }
            if (chooseAddress) {
                binding.btnRemoveAddress.visibility = View.GONE
                binding.btnEditAddress.visibility = View.GONE
                val rbSelectAddress = binding.rbChoose
                val addressId = address.addressId
                rbSelectAddress.setOnClickListener {
                    onSelectionChangeListener.onChanged(addressId)
                    notifyDataSetChanged()
                }
                rbSelectAddress.isChecked = selectedAddressId == addressId
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
        holder.bind(addresses[position])

    }

    override fun getItemCount(): Int {
        return addresses.size
    }

    interface OnClickAddressButton {
        fun onClickRemove(addressId: Int, addressGroupId: Int, addressGroupName: String)
        fun onClickEdit(addressId: Int)
    }

    interface OnSelectionChangeListener {
        fun onChanged(selectedAddressId: Int)
    }

    interface OnAddressCheckedListener {
        fun onChecked(addressId: Int, isChecked: Boolean)
    }

    class DiffUtilCallback(private val oldList: List<Address>, private val newList: List<Address>) :
    DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.addressId == newItem.addressId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    fun setAddresses(addresses: List<Address>) {
        this.addresses = addresses
    }

    fun setAddressGroup(addressGroup: AddressGroup){
        this.addressGroup = addressGroup
    }

    fun setNewData(newData: List<Address>) {
        val diffCallback = DiffUtilCallback(addresses, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        addresses = newData
        diffResult.dispatchUpdatesTo(this)
    }

}