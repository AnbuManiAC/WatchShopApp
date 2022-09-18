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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.databinding.FragmentSearchBinding
import com.sample.chrono12.ui.adapter.SuggestionAdapter
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_COMPLETED
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_INITIATED
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_NOT_INITIATED
import com.sample.chrono12.viewmodels.UserViewModel
import java.util.logging.Filter


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }
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

        productListViewModel.searchStatus.observe(viewLifecycleOwner) { status ->
            Log.d("PSearch", "In observe - $status")
            when (status) {
                SEARCH_COMPLETED -> {
                    val list = productListViewModel.getSearchResult().value
                    if (list.isNullOrEmpty()) {
                        binding.ivSearch.visibility = View.VISIBLE
                        binding.tvSearchInfo.visibility = View.VISIBLE
                        productListViewModel.setSearchStatus()
                    } else if (list.isNotEmpty()) {
                        binding.ivSearch.visibility = View.GONE
                        binding.tvSearchInfo.visibility = View.GONE
                        Log.d("PSearch", "In else")
                        productListViewModel.setSearchStatus()
//                                productListViewModel.searchStatus.removeObservers(viewLifecycleOwner)
                        findNavController()
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

        setupSuggestionAdapter()
        initSuggestions()
    }

    private fun setupSuggestionAdapter() {

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


    private fun initSuggestions() {
        userViewModel.setSuggestions()
    }

    private val getSearchQueryListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SEARCH1", "$query")
                binding.rvSearchSuggestions.visibility = View.GONE
                searchView.clearFocus()
                hideInput()
                if (query == null) return false
                productListViewModel.setProductsWithBrandAndImagesByQuery(query).also {
                    userViewModel.insertSuggestion(query, 0)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                initSuggestions()
                binding.ivSearch.visibility = View.GONE
                binding.tvSearchInfo.visibility = View.GONE
                binding.rvSearchSuggestions.visibility = View.VISIBLE
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.searchFragment)
        searchView = searchItem.actionView as SearchView
        searchItem.expandActionView()
        searchView.setOnQueryTextListener(getSearchQueryListener)

        searchItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    hideInput()
                    requireActivity().onBackPressed()
                    return true
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        filterViewModel.clearSelectedFilterIds()
    }

}