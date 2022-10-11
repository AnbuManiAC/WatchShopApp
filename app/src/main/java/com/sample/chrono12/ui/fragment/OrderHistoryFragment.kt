package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentOrderHistoryBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.OrderHistoryAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.OrderViewModel
import com.sample.chrono12.viewmodels.UserViewModel

class OrderHistoryFragment : Fragment() {

    private lateinit var binding: FragmentOrderHistoryBinding
    private val orderViewModel by lazy { ViewModelProvider(requireActivity())[OrderViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        orderViewModel.getOrderHistory().observe(viewLifecycleOwner) { orderHistory ->
            if (orderHistory.isEmpty()) {
                binding.clEmptyOrder.visibility = View.VISIBLE
            } else {
                binding.clEmptyOrder.visibility = View.GONE
            }
            orderHistory?.let {
                with(adapter) {
                    setData(orderHistory)
                    notifyDataSetChanged()
                }
                binding.rvOrderHistory.adapter = adapter
            }

        }
    }

    private val onOrderClickListener =
        object : OrderHistoryAdapter.OnClickOrder {
            override fun onClick(bulkOrderId: Int, orderId: Int) {
                findNavController().safeNavigate(
                    OrderHistoryFragmentDirections.actionOrderHistoryFragmentToOrderDetailFragment(
                        bulkOrderId,
                        orderId
                    )
                )
            }
        }

}