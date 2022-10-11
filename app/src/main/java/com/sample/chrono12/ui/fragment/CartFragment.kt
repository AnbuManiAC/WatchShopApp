package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.databinding.FragmentCartBinding
import com.sample.chrono12.databinding.LoginPromptCartWishlistDialogBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.UserViewModel

class CartFragment : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var loginPromptBinding: LoginPromptCartWishlistDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private var isUserLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if (isUserLoggedIn) {
            fragmentCartBinding = FragmentCartBinding.inflate(layoutInflater)
            fragmentCartBinding.root
        } else {
            loginPromptBinding = LoginPromptCartWishlistDialogBinding.inflate(layoutInflater)
            loginPromptBinding.root
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        if (isUserLoggedIn) {
            setupCart()
        } else {
            setupCartMissing()
        }

    }

    private fun setupCartMissing() {
        loginPromptBinding.ivMissingCart.setImageResource(R.drawable.missing_cart)
        loginPromptBinding.btnLogIn.setOnClickListener {
            findNavController().safeNavigate(CartFragmentDirections.actionCartFragmentToLogInFragment())
        }
        loginPromptBinding.tvContinueShopping.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCart() {
        val adapter = CartAdapter(
            {
                findNavController().safeNavigate(CartFragmentDirections.actionCartFragmentToProductFragment(it.productId))
            },
            getOnDeleteClickListener(),
            getOnQuantityClickListener()
        )
        adapter.setData(mutableListOf())
        fragmentCartBinding.rvCart.layoutManager = LinearLayoutManager(activity)
        fragmentCartBinding.rvCart.adapter = adapter
        fragmentCartBinding.btnPlaceOrder.setOnClickListener {
            findNavController().safeNavigate(CartFragmentDirections.actionCartFragmentToChooseAddressTypeFragment())
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
            findNavController().navigateUp()
        }
        cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) { cartItems ->
                cartViewModel.initPriceCalculating(cartItems)
                if (cartItems.isEmpty()) {
                    fragmentCartBinding.emptyCart.visibility = View.VISIBLE
                    fragmentCartBinding.layoutPriceOrder.visibility = View.GONE
                }else{
                    fragmentCartBinding.emptyCart.visibility = View.GONE
                    fragmentCartBinding.layoutPriceOrder.visibility = View.VISIBLE
                }
                adapter.setNewData(cartItems)
            }
    }

    private fun getOnDeleteClickListener(): CartAdapter.OnClickDelete =
        object : CartAdapter.OnClickDelete {
            override fun onDelete(productId: Int, quantity: Int) {
                val userId = userViewModel.getLoggedInUser().toInt()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.remove_item))
                    .setMessage(getString(R.string.cart_item_remove_alert))
                    .setPositiveButton(getString(R.string.remove)) { _, _ ->
                        cartViewModel.removeProductFromUserCart(productId, userId)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->

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
                            fragmentCartBinding.snackBarLayout,
                            getString(R.string.only_units_left, quantity),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    } else {
                        Snackbar.make(
                            fragmentCartBinding.snackBarLayout,
                            getString(R.string.max_units_reached),
                            Snackbar.LENGTH_SHORT
                        )
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

    private fun setupMenu(){
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_wishlist_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchFragment -> {
                        findNavController().safeNavigate(CartFragmentDirections.actionCartFragmentToSearchFragment())
                        true
                    }
                    R.id.wishlistFragment -> {
                        findNavController().safeNavigate(CartFragmentDirections.actionCartFragmentToWishlistFragment())
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}