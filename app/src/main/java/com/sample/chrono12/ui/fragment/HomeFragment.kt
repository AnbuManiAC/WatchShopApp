package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentHomeBinding
import com.sample.chrono12.ui.adapter.BrandsAdapter
import com.sample.chrono12.ui.adapter.CategoriesAdapter
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {


    lateinit var binding: FragmentHomeBinding
    private val mProductListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        mProductListViewModel.setSubCategory()
        mProductListViewModel.setBrand()
        mProductListViewModel.setTopRatedWatches(10)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoriesAdapter()
        setupBrandsAdapter()
        setupAllWatchesButton()
        setupTopWatchesAdapter()
    }

    private fun setupAllWatchesButton() {
        binding.tvAllWatches.setOnClickListener {
            mProductListViewModel.setProductListTitle("All Watches")
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.clearSelectedFilterPosition()
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
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
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                        subCategoryId = subCategory.subCategoryId
                    )
                )
        }
        binding.rvCategories.apply {
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = categoriesAdapter
            Log.d("Skewing1", "${System.currentTimeMillis()}")
        }
        mProductListViewModel.getSubCategory().observe(viewLifecycleOwner) {
            it?.let {
                categoriesAdapter.setNewData(it)
                Log.d("Skewing2", "${System.currentTimeMillis()}")
            }
        }
    }

    private fun setupBrandsAdapter() {
        val brandAdapter = BrandsAdapter(listOf()) { brand ->
            mProductListViewModel.setProductListTitle(brand.brandName + " Watches")
            filterViewModel.clearSelectedFilterPosition()
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.setAppliedFilterIds(getKey(this.brands, brand.brandName))
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProductListFragment(
                        brandId = brand.brandId
                    )
                )
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
            if (findNavController().currentDestination?.id == R.id.homeFragment)
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProductFragment(
                        product.productId
                    )
                )
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchFragment -> {
                mProductListViewModel.setSearchText("")
                if (findNavController().currentDestination?.id == R.id.homeFragment)
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                true
            }
            R.id.wishlistFragment -> {
                if (findNavController().currentDestination?.id == R.id.homeFragment)
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWishlistFragment())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val brands = hashMapOf(
        12 to "Fastrack",
        13 to "Titan",
        14 to "Sonata",
        15 to "Timex",
        16 to "Maxima",
        17 to "Helix",
        18 to "Fossil"
    )

    private fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target == it.value }.keys.first()
    }
}