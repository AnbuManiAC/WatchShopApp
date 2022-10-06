package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.UserViewModel

class RemoveAddressFromGroupDialog: DialogFragment() {
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<RemoveAddressFromGroupDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.remove_address))
            .setMessage(getString(R.string.address_item_remove_alert))
            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                userViewModel.deleteAddressFromGroup(navArgs.addressId, navArgs.addressGroupId)
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            .setCancelable(false)

        val build = builder.create()
        build.setCancelable(false)
        build.setCanceledOnTouchOutside(false)
        return build
    }
}