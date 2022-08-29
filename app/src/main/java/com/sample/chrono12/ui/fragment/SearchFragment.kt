package com.sample.chrono12.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentHomeBinding
import com.sample.chrono12.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchView = binding.svSearch
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(getSearchQueryListener(view))

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

            } else {

            }
        }
    }

    private fun getSearchQueryListener(view: View) =
        object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        }

    private fun hideInput() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            binding.svSearch.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}