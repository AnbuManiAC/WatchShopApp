package com.sample.chrono12.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.databinding.ActivityMainBinding
import com.sample.chrono12.databinding.FragmentAddressBinding
import com.sample.chrono12.databinding.FragmentNewAddressBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.viewmodels.UserViewModel

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddAddress.setOnClickListener{
            Navigation.findNavController(requireView()).navigate(AddressFragmentDirections.actionAddressFragmentToNewAddressFragment())
        }
        setupAddressAdapter()
    }

    private fun setupAddressAdapter() {
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getUserAddresses(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner){
                val adapter = AddressAdapter(
                    it.addressList,
                    getOnAddressButtonClickListener()
                )
                binding.rvAddress.adapter = adapter
            }
    }

    private fun getOnAddressButtonClickListener() =
        object : AddressAdapter.OnClickAddressButton {
            override fun onClickRemove(address: Address) {

            }

            override fun onClickEdit(address: Address) {

            }

        }

}
