package com.yakun.topleague.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.yakun.topleague.R
import com.yakun.topleague.firestore.FirestoreClass
import com.yakun.topleague.models.User
import com.yakun.topleague.ui.activities.LoginActivity
import com.yakun.topleague.ui.activities.UserProfileActivity
import com.yakun.topleague.utils.Constants
import com.yakun.topleague.utils.GlideLoader
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment() {
    // A variable for user details which will be initialized later on.
    private lateinit var mUserDetails: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
        tv_edit.setOnClickListener { editOnClick() }
        btn_logout.setOnClickListener { btnLogoutOnClick() }
    }

    fun editOnClick() {
        val intent = Intent(requireContext(), UserProfileActivity::class.java)
        intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
        startActivity(intent)
    }

    fun btnLogoutOnClick() {

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * A function to get the user details from firestore.
     */
    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@ProfileFragment)
    }

    /**
     * A function to receive the user details and populate it in the UI.
     */
    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        // Hide the progress dialog
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(requireActivity()).loadUserPicture(user.image, iv_user_photo)

        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        if (user.mobile.isNotEmpty()) {
            tv_mobile_number.text = user.mobile
        } else {
            tv_mobile_number.text = "No Mobile Number"
        }
    }
}