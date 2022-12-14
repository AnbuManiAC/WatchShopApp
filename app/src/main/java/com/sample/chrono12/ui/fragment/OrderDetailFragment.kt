package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentOrderDetailBinding
import com.sample.chrono12.ui.adapter.OrderInfoAdapter
import com.sample.chrono12.ui.adapter.ProductOrderedAdapter
import com.sample.chrono12.utils.DateUtil
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.OrderViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val orderViewModel by lazy { ViewModelProvider(requireActivity())[OrderViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        orderViewModel.setOrderedProductInfo(navArgs.orderId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrderDetails()
        setupProductAdapter()
    }

    private fun setupOrderDetails() {
        lifecycleScope.launch {
            val orderInfo = orderViewModel.getOrderInfo(
                navArgs.bulkOrderId,
                userViewModel.getLoggedInUser().toInt()
            )
            setupOrderInfo(orderInfo.orderCount)
            if (orderInfo.orderCount > 1) {
                binding.tvOrderId.text = getString(R.string.bulk_ord_id, orderInfo.bulkOrderId)
                with(binding.tvNumberOfProduct) {
                    visibility = View.VISIBLE
                    text = getString(R.string.number_of_products_per_order, orderInfo.productCount)
                }
                binding.tvTotalNumberOfProduct.text = getString(
                    R.string.total_number_of_products,
                    (orderInfo.orderCount * orderInfo.productCount)
                )
            } else {
                binding.tvOrderId.text = getString(R.string.order_id, orderInfo.orderId)
                binding.tvTotalNumberOfProduct.text =
                    getString(R.string.number_of_products, orderInfo.productCount)

            }
            binding.tvOrderDateTime.text =
                getString(R.string.date_time, DateUtil.getDateAndTime(orderInfo.timestamp))
            binding.tvTotalOriginalPriceInRps.text = getString(R.string.price, orderInfo.totalPrice)
            binding.tvDiscountInRps.text =
                getString(R.string.discount_price, (orderInfo.totalPrice - orderInfo.currentPrice))
            binding.tvTotalCurrentPriceInRps.text =
                getString(R.string.price, orderInfo.currentPrice)
        }

    }

    private fun setupOrderInfo(orderCount: Int) {
        if (orderCount > 1) {
            binding.clAddress.visibility = View.GONE
            binding.tvOrderInfo.visibility = View.VISIBLE
            binding.rvOrderInfo.visibility = View.VISIBLE
            setupOrderInfoAdapter()
        } else {
            binding.clAddress.visibility = View.VISIBLE
            binding.rvOrderInfo.visibility = View.GONE
            setupAddressAndStatus()
        }
    }

    private fun setupAddressAndStatus() {
        orderViewModel.getOrderDetail(navArgs.bulkOrderId, userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) { orderDetail ->
                binding.tvAddress.text = orderDetail[0].addressInfo
                binding.tvOrderStatus.text = orderDetail[0].orderStatus
            }
    }

    private fun setupOrderInfoAdapter() {
        val adapter = OrderInfoAdapter()
        binding.rvOrderInfo.layoutManager = LinearLayoutManager(requireContext())
        orderViewModel.getOrderDetail(navArgs.bulkOrderId, userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) { orderList ->
                with(adapter) {
                    setData(orderList)
                    notifyDataSetChanged()
                }
                binding.rvOrderInfo.adapter = adapter
            }
    }

    private fun setupProductAdapter() {
        val adapter = ProductOrderedAdapter { product ->
            findNavController().safeNavigate(
                OrderDetailFragmentDirections.actionOrderDetailFragmentToProductFragment(
                    product.productId
                )
            )
        }
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        orderViewModel.getOrderedProductInfo().observe(viewLifecycleOwner) { productList ->
            with(adapter) {
                setData(productList)
                notifyDataSetChanged()
            }
            binding.rvProducts.adapter = adapter
        }
    }

}