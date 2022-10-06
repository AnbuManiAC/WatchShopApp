package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.chrono12.databinding.FragmentFilterBinding
import com.sample.chrono12.ui.adapter.FilterAdapter
import com.sample.chrono12.ui.adapter.FilterValuesAdapter
import com.sample.chrono12.viewmodels.FilterViewModel
import com.sample.chrono12.viewmodels.ProductListViewModel

class FilterFragment : Fragment() {

    private lateinit var filterAdapter: FilterAdapter
    private lateinit var binding: FragmentFilterBinding
    private lateinit var filterValuesAdapter: FilterValuesAdapter
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private val filterViewModel by lazy { ViewModelProvider(requireActivity())[FilterViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFilterValueAdapter()
        setupFilterAdapter()
        setupClearButton()
        setupApplyButton()
    }

    private fun setupApplyButton() {
        binding.btnApply.setOnClickListener {
            filterViewModel.setAppliedFilterIds(filterViewModel.selectedFilterIds)
            if (filterViewModel.isClearClicked) filterViewModel.clearSelectedFilterIds()
            setFilter()
            findNavController().navigateUp()
        }
    }

    private fun setFilter() {
        val filterInput = filterViewModel.appliedFilterIds
        val genderInput = filterInput.filter { it in 1..3 }
        val dialShapeInput = filterInput.filter { it in 4..6 }
        val displayTypeInput = filterInput.filter { it in 7..11 }
        val brandInput = brands.filter { (key: Int) -> key in filterInput }.values.toList()
        val ratingInput = ratingList.filter { (key: Int) -> key in filterInput }.values.toList()
        val priceInput = priceList.filter { (key: Int) -> key in filterInput }.values.toList()

        productListViewModel.setFilterResult(
            genderInput,
            dialShapeInput,
            displayTypeInput,
            brandInput,
            priceInput,
            ratingInput
        )
    }

    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            filterViewModel.isClearClicked = true
            filterValuesAdapter.setSelectedFilterIds(filterViewModel.selectedFilterIds)
            filterValuesAdapter.notifyDataSetChanged()
        }
    }

    private fun setDataToFilterValueAdapter(filterValues: HashMap<Int, String>) {
        filterValuesAdapter.setData(filterValues)
        filterValuesAdapter.setSelectedFilterIds(filterViewModel.selectedFilterIds)
        filterValuesAdapter.notifyDataSetChanged()
    }

    private fun setupFilterAdapter() {
        binding.rvFilter.layoutManager = LinearLayoutManager(requireActivity())
        filterAdapter = FilterAdapter(
            filterList,
            getOnFilterClickListener
        ) { position ->
            filterViewModel.setSelectedFilterPosition(position)
            setSelectedPosition(position)
        }
        filterAdapter.setSelectedPosition(filterViewModel.selectedFilterPosition)
        binding.rvFilter.adapter = filterAdapter
    }

    private fun setSelectedPosition(selectedId: Int) {
        filterAdapter.setSelectedPosition(selectedId)
    }

    private val getOnFilterClickListener = object : FilterAdapter.OnClickFilter {
        override fun onClick(filter: String) {
            when (filter) {
                filterList[0] -> setDataToFilterValueAdapter(genders)
                filterList[1] -> setDataToFilterValueAdapter(dialShapes)
                filterList[2] -> setDataToFilterValueAdapter(displayTypes)
                filterList[3] -> setDataToFilterValueAdapter(brands)
                filterList[4] -> setDataToFilterValueAdapter(priceRanges)
                filterList[5] -> setDataToFilterValueAdapter(ratings)
            }
        }
    }

    private fun setupFilterValueAdapter() {
        binding.rvFilterValues.layoutManager = LinearLayoutManager(requireContext())
        filterViewModel.setSelectedFilterIds(filterViewModel.appliedFilterIds)
        filterValuesAdapter = FilterValuesAdapter { filterId, isChecked ->
            filterViewModel.addDeleteSelectedFilter(filterId, isChecked)
            filterValuesAdapter.setSelectedFilterIds(filterViewModel.selectedFilterIds)
        }
        binding.rvFilterValues.adapter = filterValuesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterViewModel.isClearClicked = false
        filterViewModel.clearSelectedFilterPosition()
    }

    companion object {
        private val filterList =
            listOf("Gender", "Dial Shape", "Display type", "Brand", "Price Range", "Rating")
        val genders = hashMapOf(
            1 to "Men's Watch",
            2 to "Women's Watch",
            3 to "Unisex Watch"
        )
        val dialShapes = hashMapOf(
            4 to "Square Dial Watch",
            5 to "Round Dial Watch",
            6 to "Other Shape Dial"
        )
        val displayTypes = hashMapOf(
            7 to "Analog Watch",
            8 to "Digital Watch",
            9 to "Smart Watch",
            10 to "Chronograph Watch",
            11 to "Analog-Digital Watch"
        )
        val brands = hashMapOf(
            12 to "Fastrack",
            13 to "Titan",
            14 to "Sonata",
            15 to "Timex",
            16 to "Maxima",
            17 to "Helix",
            18 to "Fossil"
        )
        val priceRanges = hashMapOf(
            19 to "Rs. 1000 and Below",
            20 to "Rs.1001 - Rs. 2000",
            21 to "Rs. 2001 - Rs. 3000",
            22 to "Rs. 3001 - Rs. 5000",
            23 to "Rs. 5000 and Above"
        )
        val ratings = hashMapOf(
            24 to "4 - 5⭐",
            25 to "3 - 4⭐",
            26 to "2 - 3⭐",
            27 to "1 - 2⭐",
            28 to "0 - 1⭐"
        )

        val priceList = hashMapOf(
            19 to Pair(0, 1000),
            20 to Pair(1001, 2000),
            21 to Pair(2001, 3000),
            22 to Pair(3001, 5000),
            23 to Pair(5000, 1000000)
        )

        val ratingList = hashMapOf(
            24 to Pair(4, 5),
            25 to Pair(3, 4),
            26 to Pair(2, 3),
            27 to Pair(1, 2),
            28 to Pair(0, 1)
        )
    }

}
