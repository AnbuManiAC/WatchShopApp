package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentChooseAddressTypeBinding


class ChooseAddressTypeDialog : DialogFragment() {

    private lateinit var binding: FragmentChooseAddressTypeBinding

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
                    if (findNavController().currentDestination?.id == R.id.chooseAddressTypeFragment)
                        findNavController().navigate(
                            ChooseAddressTypeDialogDirections.actionChooseAddressTypeFragmentToAddressFragment(
                                chooseAddress = true
                            )
                        )
                }
                binding.rbFromAddressGroups.id -> {
                    if (findNavController().currentDestination?.id == R.id.chooseAddressTypeFragment)
                        findNavController().navigate(
                            ChooseAddressTypeDialogDirections.actionChooseAddressTypeFragmentToAddressGroupFragment(
                                chooseGroup = true
                            )
                        )
                }
            }

        }
    }

}