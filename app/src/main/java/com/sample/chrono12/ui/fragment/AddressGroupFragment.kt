package com.sample.chrono12.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressGroupBinding
import com.sample.chrono12.ui.adapter.AddressGroupAdapter
import com.sample.chrono12.viewmodels.UserViewModel


class AddressGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddressGroupBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddressGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddressGroupAdapter()
    }

    private fun setupAddressGroupAdapter() {
        binding.rvAddressGroup.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getAddressGroupWithAddresses(userViewModel.getLoggedInUser().toInt())
            .observe(viewLifecycleOwner){
                val adapter = AddressGroupAdapter(
                    it,
                    getOnAddressGroupButtonClickListener()
                )
                binding.rvAddressGroup.adapter = adapter
            }
    }

    private fun getOnAddressGroupButtonClickListener()=
        object : AddressGroupAdapter.OnClickAddressGroupButton {
            override fun onClickEdit(addressGroup: AddressGroupWithAddress) {

            }

            override fun onClickRemove(addressGroup: AddressGroupWithAddress) {

            }

        }

}