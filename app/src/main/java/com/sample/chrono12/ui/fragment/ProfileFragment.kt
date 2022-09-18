package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentProfileBinding
import com.sample.chrono12.databinding.LoginPromptBinding
import com.sample.chrono12.viewmodels.UserViewModel

class ProfileFragment : Fragment() {

    lateinit var bindingProfile: FragmentProfileBinding
    lateinit var bindingLoginPrompt: LoginPromptBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var isUserLoggedIn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if(isUserLoggedIn){
            bindingProfile = FragmentProfileBinding.inflate(layoutInflater)
            bindingProfile.root
        } else{
            bindingLoginPrompt = LoginPromptBinding.inflate(layoutInflater)
            bindingLoginPrompt.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn){
            setupProfile()
        }
        else{
            setupLoginPrompt()
        }
    }

    private fun setupProfile() {
        userViewModel.getUserDetails().observe(viewLifecycleOwner){ user ->
            bindingProfile.tvUserName.text = user.name
            bindingProfile.tvEmailId.text = user.email
            bindingProfile.tvMobileNum.text = user.mobile
        }
        bindingProfile.tvMyWishlist.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToWishlistFragment())
        }
        bindingProfile.tvMyOrder.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToOrderHistoryFragment())
        }
        bindingProfile.tvMyAddress.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToAddressFragment())
        }
        bindingProfile.tvMyAddressGroup.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToAddressGroupFragment())
        }
    }

    private fun setupLoginPrompt() {
        bindingLoginPrompt.btnLogIn.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToLogInFragment())
        }
        bindingLoginPrompt.toSignup.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToSignUpFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(isUserLoggedIn) inflater.inflate(R.menu.logout_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            Navigation.findNavController(requireView()).navigate(ProfileFragmentDirections.actionProfileFragmentToLogoutDialog())
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}