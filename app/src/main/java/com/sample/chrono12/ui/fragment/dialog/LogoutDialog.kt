package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.UserViewModel

class LogoutDialog: DialogFragment() {

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Are you sure you want to log out?")
            .setPositiveButton("Log out"){ _,_ ->
                logoutUser()
            }
            .setNegativeButton("No"){ _,_ ->
                dismiss()
            }
            .setCancelable(false)

        return builder.create()
    }

    private fun logoutUser() {
        val sharedPref = requireActivity().getSharedPreferences(getString(R.string.user_pref),Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.apply() {
            editor.putLong(getString(R.string.user_id), 0)
            editor.apply()
        }
        userViewModel.logOutUser()
        findNavController().popBackStack(R.id.homeFragment, false)
    }
}