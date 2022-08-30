package com.sample.chrono12.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.User
import com.sample.chrono12.databinding.ActivityMainBinding
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

        setupUser()
        bottomNav = binding.bottomNav
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(onNavDestinationChangedListener())
        bottomNav.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.cartFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)




    }

    private fun setupUser() {
        val sharedPref = getSharedPreferences(getString(R.string.user_pref), MODE_PRIVATE)
        val userId = sharedPref?.getLong(getString(R.string.user_id), 0)
        userId?.let {
            if(it!=0L) userViewModel.setLoggedInUSer(userId)
        }
    }

    private fun onNavDestinationChangedListener() =
        NavController.OnDestinationChangedListener { _, destination, _ ->
         when(destination.id){
             R.id.homeFragment -> showBottomBar()
             R.id.cartFragment -> showBottomBar()
             R.id.profileFragment -> showBottomBar()
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

    fun setActionBarTitle(title: String){
        supportActionBar?.title = title
    }

    fun setBackButtonAs(@DrawableRes drawableIcon: Int){
        supportActionBar?.setHomeAsUpIndicator(drawableIcon)
    }
}
