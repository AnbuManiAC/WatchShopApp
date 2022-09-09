package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R

class OrderConfirmedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.fragment_order_confirmed_dialog)
                .setPositiveButton("Okay") { _, _ ->
                    findNavController().popBackStack(R.id.homeFragment, false)
                }
                .setCancelable(false)
            builder.create()
        }
    }

}