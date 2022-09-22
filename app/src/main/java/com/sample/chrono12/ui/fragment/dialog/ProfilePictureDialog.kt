package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sample.chrono12.databinding.FragmentProfilePictureDialogBinding
import com.sample.chrono12.data.models.ProfileSettingAction
import com.sample.chrono12.viewmodels.UserViewModel

class ProfilePictureDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentProfilePictureDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePictureDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTakePhoto.setOnClickListener {
            userViewModel.setProfileSettingAction(ProfileSettingAction.TAKE_PHOTO)
            dismiss()
        }
        binding.tvChooseFromGallery.setOnClickListener {
            userViewModel.setProfileSettingAction(ProfileSettingAction.CHOOSE_FROM_GALLERY)
            dismiss()
        }

    }


}