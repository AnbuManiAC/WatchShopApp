package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.data.entities.relations.ProductWithBrandAndImages
import com.sample.chrono12.data.entities.relations.SubCategoryWithProduct
import com.sample.chrono12.databinding.FragmentProductListBinding
import com.sample.chrono12.ui.activity.HomeActivity
import com.sample.chrono12.ui.adapter.ProductListAdapter
import com.sample.chrono12.viewmodels.CategoryProductListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private val navArgs by navArgs<ProductListFragmentArgs>()
    private lateinit var binding: FragmentProductListBinding
    private val categoryProductListViewModel by lazy { ViewModelProvider(requireActivity())[CategoryProductListViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        if(navArgs.subCategoryId>0){
            categoryProductListViewModel.setProductWithBrandAndImagesList(navArgs.subCategoryId)
        }
        if(navArgs.brandId>0){
            categoryProductListViewModel.setBrandWithProductList(navArgs.brandId)
        }
        binding = FragmentProductListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as HomeActivity).setActionBarTitle(categoryProductListViewModel.getProductListTitle())

        categoryProductListViewModel.getProductWithBrandAndImagesList().observe(viewLifecycleOwner){ productInfo ->
            productInfo?.let { setupProductListAdapter(productInfo.productWithBrandAndImagesList) }
        }
        categoryProductListViewModel.getBrandWithProductList().observe(viewLifecycleOwner) { productInfo ->
            productInfo?.let { setupProductListAdapter(productInfo) }
        }

    }

    private fun setupProductListAdapter(productWithBrandAndImagesList: List<ProductWithBrandAndImages>){
        val adapter = ProductListAdapter(productWithBrandAndImagesList){ product ->
            Navigation.findNavController(requireView()).navigate(ProductListFragmentDirections.actionProductListFragmentToProductFragment(product.productId))
        }
        binding.rvProductList.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}