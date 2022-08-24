package com.sample.chrono12.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.data.entities.ProductImage
import com.sample.chrono12.data.entities.relations.ProductWithBrand
import com.sample.chrono12.databinding.FragmentProductBinding
import com.sample.chrono12.ui.adapter.ImageSliderAdapter
import com.sample.chrono12.ui.adapter.ProductDetailsAdapter
import com.sample.chrono12.ui.adapter.ThumbnailAdapter
import com.sample.chrono12.viewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private val navArgs by navArgs<ProductFragmentArgs>()
    private lateinit var binding: FragmentProductBinding
    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        productViewModel.setProduct(navArgs.productId)
        Log.d("dataaa", "Check")
        productViewModel.getProduct().observe(viewLifecycleOwner) {
            setProductInfo(it.productWithBrand)
            setupImageAdapters(it.images)
            Log.d("dataaa", "Check1")
        }
        productViewModel.getProductDetail().observe(viewLifecycleOwner) { productDetailList ->
            productDetailList?.let { setupProductDetailAdapter(productDetailList) }
        }

    }

    private fun setProductInfo(productWithBrand: ProductWithBrand) {
        binding.tvProductName.text = productWithBrand.product.name
        binding.tvBrandName.text = productWithBrand.brand.brandName
        binding.tvCurrentPrice.text = "₹" + productWithBrand.product.currentPrice.toInt().toString()
        binding.rbRating.rating = productWithBrand.product.totalRating!!
        if(productWithBrand.product.originalPrice==productWithBrand.product.currentPrice){
            binding.tvOriginalPrice.visibility = View.GONE
            binding.tvOffPercent.visibility = View.GONE
        } else{
            binding.tvOriginalPrice.apply {
                text = "₹" + productWithBrand.product.originalPrice.toInt().toString()
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.tvOffPercent.text =
                "${((productWithBrand.product.originalPrice - productWithBrand.product.currentPrice) / productWithBrand.product.originalPrice * 100).toInt()}% Off"
        }
        if (productWithBrand.product.stockCount <= 10) {
            binding.tvStockAlert.apply {
                if(productWithBrand.product.stockCount==0){
                    text = "Currently out of stock"
                } else{
                    text = "Only ${productWithBrand.product.stockCount} left in stock - Order soon."
                }
                visibility = View.VISIBLE
            }
        }

    }

    private fun setupImageAdapters(productImage: List<ProductImage>) {
        val imageList = mutableListOf<String>()
        productImage.forEach {
            imageList.add(it.imageUrl)
        }
        binding.imageSlider.vpImage.adapter = ImageSliderAdapter(imageList)
        val thumbnailAdapter = ThumbnailAdapter(imageList) { imageUrl ->
            binding.imageSlider.vpImage.currentItem = imageList.indexOf(imageUrl)
        }
        binding.imageSlider.rvThumbnail.adapter = thumbnailAdapter
        binding.imageSlider.rvThumbnail.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.imageSlider.vpImage.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                thumbnailAdapter.updateSelectedPosition(position)
                binding.imageSlider.rvThumbnail.smoothScrollToPosition(position)
            }
        })
    }

    private fun setupProductDetailAdapter(productDetailList: List<ProductDetail>) {
        val adapter = ProductDetailsAdapter(productDetailList)
        productDetailList.forEach {
            Log.d("Detail", "${it.title} -> ${it.content}")
        }
        binding.rvProductDetails.adapter = adapter
        binding.rvProductDetails.layoutManager = LinearLayoutManager(activity)
    }

}