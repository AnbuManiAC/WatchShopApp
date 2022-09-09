package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.Order
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.ProductOrdered
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.data.entities.relations.CartWithProductInfo
import com.sample.chrono12.data.models.OrderStatus
import com.sample.chrono12.databinding.FragmentOrderConfirmationBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val navArgs by navArgs<OrderConfirmationFragmentArgs>()
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var addressGroupWithAddress: AddressGroupWithAddress
    private lateinit var cartWithProductInfo: List<CartWithProductInfo>
    private  var originalPrice: Int = 0
    private var currentPrice: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProductsAdapter()
        setupAddressAdapter()
        setupPriceDetails()
        setupConfirmOrder()
    }

    private fun setupConfirmOrder() {
        binding.btnConfirmOrder.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences(
                getString(R.string.user_pref),
                Context.MODE_PRIVATE
            )
            val bulkOrderId = sharedPref.getInt(getString(R.string.bulk_order_id), 0)
            val editor = sharedPref?.edit()
            editor?.let {
                editor.putInt(getString(R.string.bulk_order_id), bulkOrderId+1)
                editor.apply()
            }
            val orderIds = mutableListOf<Long>()
            lifecycleScope.launch {
                addressGroupWithAddress.addressList.forEach {
                    val order = Order(
                        bulkOrderId = bulkOrderId + 1,
                        userId = userViewModel.getLoggedInUser().toInt(),
                        timestamp = System.currentTimeMillis(),
                        actualTotal = originalPrice.toFloat(),
                        totalPrice = currentPrice.toFloat(),
                        addressInfo = getAddressString(addressGroupWithAddress.addressList[0]),
                        orderStatus = OrderStatus.ORDERED.toString()
                    )
                    val orderId = userViewModel.insertOrder(order)
                    cartWithProductInfo.forEach {
                        val productOrdered = ProductOrdered(
                            orderId = orderId,
                            productId = it.cart.productId,
                            quantity = it.cart.quantity
                        )
                        userViewModel.insertProductOrdered(productOrdered)
                    }
                }
            }
            findNavController().navigate(
                OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToOrderConfirmedDialog()
            )
        }
    }

    private fun setupPriceDetails() {
        cartViewModel.getTotalOriginPrice().observe(viewLifecycleOwner) { originalPrice ->
            this.originalPrice = originalPrice
            binding.tvTotalOriginalPriceInRps.text = getString(R.string.price, originalPrice)
        }
        cartViewModel.getTotalCurrentPrice().observe(viewLifecycleOwner) { currentPrice ->
            this.currentPrice = currentPrice
            binding.tvTotalCurrentPriceInRps.text = getString(R.string.price, currentPrice)
        }
        cartViewModel.getTotalDiscount().observe(viewLifecycleOwner) { totalDiscount ->
            binding.tvDiscountInRps.text = getString(R.string.discount_price, totalDiscount)
        }
    }

    private fun setupAddressAdapter() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireActivity())
        addressAdapter = AddressAdapter(getOnAddressButtonClickListener(), false, false)
        addressAdapter.setHideEditAndDeleteButton(true)
        if (navArgs.addressId > 0) {
            userViewModel.getAddressGroupWithAddressByAddressId(
                userViewModel.getLoggedInUser().toInt(), navArgs.addressGroupId, navArgs.addressId
            )
                .observe(viewLifecycleOwner) {
                    val addresses = mutableListOf<Address>()
                    it.addressList.forEach { address ->
                        if (address.addressId == navArgs.addressId) {
                            addresses.add(address)
                        }
                    }
                    addressGroupWithAddress = AddressGroupWithAddress(
                        addressGroup = it.addressGroup,
                        addressList = addresses.toList()
                    )
                    with(addressAdapter) {
                        setData(addressGroupWithAddress)
                        notifyDataSetChanged()
                    }
                    binding.rvAddress.adapter = addressAdapter
                }

        } else {
            userViewModel.getAddressGroupWithAddresses(
                userViewModel.getLoggedInUser().toInt(),
                navArgs.addressGroupId
            )
                .observe(viewLifecycleOwner) {
                    addressGroupWithAddress = it
                    with(addressAdapter) {
                        setData(it)
                        notifyDataSetChanged()
                    }
                    binding.rvAddress.adapter = addressAdapter
                }
        }
    }

    private fun setupProductsAdapter() {
        binding.rvProducts.layoutManager = LinearLayoutManager(requireActivity())
        cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                cartWithProductInfo = it
                val cartAdapter = CartAdapter(
                    cartWithProductInfo,
                    {
                        Navigation.findNavController(requireView()).navigate(
                            OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToProductFragment(
                                it.productId
                            )
                        )
                    },
                    getOnDeleteClickListener(),
                    getOnQuantityClickListener()
                )
                cartViewModel.initPriceCalculating(cartWithProductInfo)
                cartAdapter.setHideDeleteButton(true)
                binding.rvProducts.adapter = cartAdapter
                binding.tvTotalOriginalPrice.text =
                    "Price (${it.size} item${if (it.size > 1) "s" else ""})"
            }
    }


    private fun getOnAddressButtonClickListener() =
        object : AddressAdapter.OnClickAddressButton {
            override fun onClickRemove(
                addressId: Int,
                addressGroupId: Int,
                addressGroupName: String
            ) {
                if (addressGroupName == "default") {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are you sure to delete this Address?")
                        .setMessage("Deleting a address will result in removing the same from all address groups")
                        .setPositiveButton("Delete") { _, _ ->
                            userViewModel.deleteAddress(addressId)
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .setCancelable(false)

                    builder.create().show()
                } else {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are you sure you want to remove this Address?")
                        .setMessage("Remove address from this group")
                        .setPositiveButton("Remove") { _, _ ->
                            userViewModel.deleteAddressFromGroup(addressId, addressGroupId)
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .setCancelable(false)

                    builder.create().show()
                }
            }

            override fun onClickEdit(addressId: Int) {
                Navigation.findNavController(requireView()).navigate(
                    OrderConfirmationFragmentDirections.actionOrderConfirmationFragmentToNewAddressFragment(
                        addressId
                    )
                )
            }

        }

    private fun getOnDeleteClickListener(): CartAdapter.OnClickDelete =
        object : CartAdapter.OnClickDelete {
            override fun onDelete(productId: Int, quantity: Int) {
                val userId = userViewModel.getLoggedInUser().toInt()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to delete this product from Cart?")
                    .setMessage("This will not be reversed")
                    .setPositiveButton("Delete") { _, _ ->
                        cartViewModel.removeProductFromUserCart(productId, userId)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .setCancelable(false)
                builder.create().show()
            }
        }

    private fun getOnQuantityClickListener(): CartAdapter.OnClickQuantity =
        object : CartAdapter.OnClickQuantity {
            override fun onClickPlus(product: Product, quantity: Int): Boolean {
                return if (quantity < 5 && product.stockCount > quantity) {
                    cartViewModel.updateQuantity(
                        product.productId,
                        userViewModel.getLoggedInUser().toInt(),
                        quantity + 1
                    )
                    true
                } else {
                    if (product.stockCount <= quantity) {
                        Snackbar.make(
                            requireView(),
                            "Only $quantity units left",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(requireView(), "Max 5 units only", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    false
                }
            }

            override fun onClickMinus(product: Product, quantity: Int): Boolean {
                return if (quantity > 1) {
                    cartViewModel.updateQuantity(
                        product.productId,
                        userViewModel.getLoggedInUser().toInt(),
                        quantity - 1
                    )
                    true
                } else {
                    false
                }
            }

        }

    private fun getAddressString(address: Address): String {
        return address.contactName + "\n" + address.addressLine1.split("___")
            .joinToString(", ") + "\n" + address.addressLine2 + "\n" + address.city + ", " + address.state + " - " + address.pincode + "\n" + "Mobile Number : " + address.contactNumber.toString()
    }
}