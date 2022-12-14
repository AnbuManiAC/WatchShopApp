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
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressGroupBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.ui.adapter.AddressGroupAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.UserViewModel


class AddressGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddressGroupBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressGroupFragmentArgs>()
    private lateinit var addressGroupAdapter: AddressGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddAddress.setOnClickListener{
            findNavController().safeNavigate(AddressGroupFragmentDirections.actionAddressGroupFragmentToCreateAddressGroupDialog())
        }
        setupAddressGroupAdapter()
        if(navArgs.chooseGroup) setupChooseGroupButton()
    }

    private fun setupChooseGroupButton() {
        binding.btnSelectGroup.visibility = View.VISIBLE
        syncSelectButtonState()
        binding.btnSelectGroup.setOnClickListener {
            val groupId = userViewModel.selectedAddressId
            if(groupId>0){
                findNavController().safeNavigate(AddressGroupFragmentDirections.actionAddressGroupFragmentToOrderConfirmationFragment(addressGroupId = groupId))
            }
        }
    }

    private fun syncSelectButtonState() {
        binding.btnSelectGroup.isEnabled = userViewModel.isSelectAddressEnabled
    }

    private fun setupAddressGroupAdapter() {
        addressGroupAdapter = AddressGroupAdapter(
            getOnGroupClickListener(),
            getOnDeleteClickListener(),
            navArgs.chooseGroup
        )
        addressGroupAdapter.setData(listOf())
        binding.rvAddressGroup.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddressGroup.adapter = addressGroupAdapter
        if(navArgs.chooseGroup){
            addressGroupAdapter.setOnSelectionChangeListener(getOnSelectionChangeListener())
            addressGroupAdapter.setSelectedGroupId(userViewModel.selectedAddressId)
        }
        userViewModel.getAddressGroupWithAddresses(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                it?.let {
                    addressGroupAdapter.setNewData(it)
                    setTitle(it.size)
                }
                if(it.isEmpty()){
                    setTitle(0)
                    binding.clNoDataFound.visibility = View.VISIBLE
                    binding.btnSelectGroup.visibility = View.GONE
                }else{
                    binding.clNoDataFound.visibility = View.GONE
                }

            }
    }

    private fun getOnSelectionChangeListener() =
        object : AddressAdapter.OnSelectionChangeListener {
            override fun onChanged(selectedAddressId: Int) {
                userViewModel.isSelectAddressEnabled = true
                syncSelectButtonState()
                userViewModel.setSelectedAddressAndGroupId(selectedAddressId)
                addressGroupAdapter.setSelectedGroupId(userViewModel.selectedAddressId)
            }
        }

    private fun getOnDeleteClickListener() =
        object : AddressGroupAdapter.OnClickDelete {
            override fun onClick(addressGroupId: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.delete_address_group))
                    .setMessage(R.string.address_group_delete_alert)
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        userViewModel.deleteAddressGroup(addressGroupId)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                    }
                    .setCancelable(false)

                builder.create().show()
            }

        }

    private fun getOnGroupClickListener() =
        object : AddressGroupAdapter.OnClickAddressGroup {
            override fun onClick(addressGroupWithAddress: AddressGroupWithAddress) {
                findNavController().safeNavigate(AddressGroupFragmentDirections.actionAddressGroupFragmentToAddressGroupDetailFragment(
                    addressGroupWithAddress.addressGroup.addressGroupId, addressGroupWithAddress.addressGroup.groupName
                ))
            }
        }

    private fun setTitle(size: Int){
        if(size>0){
            (requireActivity() as HomeActivity).setActionBarTitle(getString(R.string.address_group_title, size))
        }else{
            (requireActivity() as HomeActivity).setActionBarTitle(getString(R.string.address_group))
        }
    }

}