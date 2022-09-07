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
import com.sample.chrono12.viewmodels.ProductListViewModel

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding
    private lateinit var adapter: FilterValuesAdapter
    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFilterAdapter()
        setupFilterValueAdapter()
        setDataToFilterValueAdapter(genders)
        setupClearButton()
        setupApplyButton()
    }

    private fun setupApplyButton() {
        binding.btnApply.setOnClickListener {
            val filterInput = adapter.getSelectedCheckBoxIds()
            val genderInput = filterInput.filter { it in 1..3 }
            val dialShapeInput = filterInput.filter { it in 4..6 }
            val displayTypeInput = filterInput.filter { it in 7..11 }
            val brandInput = brands.filter { (key: Int) -> key in filterInput }.values.toList()

//            if(genderInput.isNotEmpty() && dialShapeInput.isNotEmpty() && displayTypeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(genderInput, dialShapeInput, displayTypeInput, brandInput)
//            }
//            else if(genderInput.isNotEmpty() && dialShapeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args1 = genderInput, args2 =  dialShapeInput, args4 =  brandInput)
//            }
//            else if(dialShapeInput.isNotEmpty() && displayTypeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args2 =  dialShapeInput, args3 =  displayTypeInput, args4 =  brandInput)
//            }
//            else if(genderInput.isNotEmpty() && displayTypeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args1 = genderInput, args3 =  displayTypeInput, args4 =  brandInput)
//            }
//            else if(genderInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args1 = genderInput, args4 =  brandInput)
//            }
//            else if(dialShapeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args2 = dialShapeInput, args4 =  brandInput)
//            }
//            else if(displayTypeInput.isNotEmpty()){
//                productListViewModel.setFilterResult(args3 = displayTypeInput, args4 =  brandInput)
//            }

            productListViewModel.setFilterResult(genderInput, dialShapeInput, displayTypeInput, brandInput)

            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToProductListFragment())
        }
    }

    private fun setupClearButton() {
        binding.btnClear.setOnClickListener {
            adapter.clearCheckBox()
            adapter.notifyDataSetChanged()
        }
    }

    private fun setDataToFilterValueAdapter(filterValues: HashMap<Int, String>) {
        adapter.setData(filterValues)
        adapter.notifyDataSetChanged()
    }

    private fun setupFilterAdapter() {
        binding.rvFilter.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFilter.adapter = FilterAdapter(
            filterList,
            getOnFilterClickListener
        )
    }

    private val getOnFilterClickListener = object : FilterAdapter.OnClickFilter {
        override fun onClick(filter: String) {
            when (filter) {
                "Gender" -> setDataToFilterValueAdapter(genders)
                "Dial Shape" -> setDataToFilterValueAdapter(dialShapes)
                "Display type" -> setDataToFilterValueAdapter(displayTypes)
                "Brand" -> setDataToFilterValueAdapter(brands)
                "Price Range" -> setDataToFilterValueAdapter(priceRanges)
                "Rating" -> setDataToFilterValueAdapter(ratings)
            }
        }
    }

    private fun setupFilterValueAdapter() {
        binding.rvFilterValues.layoutManager = LinearLayoutManager(requireContext())
        adapter = FilterValuesAdapter()
        binding.rvFilterValues.adapter = adapter
    }

}
