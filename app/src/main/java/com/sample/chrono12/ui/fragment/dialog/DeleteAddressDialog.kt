package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.UserViewModel

class DeleteAddressDialog : DialogFragment() {

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<DeleteAddressDialogArgs>()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.address_item_delete_alert_title))
            .setMessage(getString(R.string.address_item_delete_alert_msg))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                userViewModel.deleteAddress(navArgs.addressId)
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }

        val build = builder.create()
        build.setCancelable(false)
        build.setCanceledOnTouchOutside(false)
        return build
    }

}