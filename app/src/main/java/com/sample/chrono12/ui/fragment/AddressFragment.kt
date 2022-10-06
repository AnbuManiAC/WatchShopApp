package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Address
import com.sample.chrono12.data.entities.relations.AddressGroupWithAddress
import com.sample.chrono12.databinding.FragmentAddressBinding
import com.sample.chrono12.ui.adapter.AddressAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.UserViewModel

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<AddressFragmentArgs>()
    private lateinit var addressAdapter: AddressAdapter
    private var showMenu = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        if (navArgs.addFromExisting) {
            binding.fabAddAddress.visibility = View.GONE
        } else {
            binding.fabAddAddress.visibility = View.VISIBLE
            binding.fabAddAddress.setOnClickListener {
                findNavController().safeNavigate(
                    AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(
                        addressGroupName = getString(R.string.default_group_name).lowercase()
                    )
                )
            }
        }
        if (navArgs.chooseAddress) setupChooseAddressButton()
        setupAddressAdapter()
    }

    private fun setupChooseAddressButton() {
        binding.btnSelectAddress.visibility = View.VISIBLE
        binding.btnSelectAddress.setOnClickListener {
            if (addressAdapter.getSelectedAddressId().second > 0) {
                val groupId = addressAdapter.getSelectedAddressId().first
                val addressId = addressAdapter.getSelectedAddressId().second
                findNavController().safeNavigate(
                    AddressFragmentDirections.actionAddressFragmentToOrderConfirmationFragment(
                        groupId,
                        addressId
                    )
                )
            }
        }
    }

    private fun setupAddressAdapter() {

        addressAdapter = AddressAdapter(
            getOnAddressButtonClickListener(),
            navArgs.addFromExisting,
            navArgs.chooseAddress
        )
        addressAdapter.setAddresses(listOf())
        binding.rvAddress.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddress.adapter = addressAdapter

        if (navArgs.addFromExisting) {
            val addressIds: List<Int> = userViewModel.getAddressIds() ?: emptyList()
            userViewModel.getUserAddresses(
                userViewModel.getLoggedInUser().toInt()
            ).observe(viewLifecycleOwner) {
                it?.let {
                    val addresses = mutableListOf<Address>()
                    it.addressList.forEach { address ->
                        if (address.addressId !in addressIds) {
                            addresses.add(address)
                        }
                    }
                    val addressGroupWithAddress = AddressGroupWithAddress(
                        addressGroup = it.addressGroup,
                        addressList = addresses.toList()
                    )
                    addressAdapter.setAddressGroup(addressGroupWithAddress.addressGroup)
                    addressAdapter.setNewData(addressGroupWithAddress.addressList)
                    if (addressGroupWithAddress.addressList.isEmpty()) {
                        if (it.addressList.isEmpty()) {
                            binding.tvEmptyAddressDesc.text =
                                resources.getString(R.string.no_address_found)
                        } else {
                            binding.tvEmptyAddressDesc.text =
                                resources.getString(R.string.all_address_added)
                        }
                        binding.clNoDataFound.visibility = View.VISIBLE
                        showMenu = false
                        (requireActivity() as MenuHost).invalidateMenu()
                    } else {
                        binding.clNoDataFound.visibility = View.GONE
                    }
                }
                if (it == null || it.addressList.isEmpty()) {
                    binding.clNoDataFound.visibility = View.VISIBLE
                }
            }
        } else {
            userViewModel.getUserAddresses(userViewModel.getLoggedInUser().toInt())
                .observe(viewLifecycleOwner) {
                    it?.let { addressWithAddressGroup ->
                        binding.rvAddress.visibility = View.VISIBLE
                        addressAdapter.setAddressGroup(addressWithAddressGroup.addressGroup)
                        addressAdapter.setNewData(addressWithAddressGroup.addressList)
                    }
                    if (it == null || it.addressList.isEmpty()) {
                        binding.clNoDataFound.visibility = View.VISIBLE
                        binding.btnSelectAddress.visibility = View.GONE
                    } else {
                        binding.clNoDataFound.visibility = View.GONE
                    }
                }
        }

    }

    private fun getOnAddressButtonClickListener() =
        object : AddressAdapter.OnClickAddressButton {
            override fun onClickRemove(
                addressId: Int,
                addressGroupId: Int,
                addressGroupName: String
            ) {
                findNavController().safeNavigate(
                    AddressFragmentDirections.actionAddressFragmentToDeleteAddressDialog(
                        addressId
                    )
                )
            }

            override fun onClickEdit(addressId: Int) {
                findNavController().safeNavigate(
                    AddressFragmentDirections.actionAddressFragmentToNewAddressFragment(
                        addressId
                    )
                )
            }

        }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (navArgs.addFromExisting && showMenu) menuInflater.inflate(
                    R.menu.done_menu,
                    menu
                )
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.done) {
                    addressAdapter.getSelectedIds().forEach {
                        userViewModel.insertIntoAddressAndGroupCrossRef(
                            it,
                            navArgs.addressGroupName
                        )
                    }
                    findNavController().navigateUp()
                    return true
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
