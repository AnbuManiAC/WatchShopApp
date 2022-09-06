package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.databinding.FragmentProductListBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val navArgs by navArgs<ProductListFragmentArgs>()
    private lateinit var binding: FragmentProductListBinding
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)

        if (navArgs.subCategoryId > 0) {
            productListViewModel.setProductWithBrandAndImagesList(navArgs.subCategoryId)
        } else if (navArgs.brandId > 0) {
            productListViewModel.setBrandWithProductList(navArgs.brandId)
        } else if(navArgs.fromAllWatches){
            productListViewModel.setAllWatches()
        }

        binding = FragmentProductListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setActionBarTitle(productListViewModel.getProductListTitle())


        productListViewModel.getProductWithBrandAndImagesList()
            .observe(viewLifecycleOwner) { productInfo ->
                productInfo?.let { setupProductListAdapter(productInfo.productWithBrandAndImagesList) }
            }
        productListViewModel.getBrandWithProductList().observe(viewLifecycleOwner) { productInfo ->
            productInfo?.let { setupProductListAdapter(productInfo) }
        }

        productListViewModel.getAllWatches().observe(viewLifecycleOwner) { productsList ->
            productsList?.let { setupProductListAdapter(productsList) }
        }

        productListViewModel.getSearchResult().observe(viewLifecycleOwner){ productList ->
            productList?.let { setupProductListAdapter(productList) }
        }


    }

    private fun setupProductListAdapter(productWithBrandAndImagesList: List<ProductWithBrandAndImages>) {
        val adapter = ProductListAdapter(productWithBrandAndImagesList) { product ->
            Navigation.findNavController(requireView()).navigate(
                ProductListFragmentDirections.actionProductListFragmentToProductFragment(product.productId)
            )
        }
        binding.rvProductList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.wishlistFragment -> {
                Navigation.findNavController(requireView())
                    .navigate(ProductListFragmentDirections.actionProductListFragmentToWishlistFragment())
                true
            }
            R.id.searchFragment -> {
                Navigation.findNavController(requireView())
                    .navigate(ProductListFragmentDirections.actionProductListFragmentToSearchFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}