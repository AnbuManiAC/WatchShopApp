package com.sample.chrono12.ui.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
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
    ): View {
        binding = FragmentProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
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
                showLogInSnackBar()
            }
        }
    }

    private fun showLogInSnackBar() {
        val snackBar = Snackbar.make(binding.snackBarLayout, getString(R.string.you_are_not_logged_in), Snackbar.LENGTH_LONG)
        snackBar.setAction(getString(R.string.log_in)){
            if(findNavController().currentDestination?.id == R.id.productFragment){
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentToLogInFragment())
            }
        }
        snackBar.show()
    }

    private fun showWishListSnackBar(message: String) {
        val snackBar = Snackbar.make(binding.snackBarLayout, message, Snackbar.LENGTH_LONG)
        snackBar.show()
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
                showLogInSnackBar()
            }
        }
    }

    private fun setUpAddToCartButton(isInUserCart: Boolean) {
        if (isInUserCart) {
            with(binding.btnAddToCart) {
                setBackgroundColor(ResourcesCompat.getColor(resources, R.color.buttonColorSelected, requireActivity().theme))
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_goto_cart, requireActivity().theme)
                text = getString(R.string.go_to_cart)
            }
        } else {
            with(binding.btnAddToCart) {
                setBackgroundColor(ResourcesCompat.getColor(resources, R.color.buttonColor, requireActivity().theme))
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_add_to_cart, requireActivity().theme)
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
            showWishListSnackBar(getString(R.string.product_is_added_to_wishlist))
        } else {
            showWishListSnackBar(getString(R.string.product_is_removed_from_wishlist))
            wishListViewModel.removeProductFromUserWishList(productId, userId)
        }
        productViewModel.setIsProductInUserWishList(isChecked)
    }


    private fun setProductInfo(productWithBrand: ProductWithBrand) {
        binding.tvProductName.text = productWithBrand.product.name
        binding.tvBrandName.text = productWithBrand.brand.brandName
        binding.tvCurrentPrice.text = getString(R.string.price, productWithBrand.product.currentPrice.toInt())
        binding.rbRating.rating = productWithBrand.product.totalRating
        if (productWithBrand.product.originalPrice == productWithBrand.product.currentPrice) {
            binding.tvOriginalPrice.visibility = View.GONE
            binding.tvOffPercent.visibility = View.GONE
        } else {
            binding.tvOriginalPrice.apply {
                text = getString(R.string.price, productWithBrand.product.originalPrice.toInt())
                paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
            binding.tvOffPercent.text =
                getString(R.string.offer_percentage, ((productWithBrand.product.originalPrice - productWithBrand.product.currentPrice) / productWithBrand.product.originalPrice * 100).toInt())
        }
        if (productWithBrand.product.stockCount <= 10) {
            binding.tvStockAlert.apply {
                if (productWithBrand.product.stockCount == 0) {
                    text = getString(R.string.out_of_stock)
                    binding.btnAddToCart.visibility = View.GONE
                } else {
                    text = getString(R.string.order_soon, productWithBrand.product.stockCount)
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

        binding.rvProductDetails.adapter = adapter
        binding.rvProductDetails.layoutManager = LinearLayoutManager(activity)
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_wishlist_cart_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
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
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}