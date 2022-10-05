package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentAddressGroupDetailBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.utils.safeNavigate
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
    ): View {
        binding = FragmentAddressGroupDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navArgs.addressGroupId > 0 && navArgs.addressGroupName != getString(R.string.default_group_name)) {
            this.addressGroupId = navArgs.addressGroupId
            setupAdapter(navArgs.addressGroupId)
        }
        setupEditButton()
        setupAddNewAddressButton()
        setupAddFromExistingButton()
        setupAddressGroupName()
    }

    private fun setupAddressGroupName() {
        userViewModel.getAddressGroupName(userViewModel.getLoggedInUser().toInt(), addressGroupId).observe(viewLifecycleOwner) { addressGroupName ->
            addressGroupName?.let {
                this.addressGroupName = addressGroupName
                binding.tvGroupName.text = addressGroupName
            }
        }
    }

    private fun setupEditButton() {
        binding.btnEditGroupName.setOnClickListener {
            findNavController().safeNavigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToCreateAddressGroupDialog(
                navArgs.addressGroupId,
                navArgs.addressGroupName
            ))
        }
    }


    private fun setupAddFromExistingButton() {
        binding.btnAddFromExisting.setOnClickListener {
            findNavController().safeNavigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToAddressFragment(
                addressGroupName = addressGroupName,
                addFromExisting = true
            ))
        }
    }

    private fun setupAddNewAddressButton() {
        binding.btnAddNewAddress.setOnClickListener {
            findNavController().safeNavigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(
                addressGroupName = addressGroupName
            ))
        }
    }

    private fun setupAdapter(addressGroupId: Int) {
        val adapter = AddressAdapter(
            getOnAddressButtonClickListener(),
            addFromExisting = false,
            chooseAddress = false
        )
        adapter.setAddresses(listOf())
        binding.rvGroupAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGroupAddresses.adapter = adapter
        userViewModel.getAddressGroupWithAddresses(
            userViewModel.getLoggedInUser().toInt(),
            addressGroupId
        ).observe(viewLifecycleOwner) { addressGroupWithAddress ->
            addressGroupWithAddress?.let {
                val addressIds = mutableListOf<Int>()
                addressGroupWithAddress.addressList.forEach { address ->
                    addressIds.add(address.addressId)
                }
                userViewModel.setAddressIds(addressIds)
                adapter.setAddressGroup(addressGroupWithAddress.addressGroup)
                adapter.setNewData(addressGroupWithAddress.addressList)
                if (addressGroupWithAddress.addressList.isNotEmpty()) {
                    with(binding.tvAddresses) {
                        visibility = View.VISIBLE
                        text = getString(R.string.addresses, addressGroupWithAddress.addressList.size)
                    }
                    binding.rvGroupAddresses.visibility = View.VISIBLE
                } else{
                    binding.rvGroupAddresses.visibility = View.GONE
                    binding.tvAddresses.visibility = View.GONE
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
                builder.setTitle(getString(R.string.remove_address))
                    .setMessage(getString(R.string.address_item_remove_alert))
                    .setPositiveButton(getString(R.string.remove)) { _, _ ->
                        userViewModel.deleteAddressFromGroup(addressId, addressGroupId)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                    }
                    .setCancelable(false)

                builder.create().show()
            }

            override fun onClickEdit(addressId: Int) {
                findNavController().safeNavigate(AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(
                    addressId,
                    addressGroupName
                ))
            }

        }

    override fun onDestroy() {
        userViewModel.clearAddressGroupId()
        super.onDestroy()
    }
}