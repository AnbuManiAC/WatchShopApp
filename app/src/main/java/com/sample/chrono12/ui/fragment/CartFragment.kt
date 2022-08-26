package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentCartBinding
import com.sample.chrono12.databinding.LoginCartDialogBinding
import com.sample.chrono12.viewmodels.UserViewModel

class CartFragment : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var cartDialogBinding: LoginCartDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var isUserLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        if(isUserLoggedIn){
            fragmentCartBinding = FragmentCartBinding.inflate(layoutInflater)
            return fragmentCartBinding.root
        }
        else{
            cartDialogBinding = LoginCartDialogBinding.inflate(layoutInflater)
            return cartDialogBinding.root
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fav_menu, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isUserLoggedIn){
            Log.d("cart","In cart")
            setupCart()
        }
        else{
            Log.d("cart","In missing cart")
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

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToSearchFragment())
                true
            }
            R.id.favoriteFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToFavoriteFragment()  )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}