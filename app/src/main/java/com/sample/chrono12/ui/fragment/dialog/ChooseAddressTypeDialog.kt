package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.databinding.FragmentChooseAddressTypeBinding
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.UserViewModel


class ChooseAddressTypeDialog : DialogFragment() {

    private lateinit var binding: FragmentChooseAddressTypeBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        dialog!!.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseAddressTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnSelect.setOnClickListener {
            when (binding.rgChooseAddressType.checkedRadioButtonId) {
                binding.rbFromAddresses.id -> {
                    userViewModel.setSelectedAddressAndGroupId(0)
                    userViewModel.isSelectAddressEnabled = false
                    findNavController().safeNavigate(
                        ChooseAddressTypeDialogDirections.actionChooseAddressTypeFragmentToAddressFragment(
                            chooseAddress = true
                        )
                    )
                }
                binding.rbFromAddressGroups.id -> {
                    userViewModel.setSelectedAddressAndGroupId(0)
                    userViewModel.isSelectAddressEnabled = false
                    findNavController().safeNavigate(
                        ChooseAddressTypeDialogDirections.actionChooseAddressTypeFragmentToAddressGroupFragment(
                            chooseGroup = true
                        )
                    )
                }
            }

        }
    }

}