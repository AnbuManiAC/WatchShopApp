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
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val mProductListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        mProductListViewModel.setTopRatedWacthes(10)
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
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment())
        })

//        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                true
            }
            R.id.favoriteFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWishlistFragment())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupCategoriesAdapter(subCategories: List<SubCategory>){
        val categoryAdapter = CategoriesAdapter(subCategories){ subCategory->
            mProductListViewModel.setProductListTitle(subCategory.name+"es")
            Navigation.findNavController(requireView()).navigate(HomeFragmentDirections.actionHomeFragmentToProductListFragment(subCategoryId = subCategory.subCategoryId))
        }
        binding.rvCategories.apply {
            this.adapter = categoryAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupBrandsAdapter(brands: List<ProductBrand>) {
        val brandAdapter = BrandsAdapter(brands){ brand ->
            mProductListViewModel.setProductListTitle(brand.brandName+" Watches")
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

}