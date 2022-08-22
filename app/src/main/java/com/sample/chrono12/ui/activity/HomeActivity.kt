package com.sample.chrono12.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Index
import com.sample.chrono12.R
import com.sample.chrono12.databinding.ActivityMainBinding
import com.sample.chrono12.viewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var orderViewModel: OrderViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerHome) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNav.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.cartFragment, R.id.profileFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        orderViewModel.setProduct(1)

        orderViewModel.getProduct().observe(this){
            Log.d("Data", it.toString())
        }

        orderViewModel.setCategoryWithSubCategory()

        orderViewModel.getCategoryWithSubCategory().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data", "\n")
            }
        }

        orderViewModel.setProductWithImages()

        orderViewModel.getProductWithImages().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data", "\n")
            }
        }

        orderViewModel.setProductWithSubCategory()

        orderViewModel.getProductWithSubCategory().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data", "\n")
            }
        }

        orderViewModel.setSubCategoryWithProduct()

        orderViewModel.getSubCategoryWithProduct().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data", "\n")
            }
        }

        orderViewModel.setCartWithProductAndImages()

        orderViewModel.getCartWithProductAndImages().observe(this){
            it.forEach {
                Log.d("Data", it.toString()+"\n")
            }
        }

        orderViewModel.setBrandWithProductAndImages()

        orderViewModel.getBrandWithProductAndImages().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data", "\n")
                Log.d("Data", "\n")
            }
        }

        orderViewModel.setProductOrderedWithProductAndImages()

        orderViewModel.getProductOrderedWithProductAndImages().observe(this){
            it.forEach {
                Log.d("Data", it.toString())
                Log.d("Data","\n")
                Log.d("Data","\n")
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

}
