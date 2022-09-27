package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.viewmodels.UserViewModel

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressFragmentArgs>()
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (navArgs.addFromExisting) {
            binding.fabAddAddress.visibility = View.GONE
        } else {
            binding.fabAddAddress.visibility = View.VISIBLE
            binding.fabAddAddress.setOnClickListener {
                if (findNavController().currentDestination?.id == R.id.addressFragment)
                    findNavController().navigate(
                        AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(
                            addressGroupName = "default"
                        )
                    )
            }
        }
        setupAddressAdapter()
        if (navArgs.chooseAddress) setupChooseAddressButton()
    }

    private fun setupChooseAddressButton() {
        binding.btnSelectAddress.visibility = View.VISIBLE
        binding.btnSelectAddress.setOnClickListener {
            if (addressAdapter.getSelectedAddressId().second > 0) {
                val groupId = addressAdapter.getSelectedAddressId().first
                val addressId = addressAdapter.getSelectedAddressId().second
                if (findNavController().currentDestination?.id == R.id.addressFragment)
                    findNavController().navigate(
                        AddressFragmentDirections.actionAddressFragmentToOrderConfirmationFragment(
                            groupId,
                            addressId
                        )
                    )
            }
        }
    }

    private fun setupAddressAdapter() {

        addressAdapter = AddressAdapter(
            getOnAddressButtonClickListener(),
            navArgs.addFromExisting,
            navArgs.chooseAddress
        )
        addressAdapter.setAddresses(listOf())
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.adapter = addressAdapter

        if (navArgs.addFromExisting) {
            val addressIds: List<Int> = userViewModel.getAddressIds() ?: emptyList()
            userViewModel.getUserAddresses(
                userViewModel.getLoggedInUser().toInt()
            ).observe(viewLifecycleOwner) {
                it?.let {
                    val addresses = mutableListOf<Address>()
                    it.addressList.forEach { address ->
                        if (address.addressId !in addressIds) {
                            addresses.add(address)
                        }
                    }
                    val addressGroupWithAddress = AddressGroupWithAddress(
                        addressGroup = it.addressGroup,
                        addressList = addresses.toList()
                    )
                    addressAdapter.setAddressGroup(addressGroupWithAddress.addressGroup)
                    addressAdapter.setNewData(addressGroupWithAddress.addressList)
                }
                if (it.addressList.isEmpty()) {
                    binding.clNoDataFound.visibility = View.VISIBLE
                } else {
                    binding.clNoDataFound.visibility = View.GONE
                }
            }
        } else {
            userViewModel.getUserAddresses(userViewModel.getLoggedInUser().toInt())
                .observe(viewLifecycleOwner) {
                    it?.let { addressWithAddressGroup ->
                        addressAdapter.setAddressGroup(addressWithAddressGroup.addressGroup)
                        addressAdapter.setNewData(addressWithAddressGroup.addressList)
                    }
                    if (it.addressList.isEmpty()) {
                        binding.clNoDataFound.visibility = View.VISIBLE
                        binding.btnSelectAddress.visibility = View.GONE
                    } else {
                        binding.clNoDataFound.visibility = View.GONE
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
                if (addressGroupName == "default") {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are you sure to delete this Address?")
                        .setMessage("Deleting a address will result in removing the same from all address groups")
                        .setPositiveButton("Delete") { _, _ ->
                            userViewModel.deleteAddress(addressId)
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .setCancelable(false)

                    builder.create().show()
                } else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are you sure you want to remove this Address?")
                        .setMessage("Remove address from this group")
                        .setPositiveButton("Remove") { _, _ ->
                            userViewModel.deleteAddressFromGroup(addressId, addressGroupId)
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .setCancelable(false)

                    builder.create().show()
                }
            }

            override fun onClickEdit(addressId: Int) {
                if (findNavController().currentDestination?.id == R.id.addressFragment)
                    findNavController().navigate(
                        AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(
                            addressId
                        )
                    )
            }

        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (navArgs.addFromExisting) inflater.inflate(R.menu.done_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            addressAdapter.getSelectedIds().forEach {
                userViewModel.insertIntoAddressAndGroupCrossRef(it, navArgs.addressGroupName)
            }
            if (findNavController().currentDestination?.id == R.id.addressFragment) findNavController().navigateUp()
//            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToAddressGroupDetailFragment(addressGroupId = navArgs.addressGroupId, addressGroupName = navArgs.addressGroupName))
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
