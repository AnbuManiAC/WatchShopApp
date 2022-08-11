package com.sample.chrono12.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.business_registration)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart -> Toast.makeText(this, "Clicked Cart", Toast.LENGTH_SHORT).show()
            R.id.fav -> Toast.makeText(this, "Clicked Favorite", Toast.LENGTH_SHORT).show()
            R.id.search -> Toast.makeText(this, "Clicked Search", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}