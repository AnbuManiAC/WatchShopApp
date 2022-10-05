package com.sample.chrono12.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.sample.chrono12.R
import com.sample.chrono12.data.models.ProfileSettingAction.*
import com.sample.chrono12.databinding.FragmentProfileBinding
import com.sample.chrono12.databinding.LoginPromptBinding
import com.sample.chrono12.utils.safeNavigate
import com.sample.chrono12.viewmodels.UserViewModel
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var bindingLoginPrompt: LoginPromptBinding
    private val userViewModel by lazy { ViewModelProvider(requireActivity())[UserViewModel::class.java] }
    private var isUserLoggedIn = false
    private var hasProfilePicture = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isUserLoggedIn = userViewModel.getIsUserLoggedIn()
        return if (isUserLoggedIn) {
            bindingProfile = FragmentProfileBinding.inflate(layoutInflater)
            bindingProfile.root
        } else {
            bindingLoginPrompt = LoginPromptBinding.inflate(layoutInflater)
            bindingLoginPrompt.root
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        if (isUserLoggedIn) {
            setupProfile()
            setupProfilePicture()
        } else {
            setupLoginPrompt()
        }
    }

    private fun setupProfilePicture() {
        bindingProfile.btnProfilePicture.setOnClickListener {
            findNavController().safeNavigate(
                ProfileFragmentDirections.actionProfileFragmentToProfilePictureDialog(
                    hasProfilePicture
                )
            )
        }
        userViewModel.getProfileSettingAction().observe(viewLifecycleOwner) { action ->
            when (action) {
                TAKE_PHOTO -> {
                    if (checkAndRequestCameraPermissions()) {
                        takePictureFromCamera()
                    }
                    userViewModel.setProfileSettingAction(NO_ACTION)
                }
                CHOOSE_FROM_GALLERY -> {
                    if (checkAndRequestGalleryPermissions()) {
                        takePictureFromGallery()
                    }
                    userViewModel.setProfileSettingAction(NO_ACTION)
                }
                DELETE -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Remove profile picture")
                        .setPositiveButton("Remove") { _, _ ->
                            userViewModel.deleteProfilePicture(
                                userViewModel.getLoggedInUser().toInt()
                            )
                        }
                        .setNegativeButton("Cancel") { _, _ ->

                        }
                        .setCancelable(false)
                    builder.create().show()
                    userViewModel.setProfileSettingAction(NO_ACTION)
                }
                else -> {}
            }
        }

    }

    private fun checkAndRequestGalleryPermissions(): Boolean {
        val galleryPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (galleryPermission == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                30
            )
            return false
        }
        return true
    }

    private fun checkAndRequestCameraPermissions(): Boolean {
        val cameraPermission: Int =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//        val galleryWritePermission: Int =
//            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (cameraPermission == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA), 20
            )
            return false
        }

        return true
    }

    private fun setupProfile() {
        userViewModel.getUserDetails().observe(viewLifecycleOwner) { user ->
            bindingProfile.tvUserName.text = user.name
            bindingProfile.tvEmail.text = user.email
            bindingProfile.tvMobileNumber.text = user.mobile
            if (user.image.isNullOrEmpty()) {
                bindingProfile.ivProfilePicture.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_profile_picture,
                        null
                    )
                )
                hasProfilePicture = false
            } else {
                hasProfilePicture = try {
                    val bitmap = BitmapFactory.decodeFile(user.image)
                    bindingProfile.ivProfilePicture.setImageBitmap(bitmap)
                    true
                } catch (e: Exception) {
                    bindingProfile.ivProfilePicture.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_profile_picture,
                            null
                        )
                    )
                    false
                }
            }
        }
        bindingProfile.myWishList.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToWishlistFragment())
        }
        bindingProfile.myOrders.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToOrderHistoryFragment())
        }
        bindingProfile.myAddress.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToAddressFragment())
        }
        bindingProfile.myAddressGroup.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToAddressGroupFragment())
        }
        bindingProfile.deleteSearchHistory.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToDeleteSearchDialog())
        }

        bindingProfile.signOut.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToLogoutDialog())
        }
    }

    private fun setupLoginPrompt() {
        bindingLoginPrompt.btnLogIn.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToLogInFragment())
        }
        bindingLoginPrompt.toSignup.setOnClickListener {
            findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToSignUpFragment())
        }
    }


    private fun takePictureFromGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1)
    }

    private fun takePictureFromCamera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, 2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 20) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureFromCamera()
            } else {
                findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToCameraPermissionDialog())
            }
        } else if (requestCode == 30) {
            if (requestCode == 30 && grantResults[0] == PackageManager.PERMISSION_GRANTED) takePictureFromGallery()
            else {
                findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToGalleryPermissionDialog())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                data?.let {
                    val selectedImageUri: Uri? = data.data
                    bindingProfile.ivProfilePicture.setImageURI(selectedImageUri)
                    val selectedImagePath = selectedImageUri?.let { getPathFromUri(it) }.toString()
                    userViewModel.addProfilePicture(
                        selectedImagePath,
                        userViewModel.getLoggedInUser().toInt()
                    )
                }

            }
            2 -> {
                data?.let {
                    val bundle = data.extras
                    bundle?.get("data")?.let {
                        val bitmapImage = bundle.get("data") as Bitmap
                        bindingProfile.ivProfilePicture.setImageBitmap(bitmapImage)
                        val selectedImagePath = getImagePath(bitmapImage)
                        userViewModel.addProfilePicture(
                            selectedImagePath,
                            userViewModel.getLoggedInUser().toInt()
                        )
                    }
                }

            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String {
        val filePath: String
        val cursor: Cursor? =
            requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun getImagePath(bitmap: Bitmap): String {

        val title = UUID.randomUUID().toString()
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, title)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        val uri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val imageOut = requireContext().contentResolver.openOutputStream(uri!!)
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOut)
        } finally {
            imageOut!!.close()
        }
        return getPathFromUri(uri)
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                if (isUserLoggedIn) menuInflater.inflate(R.menu.logout_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.logout) {
                    findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToLogoutDialog())
                    return true
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}