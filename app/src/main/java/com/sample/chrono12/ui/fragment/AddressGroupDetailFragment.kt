package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.databinding.FragmentAddressGroupBinding
import com.sample.chrono12.databinding.FragmentAddressGroupDetailBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.viewmodels.UserViewModel

class AddressGroupDetailFragment : Fragment() {

    private lateinit var binding: FragmentAddressGroupDetailBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressGroupDetailFragmentArgs>()
    private var addressGroupName = ""
    private var addressGroupId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressGroupDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navArgs.addressGroupId > 0 && navArgs.addressGroupName!="default") {
            addressGroupId = navArgs.addressGroupId
            addressGroupName = navArgs.addressGroupName
            setupAdapter(navArgs.addressGroupId)
            binding.etGroupName.setText(addressGroupName)
        } else {
            addressGroupName = binding.etGroupName.text.toString()
            binding.rvGroupAddresses.visibility = View.GONE
            binding.tvAddresses.visibility = View.GONE
        }
        setupGroupNameEt()
        setupSaveEditButton()
        setupAddNewAddressButton()
        setupAddFromExistingButton()
    }

    private fun setupSaveEditButton() {
        binding.btnSaveEditGroupName.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                binding.etGroupName.clearFocus()
                binding.etGroupName.isFocusableInTouchMode = false
                binding.etGroupName.isFocusable = false
                addAddressGroup(binding.etGroupName.text.toString())
            }
            else{
                binding.etGroupName.isFocusableInTouchMode = true
                binding.etGroupName.isFocusable = true
                binding.etGroupName.requestFocus()
            }
        }
    }

    private fun setupGroupNameEt() {
        val groupNameEt = binding.etGroupName
        if(groupNameEt.text.isEmpty()){
            groupNameEt.requestFocus()
            binding.btnSaveEditGroupName.isEnabled = false
            binding.btnAddNewAddress.isEnabled = false
            binding.btnAddFromExisting.isEnabled = false
        }
        else{
            groupNameEt.clearFocus()
            binding.btnSaveEditGroupName.isChecked = true
            binding.btnSaveEditGroupName.isEnabled = true
            binding.btnSaveEditGroupName.isEnabled = true
            binding.btnAddNewAddress.isEnabled = true
            groupNameEt.isFocusableInTouchMode = false
            groupNameEt.isFocusable = false
        }
        groupNameEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(text.toString().trim().isEmpty()){
                    binding.btnSaveEditGroupName.isEnabled = false
                    binding.btnAddNewAddress.isEnabled = false
                    binding.btnAddFromExisting.isEnabled = false
                } else{
                        binding.btnSaveEditGroupName.isEnabled = true
                        binding.btnAddNewAddress.isEnabled = true
                        binding.btnAddFromExisting.isEnabled = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun addAddressGroup(groupName: String) {
        if(addressGroupId>0){
            userViewModel.updateAddressGroupName(addressGroupId, groupName)
        } else{
            userViewModel.insertIntoAddressGroup(
                AddressGroup(
                    userId = userViewModel.getLoggedInUser().toInt(),
                    groupName = groupName
                )
            )
           userViewModel.getAddressGroupId().observe(viewLifecycleOwner){
               it?.let {
                   addressGroupId = it
               }
            }
        }

        this.addressGroupName = groupName

    }



    private fun setupAddFromExistingButton() {
        binding.btnAddFromExisting.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToAddressFragment(addressGroupName = addressGroupName, addFromExisting = true))
        }
    }

    private fun setupAddNewAddressButton() {
        binding.btnAddNewAddress.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(addressGroupName = addressGroupName))
        }
    }

    private fun setupAdapter(addressGroupId: Int) {
        val adapter = AddressAdapter(
            getOnAddressButtonClickListener(),
            addFromExisting = false,
            chooseAddress = false
        )
        userViewModel.getAddressGroupWithAddresses(
            userViewModel.getLoggedInUser().toInt(),
            addressGroupId
        ).observe(viewLifecycleOwner) {
            it?.let {
                binding.rvGroupAddresses.layoutManager = LinearLayoutManager(requireContext())
                with(binding.tvAddresses) {
                    visibility = View.VISIBLE
                    text = "Addresses(" + it.addressList.size + ")"
                }
                val addressIds = mutableListOf<Int>()
                it.addressList.forEach { addressIds.add(it.addressId) }
                userViewModel.setAddressIds(addressIds)
                with(adapter){
                    setData(it)
                    notifyDataSetChanged()
                }
                with(binding.rvGroupAddresses) {
                    this.adapter = adapter
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getOnAddressButtonClickListener() =
        object : AddressAdapter.OnClickAddressButton {
            override fun onClickRemove(
                addressId: Int,
                addressGroupId: Int,
                addressGroupName: String
            ) {

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure to remove this Address?")
                    .setMessage("Remove address from this group")
                    .setPositiveButton("Remove") { _, _ ->
                        userViewModel.deleteAddressFromGroup(addressId, addressGroupId)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .setCancelable(false)

                builder.create().show()
            }

            override fun onClickEdit(addressId: Int) {
                Navigation.findNavController(requireView()).navigate(
                    AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(addressId, addressGroupName)
                )

            }

        }

    override fun onDestroy() {
        userViewModel.clearAddressGroupId()
        userViewModel.clearAddressIds()
        super.onDestroy()
    }
}