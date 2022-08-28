package com.sample.chrono12.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.WithHint
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentWishlistBinding
import com.sample.chrono12.databinding.LoginPromptBinding
import com.sample.chrono12.ui.adapter.WishListAdapter
import com.sample.chrono12.viewmodels.UserViewModel
import com.sample.chrono12.viewmodels.WishListViewModel

class WishListFragment : Fragment() {

    private lateinit var fragmentWishListBinding: FragmentWishlistBinding
    private lateinit var loginPromptBinding: LoginPromptBinding
    private var isUserLoggedIn = false
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val wishListViewModel by lazy { ViewModelProvider(requireActivity())[WishListViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if(isUserLoggedIn){
            wishListViewModel.setWishListItems(userViewModel.getLoggedInUser().toInt())
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
        wishListViewModel.getWishListItems().observe(viewLifecycleOwner){
            val adapter = WishListAdapter(it){ product ->
                Navigation.findNavController(requireView()).navigate(WishListFragmentDirections.actionWishlistFragmentToProductFragment(product.productId))
            }
            rvWishList.adapter =adapter
        }
    }


}