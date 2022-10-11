package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.databinding.FragmentWishlistBinding
import com.sample.chrono12.databinding.LoginPromptCartWishlistDialogBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.WishListAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import com.sample.chrono12.viewmodels.WishListViewModel
import kotlinx.coroutines.launch

class WishListFragment : Fragment() {

    private lateinit var fragmentWishListBinding: FragmentWishlistBinding
    private lateinit var loginPromptBinding: LoginPromptCartWishlistDialogBinding
    private var isUserLoggedIn = false

    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val wishListViewModel by lazy { ViewModelProvider(requireActivity())[WishListViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
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
        setupMenu()
        if (isUserLoggedIn) {
            setupWishList()
        } else {
            setupLoginPrompt()
        }
    }

    private fun setupLoginPrompt() {
        loginPromptBinding.ivMissingCart.setImageResource(R.drawable.missing_wishlist)
        loginPromptBinding.tvContinueShopping.setOnClickListener {
            findNavController().navigateUp()
        }
        loginPromptBinding.btnLogIn.setOnClickListener {
            findNavController().safeNavigate(
                WishListFragmentDirections.actionWishlistFragmentToLogInFragment()
            )
        }
    }

    private fun setupWishList() {

        val adapter = WishListAdapter(
            {
                findNavController().safeNavigate(
                    WishListFragmentDirections.actionWishlistFragmentToProductFragment(it.productId)
                )
            },
            getOnClickDeleteListener(),
            getOnClickAddToCartListener()
        )
        adapter.setData(mutableListOf())
        val rvWishList = fragmentWishListBinding.rvWishlist
        rvWishList.layoutManager = LinearLayoutManager(requireActivity())
        rvWishList.adapter = adapter
        fragmentWishListBinding.btnGoHome.setOnClickListener {
            findNavController().navigateUp()
        }
        wishListViewModel.getWishListItems(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    setTitle(0)
                    fragmentWishListBinding.emptyWishlist.visibility = View.VISIBLE
                }else{
                    setTitle(it.size)
                    fragmentWishListBinding.emptyWishlist.visibility = View.GONE
                }
                adapter.setNewData(it)
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
                        changeButtonToText(button)
                    } else {
                        button.text = getString(R.string.add_to_cart)
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
                        findNavController().safeNavigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                    } else {
                        cartViewModel.addProductToUserCart(
                            Cart(
                                productId = productId,
                                userId = userViewModel.getLoggedInUser().toInt()
                            )
                        )
                        changeButtonToText(button)
                    }
                }
            }
        }

    private fun changeButtonToText(button: MaterialButton) {
        button.text = getString(R.string.in_cart)
        button.icon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_done_small,
            requireContext().theme
        )
        button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        button.iconTint = resources.getColorStateList(R.color.white, null)
        button.backgroundTintList =
            resources.getColorStateList(R.color.green1, null)
        button.isEnabled = false
    }

    private fun getOnClickDeleteListener(): WishListAdapter.OnClickDelete =
        object : WishListAdapter.OnClickDelete {
            override fun onDelete(productId: Int) {
                val userId = userViewModel.getLoggedInUser().toInt()
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.remove_item))
                    .setMessage(getString(R.string.wishlist_item_remove_alert))
                    .setPositiveButton(getString(R.string.remove)) { _, _ ->
                        wishListViewModel.removeProductFromUserWishList(productId, userId)
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                    }
                    .setCancelable(false)
                builder.create().show()
            }
        }

    private fun setTitle(size: Int){
        if(size>0){
            (requireActivity() as HomeActivity).setActionBarTitle(getString(R.string.wishlist_title, size))
        }else{
            (requireActivity() as HomeActivity).setActionBarTitle(getString(R.string.wishlist))
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_cart_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchFragment -> {
                        findNavController().safeNavigate(WishListFragmentDirections.actionWishlistFragmentToSearchFragment())
                        true
                    }
                    R.id.cartFragment -> {
                        findNavController().safeNavigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}