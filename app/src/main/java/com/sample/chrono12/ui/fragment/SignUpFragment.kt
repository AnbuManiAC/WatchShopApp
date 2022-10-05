package com.sample.chrono12.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.data.models.UserField.EMAIL
import com.sample.chrono12.data.models.UserField.MOBILE
import com.sample.chrono12.databinding.FragmentSignUpBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.utils.hideInput
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.utils.showKeyboard
import com.sample.chrono12.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFocusChangeListeners()
        setupForm()
        binding.btnSignup.setOnClickListener {
            userViewModel.clearUserFieldInfo()
            clearFormFocuses()
            initiateSignup()
        }

        binding.toLogIn.setOnClickListener {
            findNavController().safeNavigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
        }

        binding.tilEtCPassword.setOnEditorActionListener { v, actionId, _ ->
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

    private fun setupForm() {
        binding.tilEtEmail.setText(userViewModel.suggestedSignupEmail)
        binding.tilEtName.setText("")
        binding.tilEtMobile.setText("")
        binding.tilEtPassword.setText("")
        binding.tilEtCPassword.setText("")
    }

    private fun initiateSignup() {
        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner) { field ->
            field?.let {
                deliverFieldMessage(it)
                if (field.response == Response.SUCCESS) {
                    SharedPrefUtil.setUserId(requireActivity(), userViewModel.getLoggedInUser())
                    lifecycleScope.launch {
                        userViewModel.insertIntoAddressGroup(
                            AddressGroup(
                                userId = userViewModel.getLoggedInUser().toInt(),
                                groupName = getString(R.string.default_group_name)
                            )
                        )
                    }
                        findNavController().popBackStack(R.id.signUpFragment, true)
                        (requireActivity() as HomeActivity).enableCartBadge()


                }
            }
        }
        cancelErrors()
        if (inputCheck()) {
            val name = binding.tilEtName.text.toString()
            val email = binding.tilEtEmail.text.toString()
            val mobile = binding.tilEtMobile.text.toString()
            val password = binding.tilEtPassword.text.toString()
            val user = User(0, name, email, mobile, password, null)
            userViewModel.initiateSignUp(user)
        }
    }

    private fun deliverFieldMessage(field: UserField) {
        when (field) {
            EMAIL -> binding.tilEmail.error = field.response.message
            MOBILE -> binding.tilMobile.error = field.response.message
            else -> {
            }
        }
    }

    private fun clearFormFocuses() {
        binding.tilEtEmail.clearFocus()
        binding.tilEtMobile.clearFocus()
        binding.tilEtName.clearFocus()
        binding.tilEtPassword.clearFocus()
        binding.tilEtCPassword.clearFocus()
    }

    private fun cancelErrors() {
        binding.tilEtName.error = null
        binding.tilEmail.error = null
        binding.tilMobile.error = null
        binding.tilPassword.error = null
        binding.tilCPassword.error = null
    }

    private fun inputCheck(): Boolean {
        return isFieldsNotEmpty() && emailCheck() && mobileCheck() && passwordCheck()
    }

    private fun passwordCheck(): Boolean {
        val password = binding.tilEtPassword.text.toString()
        val confirmPassword = binding.tilEtCPassword.text.toString()
        if (password != confirmPassword) {
            val passwordInfo = getString(R.string.password_doesnt_match)
            binding.tilPassword.error = passwordInfo
            binding.tilCPassword.error = passwordInfo
            return false
        }
        if (password.length < 8) {
            val passwordInfo = getString(R.string.password_min_character)
            binding.tilPassword.error = passwordInfo
            binding.tilCPassword.error = passwordInfo
            return false
        }
        return true
    }

    private fun mobileCheck(): Boolean {
        val mobile = binding.tilEtMobile.text.toString()
        if (!mobile.isDigitsOnly() || mobile.length != 10 || mobile.first().toString()
                .toInt() !in listOf(6, 7, 8, 9)
        ) {
            val mobileInfo = getString(R.string.not_a_mobile)
            binding.tilMobile.error = mobileInfo
            return false
        }
        return true
    }

    private fun emailCheck(): Boolean {
        val email = binding.tilEtEmail.text.toString()
        val isEmailPattern = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        if (!isEmailPattern) {
            val emailInfo = getString(R.string.not_an_email)
            binding.tilEmail.error = emailInfo
        }
        return isEmailPattern
    }

    private fun isFieldsNotEmpty(): Boolean {
        val name = binding.tilEtName.text
        val email = binding.tilEtEmail.text
        val mobile = binding.tilEtMobile.text
        val password = binding.tilEtPassword.text
        val confirmPassword = binding.tilEtCPassword.text
        val emptyInfo = getString(R.string.field_cant_be_empty)
        if (name.isNullOrEmpty()) {
            binding.tilName.error = emptyInfo
        }
        if (email.isNullOrEmpty()) {
            binding.tilEmail.error = emptyInfo
        }
        if (mobile.isNullOrEmpty()) {
            binding.tilMobile.error = emptyInfo
        }
        if (password.isNullOrEmpty()) {
            binding.tilPassword.error = emptyInfo
        }
        if (confirmPassword.isNullOrEmpty()) {
            binding.tilCPassword.error = emptyInfo
        }
        return (!name.isNullOrEmpty() &&
                !email.isNullOrEmpty() &&
                !mobile.isNullOrEmpty() &&
                !password.isNullOrEmpty() &&
                !confirmPassword.isNullOrEmpty())
    }

    private fun setUpFocusChangeListeners() {
        binding.tilEtName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilName.error = null
                binding.tilEtName.showKeyboard()
            }
            setIconColor(
                binding.tilName,
                hasFocus
            )
        }
        binding.tilEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilEmail.error = null
            }
            setIconColor(
                binding.tilEmail,
                hasFocus
            )
        }
        binding.tilEtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilMobile.error = null
            setIconColor(
                binding.tilMobile,
                hasFocus
            )
        }
        binding.tilEtPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
            setIconColor(
                binding.tilPassword,
                hasFocus
            )
        }
        binding.tilEtCPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilCPassword.error = null
            setIconColor(
                binding.tilCPassword,
                hasFocus
            )
        }
    }

    private fun setIconColor(textInputLayout: TextInputLayout, hasFocus: Boolean) {
        val color = if (hasFocus) ResourcesCompat.getColor(
            resources,
            R.color.buttonColor,
            requireContext().theme
        ) else ResourcesCompat.getColor(resources, R.color.unselected, requireContext().theme)
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearUserFieldInfo()
        userViewModel.suggestedSignupEmail = ""
    }
}