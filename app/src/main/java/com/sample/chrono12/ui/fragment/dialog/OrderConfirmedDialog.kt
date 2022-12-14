package com.sample.chrono12.ui.fragment.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sample.chrono12.R
import com.sample.chrono12.utils.safeNavigate

class OrderConfirmedDialog : DialogFragment() {

    private val navArgs by navArgs<OrderConfirmedDialogArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(R.layout.fragment_order_confirmed_dialog)
                .setPositiveButton(getString(R.string.okay)) { _, _ ->
                    findNavController().popBackStack(R.id.homeFragment, false)
                }.setNegativeButton(getString(R.string.view_order)) { _, _ ->
                    findNavController().safeNavigate(
                        OrderConfirmedDialogDirections.actionOrderConfirmedDialogToOrderDetailFragment(
                            navArgs.bulkOrderId,
                            navArgs.orderId
                        )
                    )
                }
            val alertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog
        }
    }

}