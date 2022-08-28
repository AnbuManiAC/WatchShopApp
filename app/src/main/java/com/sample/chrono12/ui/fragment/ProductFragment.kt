package com.sample.chrono12.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.sample.chrono12.data.entities.ProductDetail
import com.sample.chrono12.data.entities.ProductImage
import com.sample.chrono12.data.entities.WishList
import com.sample.chrono12.data.entities.relations.ProductWithBrand
import com.sample.chrono12.databinding.FragmentProductBinding
import com.sample.chrono12.ui.adapter.ImageSliderAdapter
import com.sample.chrono12.ui.adapter.ProductDetailsAdapter
import com.sample.chrono12.ui.adapter.ThumbnailAdapter
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.ProductViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import com.sample.chrono12.viewmodels.WishListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment() {

    private val navArgs by navArgs<ProductFragmentArgs>()
    private lateinit var binding: FragmentProductBinding
    private lateinit var productViewModel: ProductViewModel
    private val cartViewModel by lazy { ViewModelProvider(requireActivity())[CartViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val wishListViewModel by lazy { ViewModelProvider(requireActivity())[WishListViewModel::class.java] }

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
        productViewModel.getProduct().observe(viewLifecycleOwner) {
            setProductInfo(it.productWithBrand)
            setupImageAdapters(it.images)
            checkIsProductInUserWishList(it.productWithBrand.product.productId)
        }
        productViewModel.getIsProductInUserWishList().observe(viewLifecycleOwner){ isChecked ->
            binding.favBtn.isChecked = isChecked

        }
        productViewModel.getProductDetail().observe(viewLifecycleOwner) { productDetailList ->
            productDetailList?.let { setupProductDetailAdapter(productDetailList) }
        }

    }

    private fun checkIsProductInUserWishList(productId: Int) {
        lifecycleScope.launch{
            val isProductInUserWishList = wishListViewModel.isProductInUserWishList(productId, userViewModel.getLoggedInUser().toInt())
            productViewModel.setIsProductInUserWishList(isProductInUserWishList).also {
                setupWishListToggleListener()
            }
        }
    }

    private fun setupWishListToggleListener() {
        binding.favBtn.setOnCheckedChangeListener{ button, isChecked ->
            if(userViewModel.getIsUserLoggedIn()){
                editUserWishList(isChecked)
            }
            else{
                button.isChecked = false
                Navigation.findNavController(requireView()).navigate(ProductFragmentDirections.actionProductFragmentToLogInFragment())
            }
        }
    }

    private fun editUserWishList(isChecked: Boolean) {
        val productId = productViewModel.getProduct().value!!.productWithBrand.product.productId
        val userId = userViewModel.getLoggedInUser().toInt()
        val wishList = WishList(productId = productId, userId = userId)
        if(isChecked){
            wishListViewModel.addProductToUserWishList(wishList)
        }else{
            wishListViewModel.removeProductFromUserWishList(productId , userId)
        }
        productViewModel.setIsProductInUserWishList(isChecked)
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
                    binding.btnAddToCart.visibility = View.GONE
                } else{
                    text = "Only ${productWithBrand.product.stockCount} left in stock - Order soon."
                }
                visibility = View.VISIBLE
            }
        }

        binding.btnAddToCart.setOnClickListener {
            binding.btnAddToCart.text = "View Cart"

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