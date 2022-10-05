package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentHomeBinding
import com.sample.chrono12.ui.adapter.BrandsAdapter
import com.sample.chrono12.ui.adapter.CategoriesAdapter
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private val mProductListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setupCategoriesAdapter()
        setupBrandsAdapter()
        setupAllWatchesButton()
        setupTopWatchesAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
    }

    override fun onResume() {
        super.onResume()
        if (mProductListViewModel.isFirstInitialize) {
            lifecycleScope.launch {
                changeVisibility(View.GONE)
                binding.progressBar.visibility = View.VISIBLE
                delay(1000)
                binding.progressBar.visibility = View.GONE
                changeVisibility(View.VISIBLE)
                mProductListViewModel.isFirstInitialize = false
            }
        }
    }

    private fun changeVisibility(visibility: Int) {
        binding.btnAllWatches.visibility = visibility
        binding.rvCategories.visibility = visibility
        binding.tvCategories.visibility = visibility
        binding.rvBrands.visibility = visibility
        binding.tvBrands.visibility = visibility
        binding.rvTopWatches.visibility = visibility
        binding.tvTopWatches.visibility = visibility
    }

    private fun setupAllWatchesButton() {
        binding.btnAllWatches
            .setOnClickListener {
                mProductListViewModel.setProductListTitle("All Watches")
                filterViewModel.clearSelectedFilterIds()
                filterViewModel.clearSelectedFilterPosition()
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                        fromAllWatches = true
                    )
                )
            }
    }

    private fun setupCategoriesAdapter() {
        val categoriesAdapter = CategoriesAdapter(listOf()) { subCategory ->
            mProductListViewModel.setProductListTitle(subCategory.name + "es")
            filterViewModel.clearSelectedFilterPosition()
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.setAppliedFilterIds(subCategory.subCategoryId)
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                subCategoryId = subCategory.subCategoryId
            ))
        }
        binding.rvCategories.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = categoriesAdapter
        }
        mProductListViewModel.getSubCategory().observe(viewLifecycleOwner) {
            it?.let {
                categoriesAdapter.setNewData(it)
            }
        }
    }

    private fun setupBrandsAdapter() {
        val brandAdapter = BrandsAdapter(listOf()) { brand ->
            mProductListViewModel.setProductListTitle(brand.brandName + " Watches")
            filterViewModel.clearSelectedFilterPosition()
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.setAppliedFilterIds(getKey(FilterFragment.brands, brand.brandName))
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                brandId = brand.brandId
            ))
        }
        binding.rvBrands.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = brandAdapter
        }
        mProductListViewModel.getBrand().observe(viewLifecycleOwner) {
            it?.let {
                brandAdapter.setNewData(it)
            }
        }
    }

    private fun setupTopWatchesAdapter() {
        val topWatchAdapter = ProductListAdapter { product ->
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProductFragment(
                product.productId
            ))
        }
        topWatchAdapter.setData(mutableListOf())
        binding.rvTopWatches.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            this.adapter = topWatchAdapter
        }
        mProductListViewModel.topRatedWatchList.observe(viewLifecycleOwner) {
            it?.let { topWatchAdapter.setNewData(it) }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_wishlist_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.searchFragment -> {
                        mProductListViewModel.setSearchText("")
                        findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                        true
                    }
                    R.id.wishlistFragment -> {
                        findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToWishlistFragment())
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target == it.value }.keys.first()
    }
}