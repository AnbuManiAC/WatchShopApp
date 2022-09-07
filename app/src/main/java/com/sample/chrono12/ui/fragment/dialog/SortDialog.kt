package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sample.chrono12.R
import com.sample.chrono12.data.models.SortType.*
import com.sample.chrono12.databinding.FragmentSortDialogBinding
import com.sample.chrono12.viewmodels.ProductListViewModel

class SortDialog : BottomSheetDialogFragment() {

    private val productListViewModel by lazy { ViewModelProvider(requireActivity())[ProductListViewModel::class.java] }
    private lateinit var binding: FragmentSortDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSortDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortRadioGroup = binding.rgSort

        productListViewModel.getSortType()?.let { sortType ->
            when(sortType){
                PRICE_LOW_TO_HIGH -> sortRadioGroup.check(R.id.rbPriceLowToHigh)
                PRICE_HIGH_TO_LOW -> sortRadioGroup.check(R.id.rbPriceHighToLow)
                RATING_LOW_TO_HIGH -> sortRadioGroup.check(R.id.rbRatingLowToHigh)
                RATING_HIGH_TO_LOW -> sortRadioGroup.check(R.id.rbRatingHighToLow)
            }
        }

        sortRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            Log.i("ProdBottomSheetSort","checkedId - $checkedId")
            when(checkedId){
                R.id.rbPriceHighToLow -> productListViewModel.sortProductList(PRICE_HIGH_TO_LOW)
                R.id.rbPriceLowToHigh -> productListViewModel.sortProductList(PRICE_LOW_TO_HIGH)
                 R.id.rbRatingHighToLow -> productListViewModel.sortProductList(RATING_HIGH_TO_LOW)
                R.id.rbRatingLowToHigh -> productListViewModel.sortProductList(RATING_LOW_TO_HIGH)
            }
            dismiss()
        }
    }

}