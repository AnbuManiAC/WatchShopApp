package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.UserViewModel

class DeleteSearchDialog : DialogFragment() {

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.delete_search_history))
            .setMessage(getString(R.string.delete_search_history_alert))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteSearchHistory()
                dismiss()
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

    private fun deleteSearchHistory() {
        userViewModel.deleteSearchHistory(userViewModel.getLoggedInUser().toInt())
    }

}