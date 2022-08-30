package com.sample.chrono12.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.sample.chrono12.R
import com.sample.chrono12.databinding.FragmentNewAddressBinding
import com.sample.chrono12.ui.activity.HomeActivity

class NewAddressFragment : Fragment() {

    lateinit var binding: FragmentNewAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentNewAddressBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.done_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.done){

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (requireActivity() as HomeActivity).setBackButtonAs(R.drawable.ic_close)
    }

}