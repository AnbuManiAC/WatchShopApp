package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.databinding.FragmentSearchBinding
import com.sample.chrono12.ui.adapter.SuggestionAdapter
import com.sample.chrono12.viewmodels.ProductListViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_COMPLETED
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_INITIATED
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_NOT_INITIATED
import com.sample.chrono12.viewmodels.UserViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var hasFirstFocus: Boolean = false
    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initSuggestions() {
        if (hasFirstFocus) {
            userViewModel.setSuggestions()
            hasFirstFocus = false
        }
        binding.ivSearch.visibility = View.GONE
        binding.rvSearchSuggestions.visibility = View.VISIBLE
    }

    private fun getSearchQueryListener(view: View) =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SEARCH","$query")
                binding.rvSearchSuggestions.visibility = View.GONE
                hideInput()
                searchView.clearFocus()
                hasFirstFocus = true
                if (query == null) return false
                productListViewModel.setProductsWithBrandAndImagesByQuery(query).also {
                    userViewModel.insertSuggestion(query, 0)
                }
                productListViewModel.getSearchStatus().observe(viewLifecycleOwner) { status ->
                    when (status) {
                        SEARCH_COMPLETED -> {
                            val list = productListViewModel.getSearchResult().value
                            if (list.isNullOrEmpty()) {
                                binding.ivSearch.setImageResource(R.drawable.ic_no_result_found)
                                binding.tvSearchInfo.text = "No result found!"
                            } else if (list.isNotEmpty()) {
                                Navigation.findNavController(requireView())
                                    .navigate(SearchFragmentDirections.actionSearchFragmentToProductListFragment())
                            }
                        }
                        SEARCH_INITIATED -> {
                            Log.i("PSearchFragment", "status - SEARCH_INITIALISED")
                        }
                        SEARCH_NOT_INITIATED -> {
                            Log.i("PSearchFragment", "status - SEARCH_NOT_INITIALISED")
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                binding.rvSearchSuggestions.visibility = View.VISIBLE
                if (newText.isNullOrEmpty()) userViewModel.setSuggestions()
                return true
            }

        }

    private fun hideInput() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            searchView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun getOnClickSuggestionListener() =
        object : SuggestionAdapter.OnClickSuggestion {
            override fun onClick(suggestion: SearchSuggestion) {
                searchView.setQuery(suggestion.suggestion, true)
            }

            override fun onClickRemove(suggestionHistory: SearchSuggestion, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle(suggestionHistory.suggestion)
                    .setMessage("Remove from Suggestion History")
                    .setPositiveButton("Remove") { _, _ ->
                        userViewModel.removeSuggestion(suggestionHistory)
                        (binding.rvSearchSuggestions.adapter as? SuggestionAdapter)?.removeSuggestion(
                            position
                        )
                    }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .create()
                    .show()
            }

        }

    override fun onResume() {
        super.onResume()
        binding.rvSearchSuggestions.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_wishlist_cart_menu, menu)
        menu.removeItem(R.id.wishlistFragment)
        menu.removeItem(R.id.cartFragment)

        val searchItem = menu.findItem(R.id.searchFragment)
        searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(getSearchQueryListener(requireView()))

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                initSuggestions()
                binding.ivSearch.setImageResource(R.drawable.ic_search_svg)

            } else {
                binding.ivSearch.visibility = View.VISIBLE
                binding.rvSearchSuggestions.visibility = View.GONE
            }
        }

        binding.rvSearchSuggestions.layoutManager = LinearLayoutManager(requireContext())
        userViewModel.getSuggestions().observe(viewLifecycleOwner) { suggestions ->
            val distinctSuggestions = suggestions.distinctBy {
                it.suggestion
            }
            if (suggestions.isEmpty()) return@observe
            val mutableSuggestions = ArrayList<SearchSuggestion>(distinctSuggestions)
            binding.rvSearchSuggestions.adapter = SuggestionAdapter(
                mutableSuggestions.toMutableList(),
                getOnClickSuggestionListener()
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchFragment -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}