package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R

class CartFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToSearchFragment())
                true
            }
            R.id.favoriteFragment -> {
                findNavController().navigate(CartFragmentDirections.actionCartFragmentToFavoriteFragment()  )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}