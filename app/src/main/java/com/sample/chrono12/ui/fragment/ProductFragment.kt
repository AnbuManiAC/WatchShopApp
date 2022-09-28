package com.sample.chrono12.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.Cart
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
        setHasOptionsMenu(true)
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
            checkIsProductInUserCart(it.productWithBrand.product.productId)
        }
        productViewModel.getIsProductInUserWishList().observe(viewLifecycleOwner) { isChecked ->
            binding.favBtn.isChecked = isChecked
        }
        productViewModel.getIsProductInUserCart().observe(viewLifecycleOwner) { isInCart ->
            setUpAddToCartButtonListener(isInCart)
            setUpAddToCartButton(isInCart)
        }
        productViewModel.getProductDetail().observe(viewLifecycleOwner) { productDetailList ->
            productDetailList?.let { setupProductDetailAdapter(productDetailList) }
        }

    }

    private fun checkIsProductInUserWishList(productId: Int) {
        lifecycleScope.launch {
            val isProductInUserWishList = wishListViewModel.isProductInUserWishList(
                productId,
                userViewModel.getLoggedInUser().toInt()
            )
            productViewModel.setIsProductInUserWishList(isProductInUserWishList).also {
                setupWishListToggleListener()
            }
        }
    }

    private fun checkIsProductInUserCart(productId: Int) {
        lifecycleScope.launch {
            val isProductInUserCart = cartViewModel.isProductInUserCart(
                productId,
                userViewModel.getLoggedInUser().toInt()
            )
            productViewModel.setIsProductInUserCart(isProductInUserCart)
        }
    }

    private fun setupWishListToggleListener() {
        binding.favBtn.setOnCheckedChangeListener { button, isChecked ->
            if (userViewModel.getIsUserLoggedIn()) {
                editUserWishList(isChecked)
            } else {
                button.isChecked = false
                Toast.makeText(requireContext(), "Login to add to wishlist", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setUpAddToCartButtonListener(isInUserCart: Boolean) {
        binding.btnAddToCart.setOnClickListener {
            if (userViewModel.getIsUserLoggedIn()) {
                if (isInUserCart) {
                    if (findNavController().currentDestination?.id == R.id.productFragment)
                        findNavController().navigate(ProductFragmentDirections.actionProductFragmentToCartFragment())
                } else {
                    val userId = userViewModel.getLoggedInUser().toInt()
                    val productId =
                        productViewModel.getProduct().value!!.productWithBrand.product.productId
                    val cart = Cart(userId = userId, productId = productId)
                    cartViewModel.addProductToUserCart(cart)
                    productViewModel.setIsProductInUserCart(!isInUserCart)
                }
            } else {
                Toast.makeText(requireContext(), "Login to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpAddToCartButton(isInUserCart: Boolean) {
        if (isInUserCart) {
            with(binding.btnAddToCart) {
                backgroundTintList = resources.getColorStateList(R.color.goldenYellow, null)
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_goto_cart, null)
                text = getString(R.string.go_to_cart)
            }
        } else {
            with(binding.btnAddToCart) {
                backgroundTintList = resources.getColorStateList(R.color.primaryColor, null)
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_add_to_cart, null)
                text = getString(R.string.add_to_cart)
            }
        }
    }

    private fun editUserWishList(isChecked: Boolean) {
        val productId = productViewModel.getProduct().value!!.productWithBrand.product.productId
        val userId = userViewModel.getLoggedInUser().toInt()
        val wishList = WishList(productId = productId, userId = userId)
        if (isChecked) {
            wishListViewModel.addProductToUserWishList(wishList)
            Toast.makeText(requireContext(), "Added to Wishlist Successfully", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                requireContext(),
                "Removed from Wishlist Successfully",
                Toast.LENGTH_SHORT
            ).show()
            wishListViewModel.removeProductFromUserWishList(productId, userId)
        }
        productViewModel.setIsProductInUserWishList(isChecked)
    }


    private fun setProductInfo(productWithBrand: ProductWithBrand) {
        binding.tvProductName.text = productWithBrand.product.name
        binding.tvBrandName.text = productWithBrand.brand.brandName
        binding.tvCurrentPrice.text = "₹" + productWithBrand.product.currentPrice.toInt().toString()
        binding.rbRating.rating = productWithBrand.product.totalRating!!
        if (productWithBrand.product.originalPrice == productWithBrand.product.currentPrice) {
            binding.tvOriginalPrice.visibility = View.GONE
            binding.tvOffPercent.visibility = View.GONE
        } else {
            binding.tvOriginalPrice.apply {
                text = "₹" + productWithBrand.product.originalPrice.toInt().toString()
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.tvOffPercent.text =
                "${((productWithBrand.product.originalPrice - productWithBrand.product.currentPrice) / productWithBrand.product.originalPrice * 100).toInt()}% Off"
        }
        if (productWithBrand.product.stockCount <= 10) {
            binding.tvStockAlert.apply {
                if (productWithBrand.product.stockCount == 0) {
                    text = "Currently out of stock"
                    binding.btnAddToCart.visibility = View.GONE
                } else {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_cart_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchFragment -> {
                if (findNavController().currentDestination?.id == R.id.productFragment)
                    findNavController().navigate(ProductFragmentDirections.actionProductFragmentToSearchFragment())
                true
            }
            R.id.wishlistFragment -> {
                if (findNavController().currentDestination?.id == R.id.productFragment)
                    findNavController().navigate(ProductFragmentDirections.actionProductFragmentToWishlistFragment())
                true
            }
            R.id.cartFragment -> {
                if (findNavController().currentDestination?.id == R.id.productFragment)
                    findNavController().navigate(ProductFragmentDirections.actionProductFragmentToCartFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

}