package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.viewmodels.UserViewModel

class LogoutDialog : DialogFragment() {

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.log_out))
            .setMessage(getString(R.string.log_out_alert))
            .setPositiveButton(getString(R.string.log_out)) { _, _ ->
                logoutUser()
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

    private fun logoutUser() {
        SharedPrefUtil.setUserId(requireActivity(), 0)
        userViewModel.logOutUser()
        clearNotificationsIfAny()
        findNavController().popBackStack(R.id.homeFragment, false)
        (requireActivity() as HomeActivity).disableCartBadge()
    }

    private fun clearNotificationsIfAny() {
        NotificationManagerCompat.from(requireContext()).cancelAll()
    }
}