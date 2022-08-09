package com.sample.chrono12.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sample.chrono12.R
import com.sample.chrono12.viewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        productViewModel.setProduct()

        productViewModel.product.observe(this) {
            Toast.makeText(this, "Welcome, see the boom", Toast.LENGTH_LONG).show()
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