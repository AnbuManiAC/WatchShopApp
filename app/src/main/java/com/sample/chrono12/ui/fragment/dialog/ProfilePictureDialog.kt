package com.sample.chrono12.ui.fragment.dialog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sample.chrono12.databinding.FragmentProfilePictureDialogBinding
import com.sample.chrono12.data.models.ProfileSettingAction
import com.sample.chrono12.viewmodels.UserViewModel

class ProfilePictureDialog : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentProfilePictureDialogBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private val navArgs by navArgs<ProfilePictureDialogArgs>()

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

        if(navArgs.hasPicture){
            binding.btnDelete.visibility = View.VISIBLE
        }

        binding.tvTakePhoto.setOnClickListener {
            userViewModel.setProfileSettingAction(ProfileSettingAction.TAKE_PHOTO)
            dismiss()
        }
        binding.tvChooseFromGallery.setOnClickListener {
            userViewModel.setProfileSettingAction(ProfileSettingAction.CHOOSE_FROM_GALLERY)
            dismiss()
        }
        binding.btnDelete.setOnClickListener {
            userViewModel.setProfileSettingAction(ProfileSettingAction.DELETE)
            dismiss()
        }
    }
}