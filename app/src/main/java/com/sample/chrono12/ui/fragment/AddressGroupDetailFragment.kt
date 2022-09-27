package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentAddressGroupDetailBinding
import com.sample.chrono12.ui.activity.HomeActivity
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
    ): View {
        binding = FragmentAddressGroupDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navArgs.addressGroupId > 0 && navArgs.addressGroupName != "default") {
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
            if(findNavController().currentDestination?.id == R.id.addressGroupDetailFragment)
                findNavController().navigate(
                AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToCreateAddressGroupDialog(
                    navArgs.addressGroupId,
                    navArgs.addressGroupName
                )
            )
        }
    }


    private fun setupAddFromExistingButton() {
        binding.btnAddFromExisting.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.addressGroupDetailFragment)
                findNavController()
                .navigate(
                    AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToAddressFragment(
                        addressGroupName = addressGroupName,
                        addFromExisting = true
                    )
                )
        }
    }

    private fun setupAddNewAddressButton() {
        binding.btnAddNewAddress.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.addressGroupDetailFragment)
                findNavController()
                .navigate(
                    AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(
                        addressGroupName = addressGroupName
                    )
                )
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
        ).observe(viewLifecycleOwner) { addressGroup ->
            addressGroup?.let {
                binding.rvGroupAddresses.layoutManager = LinearLayoutManager(requireContext())
                if (addressGroup.addressList.isNotEmpty()) {
                    with(binding.tvAddresses) {
                        visibility = View.VISIBLE
                        text = "Addresses(" + addressGroup.addressList.size + ")"
                    }
                    val addressIds = mutableListOf<Int>()
                    addressGroup.addressList.forEach { address ->
                        addressIds.add(address.addressId)
                    }
                    userViewModel.setAddressIds(addressIds)
                    with(adapter) {
                        setData(addressGroup)
                        notifyDataSetChanged()
                    }
                    with(binding.rvGroupAddresses) {
                        this.adapter = adapter
                        visibility = View.VISIBLE
                    }
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
                if(findNavController().currentDestination?.id == R.id.addressGroupDetailFragment)
                    findNavController().navigate(
                    AddressGroupDetailFragmentDirections.actionAddressGroupDetailFragmentToNewAddressFragment(
                        addressId,
                        addressGroupName
                    )
                )

            }

        }

    override fun onDestroy() {
        userViewModel.clearAddressGroupId()
        userViewModel.clearAddressIds()
        super.onDestroy()
    }

}