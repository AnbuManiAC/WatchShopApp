package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.ActivityMainBinding
import com.sample.chrono12.databinding.AddressRvItemBinding
import com.sample.chrono12.databinding.FragmentAddressBinding
import com.sample.chrono12.databinding.FragmentNewAddressBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressFragmentArgs>()
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
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
                Navigation.findNavController(requireView())
                    .navigate(
                        AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(
                            addressGroupName = "default"
                        )
                    )
            }
        }
        setupAddressAdapter()
    }

    private fun setupAddressAdapter() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
//        if (navArgs.addressGroupId > 0) {
//            userViewModel.getAddressGroupWithAddresses(
//                userViewModel.getLoggedInUser().toInt(),
//                navArgs.addressGroupId
//            ).observe(viewLifecycleOwner) {
//                if (it != null) {
//                    val adapter = AddressAdapter(
//                        it,
//                        getOnAddressButtonClickListener(),
//                        navArgs.addFromExisting
//                    )
//                    binding.rvAddress.adapter = adapter
//                }
//            }
//        } else {
            if (navArgs.addFromExisting) {
                val addressIds: List<Int> = userViewModel.getAddressIds()?: emptyList()
                userViewModel.getUserAddresses(
                    userViewModel.getLoggedInUser().toInt()
                ).observe(viewLifecycleOwner) {
                    it?.let {
                        val addresses = mutableListOf<Address>()
                        it.addressList.forEach {
                            if (it.addressId !in addressIds) {
                                addresses.add(it)
                            }
                        }
                        val addressGroupWithAddress = AddressGroupWithAddress(
                            addressGroup = it.addressGroup,
                            addressList = addresses.toList()
                        )
                        addressAdapter = AddressAdapter(
                            addressGroupWithAddress,
                            getOnAddressButtonClickListener(),
                            navArgs.addFromExisting
                        )
                        binding.rvAddress.adapter = addressAdapter
                    }
                }
            } else {
                userViewModel.getUserAddresses(userViewModel.getLoggedInUser().toInt())
                    .observe(viewLifecycleOwner) {
                        if (it !== null) {
                            addressAdapter = AddressAdapter(
                                it,
                                getOnAddressButtonClickListener(),
                                navArgs.addFromExisting
                            )
                            binding.rvAddress.adapter = addressAdapter
                        }

                    }
            }

//        }

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
                Navigation.findNavController(requireView()).navigate(
                    AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(addressId)
                )
            }

        }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(navArgs.addFromExisting) inflater.inflate(R.menu.done_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.done){
                addressAdapter.getSelectedIds().forEach {
                    userViewModel.insertIntoAddressAndGroupCrossRef(it, navArgs.addressGroupName)
                }
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
