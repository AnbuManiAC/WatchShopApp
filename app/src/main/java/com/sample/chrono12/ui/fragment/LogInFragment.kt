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
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.databinding.FragmentLogInBinding
import com.sample.chrono12.viewmodels.UserViewModel

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onAttach(context: Context) {
        Log.d("cart", "Login1")
        super.onAttach(context)
        Log.d("cart", "Login2")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("cart", "Login3")
        binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("cart", "Login4")
        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner){ field ->
            Log.d("hjkl","hjkl")
            field?.let {
                if(field.response == Response.SUCCESS){
                    Log.d("hjkl","hjkl1")
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


        binding.toSignup.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }
        binding.btnLogin.setOnClickListener {
            userViewModel.authenticateUser(binding.tiEtEmail.text.toString(), binding.tiEtPassword.text.toString())
        }
    }

    override fun onDestroy() {
        Log.d("cart", "Login5")
        super.onDestroy()
        userViewModel.clearUserFieldInfo()
    }

}