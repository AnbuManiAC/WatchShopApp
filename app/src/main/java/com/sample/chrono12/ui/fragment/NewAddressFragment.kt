package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.databinding.FragmentNewAddressBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.viewmodels.UserViewModel

class NewAddressFragment : Fragment() {

    private lateinit var binding: FragmentNewAddressBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<NewAddressFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (navArgs.addressId > 0)
            userViewModel.setAddress(navArgs.addressId)
        binding = FragmentNewAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setUpFocusChangeListeners()
        if (navArgs.addressId > 0) {
            (requireActivity() as HomeActivity).setActionBarTitle("Address")
            userViewModel.getAddress().observe(viewLifecycleOwner) { address ->
                val doorAndStreet = address.addressLine1.split("___")
                with(binding) {
                    tilEtAddressName.setText(address.contactName)
                    tilEtDoorNum.setText(doorAndStreet[0])
                    tilEtStreet.setText(doorAndStreet[1])
                    tilEtLandmark.setText(address.addressLine2)
                    tilEtCity.setText(address.city)
                    tilEtState.setText(address.state)
                    tilEtPincode.setText(address.pincode.toString())
                    tilEtMobile.setText(address.contactNumber.toString())
                }
            }
        } else {
            (requireActivity() as HomeActivity).setActionBarTitle("New Address")
        }
        setupStatesDropDownAdapter()
    }

    private fun setupStatesDropDownAdapter() {
        val states = resources.getStringArray(R.array.india_states)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.state_name_item, states)
        binding.tilEtState.setAdapter(arrayAdapter)
    }

    private fun cancelErrors() {
        binding.tilAddressName.error = null
        binding.tilDoorNum.error = null
        binding.tilStreet.error = null
        binding.tilLandmark.error = null
        binding.tilCity.error = null
        binding.tilPincode.error = null
        binding.tilMobile.error = null
    }


    private fun clearFocus() {
        binding.tilEtAddressName.clearFocus()
        binding.tilEtDoorNum.clearFocus()
        binding.tilEtStreet.clearFocus()
        binding.tilEtLandmark.clearFocus()
        binding.tilEtCity.clearFocus()
        binding.tilEtState.clearFocus()
        binding.tilEtPincode.clearFocus()
        binding.tilEtMobile.clearFocus()
    }

    private fun checkInput(): Boolean =
        isFieldsNotEmpty() && pincodeCheck() && mobileCheck()

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


    private fun isFieldsNotEmpty(): Boolean {
        val name = binding.tilEtAddressName
        val doorNum = binding.tilEtDoorNum
        val street = binding.tilEtStreet
        val city = binding.tilEtCity
        val state = binding.tilEtState
        val pincode = binding.tilEtPincode
        val mobile = binding.tilEtMobile
        val emptyErrorMsg = "Thid field can't be empty"

        val isNameEmpty = name.text.isNullOrEmpty()
        val isDoorNumEmpty = doorNum.text.isNullOrEmpty()
        val isStreetEmpty = street.text.isNullOrEmpty()
        val isCityEmpty = city.text.isNullOrEmpty()
        val isStateEmpty = state.text.isNullOrEmpty()
        val isPincodeEmpty = pincode.text.isNullOrEmpty()
        val isMobileEmpty = mobile.text.isNullOrEmpty()

        if (isNameEmpty) binding.tilAddressName.error = emptyErrorMsg
        if (isDoorNumEmpty) binding.tilDoorNum.error = emptyErrorMsg
        if (isStreetEmpty) binding.tilStreet.error = emptyErrorMsg
        if (isCityEmpty) binding.tilCity.error = emptyErrorMsg
        if (isStateEmpty) binding.tilState.error = emptyErrorMsg
        if (isPincodeEmpty) binding.tilPincode.error = emptyErrorMsg
        if (isMobileEmpty) binding.tilMobile.error = emptyErrorMsg

        return (!isNameEmpty && !isDoorNumEmpty && !isStreetEmpty
                 && !isCityEmpty && !isPincodeEmpty
                && !isStateEmpty && !isMobileEmpty)
    }

    private fun pincodeCheck(): Boolean {
        val pincode = binding.tilEtPincode.text.toString()
        var isValidPincode = pincode.isDigitsOnly() && pincode.length == 6
        if (!isValidPincode && pincode.toInt() < 100_000) {
            binding.tilPincode.error = "Invalid pincode"
        }
        return isValidPincode
    }

    private fun addUserAddress() {
        val name = binding.tilEtAddressName.text.toString()
        val addressLine1 =
            binding.tilEtDoorNum.text.toString() + "___" + binding.tilEtStreet.text.toString()
        val addressLine2 = binding.tilEtLandmark.text.toString()
        val city = binding.tilEtCity.text.toString()
        val state = binding.tilEtState.text.toString()
        val pincode = binding.tilEtPincode.text.toString().toInt()
        val mobile = binding.tilEtMobile.text.toString()
        val address = if (navArgs.addressId > 0) {
            Address(
                addressId = navArgs.addressId,
                contactName = name,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                pincode = pincode,
                contactNumber = mobile
            )
        } else {
            Address(
                contactName = name,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                city = city,
                state = state,
                pincode = pincode,
                contactNumber = mobile
            )
        }
//        userViewModel.insertAddress(address)
//        userViewModel.getAddressId().observe(viewLifecycleOwner) {
//            it?.let {
//                userViewModel.insertIntoAddressAndGroupCrossRef(it, navArgs.addressGroupName)
//            }
//        }
        userViewModel.insertAddress(address, navArgs.addressGroupName)

    }

    private fun setUpFocusChangeListeners() {
        binding.tilEtAddressName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilAddressName.error = null
        }
        binding.tilEtDoorNum.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilDoorNum.error = null
        }
        binding.tilEtStreet.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilStreet.error = null
        }
        binding.tilEtLandmark.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilLandmark.error = null
        }
        binding.tilEtCity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilCity.error = null
        }
        binding.tilEtState.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilState.error = null
        }
        binding.tilEtPincode.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPincode.error = null
        }
        binding.tilEtMobile.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilMobile.error = null
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_close)
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.done_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.done) {
                    cancelErrors()
                    clearFocus()
                    if (checkInput()) {
                        addUserAddress()
                        if (findNavController().currentDestination?.id == R.id.newAddressFragment)
                            findNavController().navigateUp()
                    }
                    return true
                }
                return false
            }
        },viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onPause() {
        super.onPause()
        userViewModel.getAddress().removeObservers(viewLifecycleOwner)
    }

    override fun onDestroy() {
        userViewModel.clearAddressId()
        super.onDestroy()
    }
}