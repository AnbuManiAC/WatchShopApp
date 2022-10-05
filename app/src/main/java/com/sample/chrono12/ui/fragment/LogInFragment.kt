package com.sample.chrono12.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.sample.chrono12.R
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.databinding.FragmentLogInBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.utils.hideInput
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.utils.showKeyboard
import com.sample.chrono12.viewmodels.UserViewModel

class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLogInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        setUpFocusChangeListeners()

        binding.toSignup.setOnClickListener {
            userViewModel.clearUserFieldInfo()
            cancelErrors()
            findNavController().safeNavigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
        }

        binding.btnLogin.setOnClickListener {
            clearFormFocus()
            initiateLogin()
        }

        binding.tiEtPassword.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    v.hideInput()
                    true
                }
                else -> false
            }
        }
    }

    private fun setUpFocusChangeListeners() {
        binding.tiEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) binding.tilLoginPassword.error = null
            setIconColor(
                binding.tilLoginPassword,
                hasFocus
            )
        }
        binding.tiEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                binding.tilLoginName.error = null
                binding.tiEtEmail.showKeyboard()
            }
            setIconColor(
                binding.tilLoginName,
                hasFocus
            )
        }
    }

    private fun initiateLogin() {
        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner) { field ->
            field?.let {
                deliverFieldMessage(it)
                if (field.response == Response.SUCCESS) {
                    SharedPrefUtil.setUserId(requireActivity(), userViewModel.getLoggedInUser())
                    findNavController().popBackStack(R.id.logInFragment, true)
                    (requireActivity() as HomeActivity).enableCartBadge()
                }
            }
        }
        val email = binding.tiEtEmail.text.toString()
        val password = binding.tiEtPassword.text.toString()
        if (isInputNonNull(email, password)) {
            if (emailCheck()){
                userViewModel.authenticateUser(email, password)
            }
        }
        else{
            if(email.isEmpty()) binding.tilLoginName.error = getString(R.string.field_cant_be_empty)
            if(password.isEmpty()) binding.tilLoginPassword.error = getString(R.string.field_cant_be_empty)
        }
    }

    private fun setupForm() {
        binding.tiEtEmail.setText(userViewModel.suggestedLoginEmail)
        binding.tiEtPassword.setText("")
    }

    private fun setIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val color = if (hasFocus) ResourcesCompat.getColor(
            resources,
            R.color.buttonColor,
            requireContext().theme
        ) else ResourcesCompat.getColor(resources, R.color.unselected, requireContext().theme)
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    private fun emailCheck(): Boolean {
        val email = binding.tiEtEmail.text.toString()
        val isEmailPattern = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailPattern) {
            val emailInfo = getString(R.string.not_an_email)
            binding.tilLoginName.error = emailInfo
        }
        return isEmailPattern
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
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearUserFieldInfo()
        userViewModel.suggestedLoginEmail = ""
    }

}