package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.WithHint
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.databinding.FragmentWishlistBinding
import com.sample.chrono12.databinding.LoginPromptBinding
import com.sample.chrono12.databinding.LoginPromptCartWishlistDialogBinding
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.ui.adapter.WishListAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.ProductViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import com.sample.chrono12.viewmodels.WishListViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.log

class WishListFragment : Fragment() {

    private lateinit var fragmentWishListBinding: FragmentWishlistBinding
    private lateinit var loginPromptBinding: LoginPromptCartWishlistDialogBinding
    private var isUserLoggedIn = false

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val wishListViewModel by lazy { ViewModelProvider(requireActivity())[WishListViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        setHasOptionsMenu(true)
        return if (isUserLoggedIn) {
            fragmentWishListBinding = FragmentWishlistBinding.inflate(layoutInflater)
            fragmentWishListBinding.root
        } else {
            loginPromptBinding = LoginPromptCartWishlistDialogBinding.inflate(layoutInflater)
            loginPromptBinding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn) {
            setupWishList()
        } else {
            setupLoginPrompt()
        }
    }

    private fun setupLoginPrompt() {
        loginPromptBinding.ivMissingCart.setImageResource(R.drawable.missing_wishlist)
        loginPromptBinding.tvContinueShopping.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(WishListFragmentDirections.actionWishlistFragmentToHomeFragment())
        }
        loginPromptBinding.btnLogIn.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(WishListFragmentDirections.actionWishlistFragmentToLogInFragment())
        }
    }

    private fun setupWishList() {

        val rvWishList = fragmentWishListBinding.rvWishlist
        rvWishList.layoutManager = LinearLayoutManager(activity)
        fragmentWishListBinding.btnGoHome.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(WishListFragmentDirections.actionWishlistFragmentToHomeFragment())
        }
        wishListViewModel.getWishListItems(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    fragmentWishListBinding.ivEmptyCart.visibility = View.VISIBLE
                    fragmentWishListBinding.tvEmptyCart.visibility = View.VISIBLE
                    fragmentWishListBinding.tvEmptyCartDesc.visibility = View.VISIBLE
                    fragmentWishListBinding.btnGoHome.visibility = View.VISIBLE
                }
                if (it.isNotEmpty()) {
                    fragmentWishListBinding.ivEmptyCart.visibility = View.GONE
                    fragmentWishListBinding.tvEmptyCart.visibility = View.GONE
                    fragmentWishListBinding.tvEmptyCartDesc.visibility = View.GONE
                    fragmentWishListBinding.btnGoHome.visibility = View.GONE
                }
                val adapter = WishListAdapter(
                    it,
                    {
                        Navigation.findNavController(requireView()).navigate(
                            WishListFragmentDirections.actionWishlistFragmentToProductFragment(it.productId)
                        )
                    },
                    getOnClickDeleteListener(),
                    getOnClickAddToCartListener()
                )
                rvWishList.adapter = adapter
            }
    }

    private fun getOnClickAddToCartListener(): WishListAdapter.OnClickAddToCart =
        object : WishListAdapter.OnClickAddToCart {

            override fun initButton(button: MaterialButton, productId: Int) {
                lifecycleScope.launch {
                    val isInCart = cartViewModel.isProductInUserCart(
                        productId,
                        userViewModel.getLoggedInUser().toInt()
                    )
                    if (isInCart) {
                        button.text = "Added To Cart"
                        button.icon = resources.getDrawable(R.drawable.ic_done_small, null)
                        button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                        button.iconTint = resources.getColorStateList(R.color.white, null)
                        button.backgroundTintList =
                            resources.getColorStateList(R.color.green1, null)
                        button.isEnabled = false
                    } else {
                        button.text = "Add to Cart"

                    }
                }
            }

            override fun onClickAdd(button: MaterialButton, productId: Int) {
                lifecycleScope.launch {
                    val isInCart = cartViewModel.isProductInUserCart(
                        productId,
                        userViewModel.getLoggedInUser().toInt()
                    )
                    if (isInCart) {
                        Navigation.findNavController(requireView())
                            .navigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                    } else {
                        cartViewModel.addProductToUserCart(
                            Cart(
                                productId = productId,
                                userId = userViewModel.getLoggedInUser().toInt()
                            )
                        )
                        button.text = "Added To Cart"
                        button.icon = resources.getDrawable(R.drawable.ic_done_small, null)
                        button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                        button.iconTint = resources.getColorStateList(R.color.white, null)
                        button.backgroundTintList =
                            resources.getColorStateList(R.color.green1, null)
                        button.isEnabled = false
                    }
                }
            }


        }

    private fun getOnClickDeleteListener(): WishListAdapter.OnClickDelete =
        object : WishListAdapter.OnClickDelete {
            override fun onDelete(productId: Int) {
                val userId = userViewModel.getLoggedInUser().toInt()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Are you sure you want to delete this product from Wishlist?")
                    .setPositiveButton("Delete") { _, _ ->
                        wishListViewModel.removeProductFromUserWishList(productId, userId)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .setCancelable(false)
                builder.create().show()
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchFragment -> {
                findNavController().navigate(WishListFragmentDirections.actionWishlistFragmentToSearchFragment())
                true
            }
            R.id.cartFragment -> {
                findNavController().navigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}