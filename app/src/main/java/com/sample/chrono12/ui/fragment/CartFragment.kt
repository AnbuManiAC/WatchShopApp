package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.databinding.FragmentCartBinding
import com.sample.chrono12.databinding.LoginPromptCartWishlistDialogBinding
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.awaitAll

class CartFragment : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var loginPromptBinding: LoginPromptCartWishlistDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private var isUserLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if (isUserLoggedIn) {
            fragmentCartBinding = FragmentCartBinding.inflate(layoutInflater)
            fragmentCartBinding.root
        } else {
            loginPromptBinding = LoginPromptCartWishlistDialogBinding.inflate(layoutInflater)
            loginPromptBinding.root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn) {
            setupCart()
        } else {
            setupCartMissing()
        }

    }

    private fun setupCartMissing() {
        loginPromptBinding.ivMissingCart.setImageResource(R.drawable.missing_cart)
        loginPromptBinding.btnLogIn.setOnClickListener {
            Log.d("cart", "In missing cart nav")
            if(findNavController().currentDestination?.id == R.id.cartFragment)
                findNavController()
                .navigate(CartFragmentDirections.actionCartFragmentToLogInFragment())
        }
        loginPromptBinding.tvContinueShopping.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.cartFragment)
                findNavController()
                .navigate(CartFragmentDirections.actionCartFragmentToHomeFragment())
        }
    }

    private fun setupCart() {
        val adapter = CartAdapter(
            {
                if(findNavController().currentDestination?.id == R.id.cartFragment)
                    findNavController()
                    .navigate(CartFragmentDirections.actionCartFragmentToProductFragment(it.productId))
            },
            getOnDeleteClickListener(),
            getOnQuantityClickListener()
        )
        adapter.setData(mutableListOf())
        fragmentCartBinding.rvCart.layoutManager = LinearLayoutManager(activity)
        fragmentCartBinding.rvCart.adapter = adapter
        fragmentCartBinding.btnPlaceOrder.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.cartFragment)
                findNavController().navigate(
                CartFragmentDirections.actionCartFragmentToChooseAddressTypeFragment()
            )
        }
        cartViewModel.getTotalCurrentPrice().observe(viewLifecycleOwner) {
            fragmentCartBinding.tvTotalCurrentPrice.text = getString(R.string.price, it)
        }
        cartViewModel.getTotalOriginPrice().observe(viewLifecycleOwner) {
            fragmentCartBinding.tvTotalPrice.apply {
                text = getString(R.string.price, it)
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

        }
        fragmentCartBinding.btnGoHome.setOnClickListener {
            if(findNavController().currentDestination?.id == R.id.cartFragment)
                findNavController()
                .navigate(CartFragmentDirections.actionCartFragmentToHomeFragment())
        }
        cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) { cartItems ->
                cartViewModel.initPriceCalculating(cartItems)
                if (cartItems.isNotEmpty()) {
                    fragmentCartBinding.ivEmptyCart.visibility = View.GONE
                    fragmentCartBinding.tvEmptyCart.visibility = View.GONE
                    fragmentCartBinding.tvEmptyCartDesc.visibility = View.GONE
                    fragmentCartBinding.btnGoHome.visibility = View.GONE
                    fragmentCartBinding.layoutPriceOrder.visibility = View.VISIBLE
                }
                if (cartItems.isEmpty()) {
                    fragmentCartBinding.ivEmptyCart.setImageResource(R.drawable.ic_empty_cart_svg)
                    fragmentCartBinding.ivEmptyCart.visibility = View.VISIBLE
                    fragmentCartBinding.tvEmptyCart.visibility = View.VISIBLE
                    fragmentCartBinding.tvEmptyCartDesc.visibility = View.VISIBLE
                    fragmentCartBinding.btnGoHome.visibility = View.VISIBLE
                    fragmentCartBinding.layoutPriceOrder.visibility = View.GONE
                }
                adapter.setNewData(cartItems)
//                fragmentCartBinding.rvCart.adapter = adapter
            }
    }

    private fun getOnDeleteClickListener(): CartAdapter.OnClickDelete =
        object : CartAdapter.OnClickDelete {
            override fun onDelete(productId: Int, quantity: Int) {
                val userId = userViewModel.getLoggedInUser().toInt()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to remove this product from Cart?")
                    .setPositiveButton("Remove") { _, _ ->
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchFragment -> {
                if(findNavController().currentDestination?.id == R.id.cartFragment)
                    findNavController().navigate(CartFragmentDirections.actionCartFragmentToSearchFragment())
                true
            }
            R.id.wishlistFragment -> {
                if(findNavController().currentDestination?.id == R.id.cartFragment)
                    findNavController().navigate(CartFragmentDirections.actionCartFragmentToWishlistFragment())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}