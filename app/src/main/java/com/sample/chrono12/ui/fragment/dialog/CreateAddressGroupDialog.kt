package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.databinding.AddAddressGroupNameBinding
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class CreateAddressGroupDialog : DialogFragment() {

    private lateinit var binding: AddAddressGroupNameBinding
    private val navArgs by navArgs<CreateAddressGroupDialogArgs>()
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddAddressGroupNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        dialog!!.setCanceledOnTouchOutside(false)

        if (navArgs.addressGroupId > 0) {
            binding.title.text = resources.getString(R.string.address_group)
            binding.tilAddressGroupName.hint = resources.getString(R.string.edit_group_name)
        }

        val etGroupName = binding.tilEtAddressGroupName
        val etGroupNameLayout = binding.tilAddressGroupName
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        etGroupName.setText(navArgs.addressGroupName)
        etGroupName.setSelection(navArgs.addressGroupName.length)
        etGroupName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etGroupNameLayout.error = null
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            val groupName = etGroupName.text.toString()
            if (groupName.isEmpty()) {
                etGroupName.clearFocus()
                etGroupNameLayout.error = resources.getString(R.string.field_cant_be_empty)
            } else {
                lifecycleScope.launch {
                    if (userViewModel.checkForAddressGroupExistence(groupName)) {
                        etGroupName.clearFocus()
                        etGroupNameLayout.error =
                            resources.getString(R.string.address_group_name_already_exists)
                    } else {
                        addAddressGroup(groupName)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun addAddressGroup(groupName: String) {
        if (navArgs.addressGroupId > 0) {
            userViewModel.updateAddressGroupName(navArgs.addressGroupId, groupName)
            dismiss()
        } else {
            lifecycleScope.launch {
                val groupId = userViewModel.insertIntoAddressGroup(
                    AddressGroup(
                        userId = userViewModel.getLoggedInUser().toInt(),
                        groupName = groupName
                    )
                )
                if (findNavController().currentDestination?.id == R.id.createAddressGroupDialog)
                    findNavController().navigate(
                        CreateAddressGroupDialogDirections.actionCreateAddressGroupDialogToAddressGroupDetailFragment(
                            addressGroupName = groupName,
                            addressGroupId = groupId
                        )
                    )
            }
        }
    }
}