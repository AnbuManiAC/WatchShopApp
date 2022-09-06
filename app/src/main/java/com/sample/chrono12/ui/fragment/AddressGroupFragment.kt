package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressGroupBinding
import com.sample.chrono12.ui.adapter.AddressGroupAdapter
import com.sample.chrono12.viewmodels.UserViewModel


class AddressGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddressGroupBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddressGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddAddress.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(AddressGroupFragmentDirections.actionAddressGroupFragmentToAddressGroupDetailFragment())
        }
        setupAddressGroupAdapter()
    }

    private fun setupAddressGroupAdapter() {
        binding.rvAddressGroup.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getAddressGroupWithAddresses(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                val adapter = AddressGroupAdapter(
                    it,
                    getOnGroupClickListener(),
                    getOnDeleteClickListener()
                )
                binding.rvAddressGroup.adapter = adapter
            }
    }

    private fun getOnDeleteClickListener() =
        object : AddressGroupAdapter.OnClickDelete {
            override fun onClick(addressGroupId: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to delete this Address group?")
                    .setMessage("This will not be reversed")
                    .setPositiveButton("Delete") { _, _ ->
                        userViewModel.deleteAddressGroup(addressGroupId)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .setCancelable(false)

                builder.create().show()
            }

        }

    private fun getOnGroupClickListener() =
        object : AddressGroupAdapter.OnClickAddressGroup {
            override fun onClick(addressGroupWithAddress: AddressGroupWithAddress) {
                Navigation.findNavController(requireView()).navigate(
                    AddressGroupFragmentDirections.actionAddressGroupFragmentToAddressGroupDetailFragment(
                        addressGroupWithAddress.addressGroup.addressGroupId, addressGroupWithAddress.addressGroup.groupName
                    )
                )
            }
        }

}