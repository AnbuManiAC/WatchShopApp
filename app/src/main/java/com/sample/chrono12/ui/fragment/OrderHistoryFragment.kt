package com.sample.chrono12.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Index
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentOrderConfirmationBinding
import com.sample.chrono12.databinding.FragmentOrderHistoryBinding
import com.sample.chrono12.ui.adapter.OrderHistoryAdapter
import com.sample.chrono12.viewmodels.OrderViewModel
import com.sample.chrono12.viewmodels.UserViewModel

class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    private val orderViewModel by  lazy { ViewModelProvider(requireActivity())[OrderViewModel::class.java] }
    private val userViewModel by  lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderViewModel.setOrderHistory(userViewModel.getLoggedInUser().toInt())
        setupOrderHistoryAdapter()
    }

    private fun setupOrderHistoryAdapter() {
        val adapter = OrderHistoryAdapter(onOrderClickListener)
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(requireContext())
        orderViewModel.getOrderHistory().observe(viewLifecycleOwner){ orderHistory ->
            with(adapter){
                setData(orderHistory)
                notifyDataSetChanged()
            }
            binding.rvOrderHistory.adapter = adapter
        }
    }

    private val onOrderClickListener =
        object : OrderHistoryAdapter.OnClickOrder {
        override fun onClick(bulkOrderId: Int, orderId: Int) {
            Navigation.findNavController(requireView()).navigate(
                OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailFragment(bulkOrderId, orderId)
            )
        }
    }

}