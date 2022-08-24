package com.sample.chrono12.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sample.chrono12.R
import com.sample.chrono12.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNav = binding.bottomNav
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(onNavDestinationChangedListener())
        bottomNav.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.cartFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)




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
}
