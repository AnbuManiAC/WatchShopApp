package com.sample.chrono12.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.AddressGroup
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.data.models.Response
import com.sample.chrono12.data.models.UserField.*
import com.sample.chrono12.data.models.UserField
import com.sample.chrono12.databinding.FragmentSignUpBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.utils.SharedPrefUtil
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
        binding.btnSignup.setOnClickListener {
            userViewModel.clearUserFieldInfo()
            clearFormFocuses()
            initiateSignup()
        }
        binding.tilEtCPassword.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    v.clearFocus()
                    hideInput(v)
                    true
                }
                else -> false
            }
        }


        binding.toLogIn.setOnClickListener{
            if(findNavController().currentDestination?.id == R.id.signUpFragment)
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLogInFragment())
        }
    }

    private fun initiateSignup() {
        userViewModel.getUserFieldInfo().observe(viewLifecycleOwner){ field ->
            field?.let {
                deliverFieldMessage(it)
                if(field.response == Response.SUCCESS){
//                    val sharedPref = requireActivity().getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
//                    val editor = sharedPref?.edit()
//                    editor?.let {
//                        editor.putLong(getString(R.string.user_id), userViewModel.getLoggedInUser())
//                        editor.apply()
//                    }
                    SharedPrefUtil.setUserId(requireActivity(), userViewModel.getLoggedInUser())
                    lifecycleScope.launch{
                        userViewModel.insertIntoAddressGroup(AddressGroup(userId = userViewModel.getLoggedInUser().toInt(), groupName = "default"))
                    }
                    if(findNavController().currentDestination?.id == R.id.signUpFragment)
                    {
                        findNavController().popBackStack(R.id.signUpFragment, true)
                        (requireActivity() as HomeActivity).enableCartBadge()
                    }

                }
            }
        }
        cancelErrors()
        if(inputCheck()){
            val name = binding.tilEtName.text.toString()
            val email = binding.tilEtEmail.text.toString()
            val mobile = binding.tilEtMobile.text.toString()
            val password = binding.tilEtPassword.text.toString()
            val user = User(0,name,email,mobile,password,null)
            userViewModel.initiateSignUp(user)
        }
    }

    private fun deliverFieldMessage(field: UserField) {
        when(field){
            EMAIL -> binding.tilEmail.error = field.response.message
            MOBILE -> binding.tilMobile.error = field.response.message
            ALL -> {
                Toast.makeText(requireContext(), field.response.message, Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireContext(), field.response.message, Toast.LENGTH_SHORT).show()
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
        if (password != confirmPassword){
            val passwordInfo = getString(R.string.password_doesnt_match)
            binding.tilPassword.error = passwordInfo
            binding.tilCPassword.error = passwordInfo
            return false
        }
        if(password.length<8){
            val passwordInfo = getString(R.string.password_min_character)
            binding.tilPassword.error = passwordInfo
            binding.tilCPassword.error = passwordInfo
            return false
        }
        return true
    }

    private fun mobileCheck(): Boolean {
        val mobile = binding.tilEtMobile.text.toString()
        if ( !mobile.isDigitsOnly() || mobile.length!=10 || mobile.first().toString().toInt() !in listOf(6,7,8,9)){
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
        if(name.isNullOrEmpty()) {
            binding.tilName.error = emptyInfo
        }
        if(email.isNullOrEmpty()) {
            binding.tilEmail.error = emptyInfo
        }
        if(mobile.isNullOrEmpty()) {
            binding.tilMobile.error = emptyInfo
        }
        if(password.isNullOrEmpty()) {
            binding.tilPassword.error = emptyInfo
        }
        if(confirmPassword.isNullOrEmpty()) {
            binding.tilCPassword.error = emptyInfo
        }
        return (!name.isNullOrEmpty() &&
                !email.isNullOrEmpty() &&
                !mobile.isNullOrEmpty() &&
                !password.isNullOrEmpty() &&
                !confirmPassword.isNullOrEmpty())
    }

    private fun setUpFocusChangeListeners(){
        binding.tilEtName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilName.error = null
            setIconColor(
                binding.tilName,
                hasFocus
            )
        }
        binding.tilEtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
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
        val colorFocussed =
            ResourcesCompat.getColor(resources, R.color.primaryColor, null)
        val colorNonFocussed = ResourcesCompat.getColor(resources, R.color.unselected, null)
        val color = if (hasFocus) colorFocussed else colorNonFocussed
        textInputLayout.setStartIconTintList(ColorStateList.valueOf(color))
    }

    private fun hideInput(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clearUserFieldInfo()
    }
}