package com.sample.chrono12.ui.fragment

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
import com.sample.chrono12.data.entities.Cart
import com.sample.chrono12.data.entities.Product
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.databinding.FragmentCartBinding
import com.sample.chrono12.databinding.LoginCartDialogBinding
import com.sample.chrono12.ui.adapter.CartAdapter
import com.sample.chrono12.ui.adapter.WishListAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import java.io.LineNumberReader

class CartFragment : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var cartDialogBinding: LoginCartDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private var isUserLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if(isUserLoggedIn){
            fragmentCartBinding = FragmentCartBinding.inflate(layoutInflater)
            fragmentCartBinding.root
        } else{
            cartDialogBinding = LoginCartDialogBinding.inflate(layoutInflater)
            cartDialogBinding.root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fav_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isUserLoggedIn){
            setupCart()
        }
        else{
            setupCartMissing()
        }
    }

    private fun setupCartMissing() {
        cartDialogBinding.btnLogIn.setOnClickListener {
            Log.d("cart","In missing cart nav")
            Navigation.findNavController(requireView()).navigate(CartFragmentDirections.actionCartFragmentToLogInFragment())
        }
        cartDialogBinding.tvContinueShopping.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(CartFragmentDirections.actionCartFragmentToHomeFragment())
        }
    }

    private fun setupCart() {
        cartViewModel.getTotalCurrentPrice().observe(viewLifecycleOwner){
            fragmentCartBinding.tvTotalCurrentPrice.text = it?.toString()
        }
        cartViewModel.getTotalOriginPrice().observe(viewLifecycleOwner){
            fragmentCartBinding.tvTotalPrice.apply {
                text = it?.toString()
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

        }
        fragmentCartBinding.rvCart.layoutManager = LinearLayoutManager(activity)
        fragmentCartBinding.btnGoHome.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(CartFragmentDirections.actionCartFragmentToHomeFragment())
        }
        cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt()).observe(viewLifecycleOwner){
            cartViewModel.initPriceCalculating(it)
            if(it.isNotEmpty()){
                fragmentCartBinding.ivEmptyCart.visibility = View.GONE
                fragmentCartBinding.tvEmptyCart.visibility = View.GONE
                fragmentCartBinding.tvEmptyCartDesc.visibility = View.GONE
                fragmentCartBinding.btnGoHome.visibility = View.GONE
                fragmentCartBinding.layoutPriceOrder.visibility = View.VISIBLE
            }
            if(it.isEmpty()){
                fragmentCartBinding.ivEmptyCart.visibility = View.VISIBLE
                fragmentCartBinding.tvEmptyCart.visibility = View.VISIBLE
                fragmentCartBinding.tvEmptyCartDesc.visibility = View.VISIBLE
                fragmentCartBinding.btnGoHome.visibility = View.VISIBLE
                fragmentCartBinding.layoutPriceOrder.visibility = View.GONE
            }
            fragmentCartBinding.rvCart.adapter = CartAdapter(
                it,
                {
                    Navigation.findNavController(requireView()).navigate(CartFragmentDirections.actionCartFragmentToProductFragment(it.productId))
                },
                getOnDeleteClickListener(),
                getOnQuantityClickListener()
            )
        }
    }

    private fun getOnDeleteClickListener(): CartAdapter.OnClickDelete
            = object : CartAdapter.OnClickDelete{
        override fun onDelete(productId: Int, quantity: Int) {
            val userId = userViewModel.getLoggedInUser().toInt()
            cartViewModel.removeProductFromUserCart(productId, userId)
            Snackbar.make(requireView(), "Removed Item from Cart", Snackbar.LENGTH_SHORT)
                .setAction("Undo"
                ) {
                    cartViewModel.addProductToUserCart(Cart(userId = userId, productId = productId, quantity = quantity))
                }.show()
        }
    }

    private fun getOnQuantityClickListener(): CartAdapter.OnClickQuantity
     = object : CartAdapter.OnClickQuantity{
        override fun onClickPlus(product: Product, quantity: Int): Boolean {
            return if(quantity<5 && product.stockCount>quantity){
                cartViewModel.updateQuantity(product.productId, userViewModel.getLoggedInUser().toInt(), quantity+1)
                true
            } else{
                Snackbar.make(requireView(), "5 Units per Product Only", Snackbar.LENGTH_SHORT).show()
                false
            }
        }

        override fun onClickMinus(product: Product, quantity: Int): Boolean {
            return if(quantity>1){
                cartViewModel.updateQuantity(product.productId, userViewModel.getLoggedInUser().toInt(), quantity-1)
                true
            } else{
                Snackbar.make(requireView(), "Reached lower limit", Snackbar.LENGTH_SHORT).show()
                false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToSearchFragment())
                true
            }
            R.id.wishlistFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToWishlistFragment()  )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}