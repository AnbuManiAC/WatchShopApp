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
import com.sample.chrono12.data.models.SortType
import com.sample.chrono12.data.models.SortType.*
import com.sample.chrono12.databinding.FragmentProductListBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val navArgs by navArgs<ProductListFragmentArgs>()
    private lateinit var binding: FragmentProductListBinding
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (navArgs.subCategoryId > 0) {
            productListViewModel.setSubcategoryWithProductList(navArgs.subCategoryId, getSortType())
        } else if (navArgs.brandId > 0) {
            productListViewModel.setBrandWithProductList(navArgs.brandId, getSortType())
        } else if (navArgs.fromAllWatches) {
            productListViewModel.setAllWatches(getSortType())
        }
    }

    private fun getSortType(): SortType {
        return when (SharedPrefUtil.getSortType(requireActivity())) {
            PRICE_LOW_TO_HIGH.toString() -> PRICE_LOW_TO_HIGH
            PRICE_HIGH_TO_LOW.toString() -> PRICE_HIGH_TO_LOW
            RATING_HIGH_TO_LOW.toString() -> RATING_HIGH_TO_LOW
            RATING_LOW_TO_HIGH.toString() -> RATING_LOW_TO_HIGH
            else -> RATING_HIGH_TO_LOW
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)

        binding = FragmentProductListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setActionBarTitle(productListViewModel.getProductListTitle())

        productListViewModel.watchList
            .observe(viewLifecycleOwner) { productList ->
                productList?.let {
                    setProductCountTv(it.size)
                    setupProductListAdapter(productList)
                }
            }

        setupSortButtonListener()
        setupFilterButtonListener()

    }

    private fun setupFilterButtonListener() {
        filterViewModel.filterCount.observe(viewLifecycleOwner) { filterCount ->
            filterCount?.let {
                if(filterCount>=1){
                    binding.btnFilter.text = resources.getString(R.string.filter_button_text, filterCount)
                }else{
                    binding.btnFilter.text = resources.getString(R.string.filter)

                }
            }
        }
        binding.btnFilter.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(ProductListFragmentDirections.actionProductListFragmentToFilterFragment())
        }
    }

    private fun setupSortButtonListener() {
        productListViewModel.sortType.observe(viewLifecycleOwner) { sortType ->
            if (sortType == PRICE_HIGH_TO_LOW || sortType == PRICE_LOW_TO_HIGH) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Price")
            }
            if (sortType == RATING_HIGH_TO_LOW || sortType == RATING_LOW_TO_HIGH) {
                binding.btnSort.text = resources.getString(R.string.sort_button_text, "Rating")
            }
        }
        binding.btnSort.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(ProductListFragmentDirections.actionProductListFragmentToSortDialog())
        }
    }

    private fun setProductCountTv(count: Int) {
        binding.tvProductDetail.text = resources.getString(R.string.product_count, count)
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
        inflater.inflate(R.menu.search_wishlist_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
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

    override fun onDestroy() {
        super.onDestroy()
    }

}