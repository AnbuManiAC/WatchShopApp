package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.sample.chrono12.viewmodels.UserViewModel

class DeleteSearchDialog : DialogFragment() {

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are you sure you want to delete search history?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSearchHistory()
                dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
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