package com.sample.chrono12.ui.fragment

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
import com.sample.chrono12.data.models.SortType
import com.sample.chrono12.data.models.SortType.*
import com.sample.chrono12.databinding.FragmentProductListBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private lateinit var binding: FragmentProductListBinding
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        syncTitle()
        binding = FragmentProductListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupProductListAdapter()
        setupSortButtonListener()
        setupFilterButtonListener()
    }

    private fun setupFilterButtonListener() {
        filterViewModel.filterCount.observe(viewLifecycleOwner) { filterCount ->
            filterCount?.let {
                if (filterCount > 0) {
                    binding.btnFilter.text =
                        resources.getString(R.string.filter_button_text, filterCount)
                } else {
                    binding.btnFilter.text = resources.getString(R.string.filter)

                }
            }
            if(filterViewModel.isFilterChanged){
                productListViewModel.setProductListTitle("Watches")
                syncTitle()
                filterViewModel.isFilterChanged = false
            }
        }
        binding.btnFilter.setOnClickListener {
            filterViewModel.setSelectedFilterIds(filterViewModel.appliedFilterIds)
            findNavController().safeNavigate(ProductListFragmentDirections.actionProductListFragmentToFilterFragment())
        }
    }

    private fun setupSortButtonListener() {
        productListViewModel.sortType.observe(viewLifecycleOwner) { sortType ->
            if (sortType == PRICE_HIGH_TO_LOW) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Price ⬇")
            }
            if (sortType == RATING_HIGH_TO_LOW) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Rating ⬇")
            }
            if (sortType == PRICE_LOW_TO_HIGH) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Price ⬆")
            }
            if (sortType == RATING_LOW_TO_HIGH) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Rating ⬆")
            }
        }
        binding.btnSort.setOnClickListener {
            findNavController().safeNavigate(ProductListFragmentDirections.actionProductListFragmentToSortDialog())
            (requireActivity() as HomeActivity).setActionBarTitle(productListViewModel.getProductListTitle())
        }
    }

    private fun setProductCountTv(count: Int) {
        binding.tvProductDetail.text = resources.getString(R.string.product_count, count)
    }

    private fun setupProductListAdapter() {

        val adapter = ProductListAdapter { product ->
            findNavController().safeNavigate(
                ProductListFragmentDirections.actionProductListFragmentToProductFragment(product.productId)
            )
        }
        adapter.setData(mutableListOf())
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvProductList.apply {
            layoutManager = linearLayoutManager
            this.adapter = adapter
        }
        productListViewModel.watchList
            .observe(viewLifecycleOwner) { productList ->
                productList?.let {
                    setProductCountTv(it.size)
                    adapter.setNewData(it)
                    linearLayoutManager.scrollToPosition(0)
                }
                if (productList.isEmpty()) {
                    binding.tvProductDetail.visibility = View.GONE
                    binding.ivNoProductFound.visibility = View.VISIBLE
                } else {
                    binding.tvProductDetail.visibility = View.VISIBLE
                    binding.ivNoProductFound.visibility = View.GONE
                }
            }
    }

    private fun syncTitle() {
        (requireActivity() as HomeActivity).setActionBarTitle(productListViewModel.getProductListTitle())
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_wishlist_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.wishlistFragment -> {
                        findNavController().safeNavigate(ProductListFragmentDirections.actionProductListFragmentToWishlistFragment())
                        true
                    }
                    R.id.searchFragment -> {
                        findNavController().safeNavigate(ProductListFragmentDirections.actionProductListFragmentToSearchFragment())
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}