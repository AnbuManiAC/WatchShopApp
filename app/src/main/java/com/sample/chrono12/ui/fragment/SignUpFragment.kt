package com.sample.chrono12.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.databinding.FragmentLogInBinding
import com.sample.chrono12.databinding.FragmentSignUpBinding
import com.sample.chrono12.viewmodels.UserViewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner){ field ->
            field?.let {
                if(field.response == Response.SUCCESS){
                    val sharedPref = activity?.getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
                    val editor = sharedPref?.edit()
                    editor?.let {
                        editor.putLong(getString(R.string.user_id), userViewModel.getLoggedInUser())
                        editor.apply()
                        Log.d("hjkl","hjk2")
                    }
                    Navigation.findNavController(view).popBackStack(R.id.logInFragment, true)
                }
            }
        }

        binding.toLogIn.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
        }

        binding.btnSignup.setOnClickListener {
            val username = binding.tilEtName.text.toString()
            val emailId = binding.tilEtEmail.text.toString()
            val mobile = binding.tilEtMobile.text.toString()
            val password = binding.tilEtPassword.text.toString()
            val user = User(name = username, email = emailId, mobileNumber = mobile, password = password)
            userViewModel.initiateSignUp(user)
        }
    }



}