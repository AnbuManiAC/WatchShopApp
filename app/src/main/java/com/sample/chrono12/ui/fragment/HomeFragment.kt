package com.sample.chrono12.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.chrono12.R
import com.sample.chrono12.ui.adapter.CategoriesAdapter
import com.sample.chrono12.viewmodels.ProductCategoryViewModel
import com.sample.chrono12.viewmodels.ProductViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mProductCategoryViewModel: ProductCategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView = view.findViewById(R.id.rvCategories)
        mProductCategoryViewModel = ViewModelProvider(requireActivity())[ProductCategoryViewModel::class.java]

        mProductCategoryViewModel.getSubCategory().observe(viewLifecycleOwner){
            val categoryAdapter = CategoriesAdapter(it)
            mRecyclerView.adapter = categoryAdapter
        }
        mRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
//        mRecyclerView.layoutManager = GridLayoutManager(activity,3)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_fav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.searchFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProductFragment())
                true
            }
            R.id.favoriteFragment -> {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}