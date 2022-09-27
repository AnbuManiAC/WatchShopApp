package com.sample.chrono12.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sample.chrono12.R
import com.sample.chrono12.databinding.ActivityMainBinding
import com.sample.chrono12.ui.fragment.FilterFragment
import com.sample.chrono12.ui.fragment.HomeFragment
import com.sample.chrono12.utils.ConnectivityObserver
import com.sample.chrono12.viewmodels.FilterViewModel
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
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

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

    }

    override fun onResume() {
        super.onResume()
        setupNetworkConnectionMonitor()
    }

    private fun setupNetworkConnectionMonitor() {
        val connectionLiveData = ConnectivityObserver(this)
        connectionLiveData.observe(this) { hasInternet ->
            if (hasInternet) {
                Snackbar.make(
                    findViewById(R.id.snackBarLayout),
                    R.string.internet_is_back, Snackbar.LENGTH_SHORT
                ).show()
            }
            else {
                Snackbar.make(
                    findViewById(R.id.snackBarLayout),
                    R.string.no_internet, Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun setupUser() {
        val sharedPref = getSharedPreferences(getString(R.string.user_pref), MODE_PRIVATE)
        val userId = sharedPref?.getLong(getString(R.string.user_id), 0)
        userId?.let {
            if (it != 0L) userViewModel.setLoggedInUser(it)
        }
    }

    private fun onNavDestinationChangedListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> showBottomBar()
                R.id.cartFragment -> showBottomBar()
                R.id.profileFragment -> showBottomBar()
                R.id.logoutDialog -> showBottomBar()
                R.id.deleteSearchHistory -> showBottomBar()
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

    private fun setupSharedPref() {
        val sharedPreference =
            this.getSharedPreferences(getString(R.string.user_pref), Context.MODE_PRIVATE)
        if (sharedPreference.getLong(getString(R.string.user_id), -1) == -1L) {
            val editor = sharedPreference?.edit()
            editor?.let { editor ->
                editor.putLong(getString(R.string.user_id), 0)
                editor.putInt(getString(R.string.bulk_order_id), 0)
                editor.putInt(getString(R.string.notification_id), 0)
                editor.putString(getString(R.string.sort_type), "")
                editor.apply()
            }
        }
    }

}
