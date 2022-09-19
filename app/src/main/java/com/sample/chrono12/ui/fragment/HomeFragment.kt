package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.ProductBrand
import com.sample.chrono12.data.entities.SubCategory
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
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
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        mProductListViewModel.setTopRatedWatches(10)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mProductListViewModel.getSubCategory().observe(viewLifecycleOwner){
            setupCategoriesAdapter(it)
        }

        mProductListViewModel.getBrand().observe(viewLifecycleOwner){
            setupBrandsAdapter(it)
        }

        mProductListViewModel.getTopRatedWatches().observe(viewLifecycleOwner){
            setupTopWatchesAdapter(it)
        }

        binding.tvAllWatches.setOnClickListener(View.OnClickListener {
            mProductListViewModel.setProductListTitle("All Watches")
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.clearSelectedFilterPosition()
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(fromAllWatches = true))
        })

//        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                true
            }
            R.id.wishlistFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWishlistFragment())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupCategoriesAdapter(subCategories: List<SubCategory>){
        val categoryAdapter = CategoriesAdapter(subCategories){ subCategory->
            mProductListViewModel.setProductListTitle(subCategory.name+"es")
            filterViewModel.clearSelectedFilterPosition()
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.addSelectedFilter(subCategory.subCategoryId)
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(subCategoryId = subCategory.subCategoryId))
        }
        binding.rvCategories.apply {
            this.adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }
    fun <K, V> getKey(hashMap: Map<K, V>, target: V): K {
        return hashMap.filter { target == it.value }.keys.first()
    }

    private fun setupBrandsAdapter(brands: List<ProductBrand>) {
        val brandAdapter = BrandsAdapter(brands){ brand ->
            mProductListViewModel.setProductListTitle(brand.brandName+" Watches")
            filterViewModel.clearSelectedFilterPosition()
            filterViewModel.clearSelectedFilterIds()
            filterViewModel.addSelectedFilter(getKey(this.brands,brand.brandName))
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(brandId = brand.brandId))
        }
        binding.rvBrands.apply {
            this.adapter = brandAdapter

            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupTopWatchesAdapter(topWacthes: List<ProductWithBrandAndImages>) {
        val topWacthesAdapter = ProductListAdapter(topWacthes){ product ->
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductFragment(product.productId))
        }
        binding.rvTopWatches.apply {
            this.adapter = topWacthesAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    val brands = hashMapOf(
        12 to "Fastrack",
        13 to "Titan",
        14 to "Sonata",
        15 to "Timex",
        16 to "Maxima",
        17 to "Helix",
        18 to "Fossil"
    )
}