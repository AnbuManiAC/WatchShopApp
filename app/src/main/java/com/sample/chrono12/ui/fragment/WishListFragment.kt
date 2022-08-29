package com.sample.chrono12.ui.fragment

import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.databinding.FragmentWishlistBinding
import com.sample.chrono12.databinding.LoginPromptBinding
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.ui.adapter.WishListAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.ProductViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import com.sample.chrono12.viewmodels.WishListViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class WishListFragment : Fragment() {

    private lateinit var fragmentWishListBinding: FragmentWishlistBinding
    private lateinit var loginPromptBinding: LoginPromptBinding
    private var isUserLoggedIn = false

    private val productViewModel by lazy { ViewModelProvider(requireActivity())[ProductViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val wishListViewModel by lazy { ViewModelProvider(requireActivity())[WishListViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        setHasOptionsMenu(true)
        return if(isUserLoggedIn){
            fragmentWishListBinding = FragmentWishlistBinding.inflate(layoutInflater)
            fragmentWishListBinding.root
        } else{
            loginPromptBinding = LoginPromptBinding.inflate(layoutInflater)
            loginPromptBinding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isUserLoggedIn){
            setupWishList()
        }
        else{
            setupLoginPrompt()
        }
    }

    private fun setupLoginPrompt() {
        loginPromptBinding.btnLogIn.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(WishListFragmentDirections.actionWishlistFragmentToLogInFragment())
        }
    }

    private fun setupWishList() {
        val rvWishList = fragmentWishListBinding.rvWishlist
        rvWishList.layoutManager = LinearLayoutManager(activity)
        wishListViewModel.getWishListItems(userViewModel.getLoggedInUser().toInt()).observe(viewLifecycleOwner){ it ->
            val adapter = WishListAdapter(
                it,
                {
                    Navigation.findNavController(requireView()).navigate(WishListFragmentDirections.actionWishlistFragmentToProductFragment(it.productId))
                },
                getOnClickDeleteListener(),
                getOnClickAddToCartListener()
            )
            rvWishList.adapter =adapter
        }
    }

    private fun getOnClickAddToCartListener(): WishListAdapter.OnClickAddToCart
    = object : WishListAdapter.OnClickAddToCart{

        override fun initButton(button: Button, productId: Int) {
            lifecycleScope.launch{
                val isInCart = cartViewModel.isProductInUserCart(productId, userViewModel.getLoggedInUser().toInt())
                if(isInCart){
                    button.text = "View Cart"
                }else{
                    button.text = "Add to Cart"
                }
            }
        }

        override fun onClickAdd(button: Button, productId: Int) {
            lifecycleScope.launch{
                val isInCart = cartViewModel.isProductInUserCart(productId, userViewModel.getLoggedInUser().toInt())
                if(isInCart){
                    Navigation.findNavController(requireView()).navigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                }else{
                    cartViewModel.addProductToUserCart(Cart(productId = productId, userId = userViewModel.getLoggedInUser().toInt()))
                    button.text = "View Cart"
                }
            }
        }


    }

    private fun getOnClickDeleteListener(): WishListAdapter.OnClickDelete
    = object : WishListAdapter.OnClickDelete{
        override fun onDelete(productId: Int) {
            val userId = userViewModel.getLoggedInUser().toInt()
            wishListViewModel.removeProductFromUserWishList(productId, userId)

            Snackbar.make(requireView(), "Removed Item from WishList", Snackbar.LENGTH_SHORT)
                .setAction("Undo",
                View.OnClickListener {
                    wishListViewModel.addProductToUserWishList(WishList(userId = userId, productId = productId) )
                }).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment ->{
                findNavController().navigate(WishListFragmentDirections.actionWishlistFragmentToSearchFragment())
                true
            }
            R.id.cartFragment ->{
                findNavController().navigate(WishListFragmentDirections.actionWishlistFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}