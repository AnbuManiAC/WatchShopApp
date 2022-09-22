package com.sample.chrono12.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.sample.chrono12.R
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.databinding.FragmentLogInBinding
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.viewmodels.UserViewModel

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner) { field ->
            field?.let {
                deliverFieldMessage(it)
                if (field.response == Response.SUCCESS) {
//                    val sharedPref = requireActivity().getSharedPreferences(
//                        getString(R.string.user_pref),
//                        Context.MODE_PRIVATE
//                    )
//                    val editor = sharedPref?.edit()
//                    editor?.let {
//                        editor.putLong(getString(R.string.user_id), userViewModel.getLoggedInUser())
//                        editor.apply()
//                    }
                    SharedPrefUtil.setUserId(requireActivity(), userViewModel.getLoggedInUser())
                    Navigation.findNavController(view).popBackStack(R.id.logInFragment, true)
                }
            }
        }


        binding.toSignup.setOnClickListener {
            userViewModel.clearUserFieldInfo()
            cancelErrors()
            Navigation.findNavController(requireView())
                .navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }
        binding.btnLogin.setOnClickListener {
            clearFormFocus()
            val email = binding.tiEtEmail.text.toString()
            val password = binding.tiEtPassword.text.toString()
            if (isInputNonNull(email, password)) {
                userViewModel.authenticateUser(email, password)
            }else{
                if(email.isEmpty()) binding.tilLoginName.error = "This field can't be empty"
                if(password.isEmpty()) binding.tilLoginPassword.error = "This field can't be empty"
            }
        }

        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.tilLoginPassword.error = null
        }
        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.tilLoginName.error = null
        }
    }


    private fun cancelErrors() {
        binding.tilLoginName.error = null
        binding.tilLoginPassword.error = null
    }

    private fun isInputNonNull(email: String, password: String): Boolean =
        email.isNotEmpty() && password.isNotEmpty()

    private fun clearFormFocus() {
        binding.tiEtEmail.clearFocus()
        binding.tiEtPassword.clearFocus()
    }

    private fun deliverFieldMessage(field: UserField) {
        when(field){
            UserField.EMAIL -> binding.tilLoginName.error = field.response.message
            UserField.PASSWORD -> binding.tilLoginPassword.error = field.response.message
            else -> {
                Toast.makeText(requireContext(), field.response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearUserFieldInfo()
    }

}