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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressGroupBinding
import com.sample.chrono12.ui.adapter.AddressGroupAdapter
import com.sample.chrono12.viewmodels.UserViewModel


class AddressGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddressGroupBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressGroupFragmentArgs>()
    private lateinit var addressGroupAdapter: AddressGroupAdapter

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
        if(navArgs.chooseGroup) setupChooseGroupButton()
    }

    private fun setupChooseGroupButton() {
        binding.btnSelectGroup.visibility = View.VISIBLE
        binding.btnSelectGroup.setOnClickListener {
            val groupId = addressGroupAdapter.getSelectedGroupId()
            if(groupId>0){
                findNavController().navigate(
                    AddressGroupFragmentDirections.actionAddressGroupFragmentToOrderConfirmationFragment(addressGroupId = groupId)
                )
            }
        }
    }

    private fun setupAddressGroupAdapter() {
        addressGroupAdapter = AddressGroupAdapter(
            getOnGroupClickListener(),
            getOnDeleteClickListener(),
            navArgs.chooseGroup
        )
        binding.rvAddressGroup.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getAddressGroupWithAddresses(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                with(addressGroupAdapter){
                    setData(it)
                    notifyDataSetChanged()
                }
                binding.rvAddressGroup.adapter = addressGroupAdapter
            }
    }

    private fun getOnDeleteClickListener() =
        object : AddressGroupAdapter.OnClickDelete {
            override fun onClick(addressGroupId: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to delete this Address group?")
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