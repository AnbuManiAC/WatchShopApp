package com.sample.chrono12.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.databinding.ActivityMainBinding
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.viewmodels.CartViewModel
import com.sample.chrono12.viewmodels.ConnectivityViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import com.sample.chrono12.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var badge : BadgeDrawable
    private val userViewModel by lazy { ViewModelProvider(this)[UserViewModel::class.java] }
    private val productListViewModel by lazy { ViewModelProvider(this)[ProductListViewModel::class.java] }
    private val cartViewModel by lazy { ViewModelProvider(this)[CartViewModel::class.java] }
    private val connectivityViewModel by lazy { ViewModelProvider(this)[ConnectivityViewModel::class.java] }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupSharedPref()
        setupUser()
        bottomNav = binding.bottomNav
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(onNavDestinationChangedListener())
        bottomNav.setupWithNavController(navController)

        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.cartFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        productListViewModel.setSubCategory()
        productListViewModel.setBrand()
        productListViewModel.setTopRatedWatches(10)
        enableCartBadge()
    }

    override fun onResume() {
        super.onResume()
        setupNetworkConnectionMonitor()
    }

    private fun setupNetworkConnectionMonitor() {
        connectivityViewModel.networkState.observe(this){ hasInternet ->
            if (!hasInternet) {
                val snackBar = Snackbar.make(
                    findViewById(R.id.snackBarLayout),
                    R.string.no_internet, Snackbar.LENGTH_INDEFINITE
                )
                snackBar.setAction(getString(R.string.okay)){
                    snackBar.dismiss()
                }
                snackBar.show()
                connectivityViewModel.setNetWorkState(true)
            }
        }
    }


    private fun setupUser() {
        val userId = SharedPrefUtil.getUserId(this)
        if(userId != 0L) userViewModel.setLoggedInUser(userId)
    }

    private fun onNavDestinationChangedListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomBar()
                R.id.cartFragment -> showBottomBar()
                R.id.profileFragment -> showBottomBar()
                R.id.logoutDialog -> showBottomBar()
                R.id.deleteSearchDialog -> showBottomBar()
                R.id.chooseAddressTypeFragment -> showBottomBar()
                else -> hideBottomBar()
            }
        }

    private fun showBottomBar() {
        bottomNav.visibility = View.VISIBLE
    }

    private fun hideBottomBar() {
        bottomNav.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setBackButtonAs(@DrawableRes drawableIcon: Int) {
        supportActionBar?.setHomeAsUpIndicator(drawableIcon)
    }

    fun enableCartBadge() {
        badge = bottomNav.getOrCreateBadge(R.id.cartFragment)
        badge.backgroundColor = ResourcesCompat.getColor(resources, R.color.badgeColor, theme)
        badge.badgeTextColor = getColor(R.color.white)
        if(userViewModel.getIsUserLoggedIn()){
            cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt()).observe(this){
                val cartItemCount = it.size
                if(cartItemCount>0){
                    badge.isVisible = true
                    badge.number = cartItemCount
                }else{
                    badge.isVisible = false
                }
            }
        } else{
            badge.isVisible = false
        }
    }

    fun disableCartBadge(){
        badge.isVisible = false
        cartViewModel.getCartItems(userViewModel.getLoggedInUser().toInt()).removeObservers(this)
    }

    private fun setupSharedPref() {
        val sharedPreference =
            this.getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
        if (sharedPreference.getLong(getString(R.string.user_id), -1) == -1L) {
            val sharedPrefEditor = sharedPreference?.edit()
            sharedPrefEditor?.let { editor ->
                editor.putLong(getString(R.string.user_id), 0)
                editor.putInt(getString(R.string.bulk_order_id), 0)
                editor.putInt(getString(R.string.notification_id), 0)
                editor.putString(getString(R.string.sort_type), "")
                editor.apply()
            }
        }
    }

}
