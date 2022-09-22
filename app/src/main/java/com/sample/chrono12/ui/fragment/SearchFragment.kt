package com.sample.chrono12.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.R
import com.sample.chrono12.data.entities.SearchSuggestion
import com.sample.chrono12.data.models.SortType
import com.sample.chrono12.databinding.FragmentSearchBinding
import com.sample.chrono12.ui.adapter.SuggestionAdapter
import com.sample.chrono12.utils.SharedPrefUtil
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel.Companion.SEARCH_COMPLETED
import com.sample.chrono12.viewmodels.UserViewModel
import java.util.*


class SearchFragment : Fragment() {

    private lateinit var adapter: SuggestionAdapter
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
            when (status) {
                SEARCH_COMPLETED -> {
                    val list = productListViewModel.getSearchResult().value
                    if (list.isNullOrEmpty()) {
                        changeSearchImageAndTextVisibility(View.VISIBLE)
                        productListViewModel.setSearchStatus()
                    } else if (list.isNotEmpty()) {
                        changeSearchImageAndTextVisibility(View.GONE)
                        productListViewModel.setSearchStatus()
                        findNavController()
                            .navigate(SearchFragmentDirections.actionSearchFragmentToProductListFragment())
                    }
                }
                else -> {}
            }
        }
        userViewModel.setSearchHistory()
        setupSuggestionAdapter()
    }

    private fun changeSearchImageAndTextVisibility(visibility: Int){
        binding.ivSearch.visibility = visibility
        binding.tvSearchInfo.visibility = visibility
    }

    private fun setupSuggestionAdapter() {
        adapter = SuggestionAdapter(getOnClickSuggestionListener())
        adapter.setData(mutableListOf())
        binding.rvSearchSuggestions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchSuggestions.adapter = adapter
        userViewModel.suggestion.observe(viewLifecycleOwner) { suggestions ->
            if (suggestions.isEmpty()) {
                binding.rvSearchSuggestions.visibility = View.GONE
                return@observe
            } else{
                val mutableSuggestions = ArrayList<SearchSuggestion>(suggestions)
                binding.rvSearchSuggestions.visibility = View.VISIBLE
                adapter.setNewData(mutableSuggestions)
            }

        }

    }



    private fun getSortType(): SortType {
        return when (SharedPrefUtil.getSortType(requireActivity())) {
            SortType.PRICE_LOW_TO_HIGH.toString() -> SortType.PRICE_LOW_TO_HIGH
            SortType.PRICE_HIGH_TO_LOW.toString() -> SortType.PRICE_HIGH_TO_LOW
            SortType.RATING_HIGH_TO_LOW.toString() -> SortType.RATING_HIGH_TO_LOW
            SortType.RATING_LOW_TO_HIGH.toString() -> SortType.RATING_LOW_TO_HIGH
            else -> SortType.RATING_HIGH_TO_LOW
        }
    }

    private val getSearchQueryListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SEARCH1", "$query")
                binding.rvSearchSuggestions.visibility = View.GONE
                searchView.clearFocus()
                hideInput()
                if (query == null) return false
                productListViewModel.setProductsWithBrandAndImagesByQuery(query, getSortType()).also {
                    userViewModel.insertSuggestion(query, Calendar.getInstance().timeInMillis)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()) {
                    userViewModel.setSearchHistory()
                }else{
                    userViewModel.updateSearchSuggestion(newText)
                }
                changeSearchImageAndTextVisibility(View.GONE)
//                binding.rvSearchSuggestions.visibility = View.VISIBLE
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
                        adapter.removeSuggestion(position)
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
        searchView.maxWidth = Integer.MAX_VALUE
        searchItem.expandActionView()
        searchView.setOnQueryTextListener(getSearchQueryListener)

        val searchText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            searchText.textCursorDrawable = resources.getDrawable(R.drawable.cursor_primary, null)
        }



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