package com.yakun.topleague.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.yakun.topleague.R
import java.io.IOException

/**
 * A custom object to create a common functions for Glide which can be used in whole application.
 */
class GlideLoader(val context: Context) {

    /**
     * A function to load image from Uri or URL for the user profile picture.
     */
    fun loadUserPicture(image: Any, imageView: ImageView) {

        try {
            // Load the user image in the ImageView
            Glide
                .with(context)
                .load(image)
                .centerCrop() // Scale type of the image.
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * A function to load image from Uri or URL for the product image.
     */
    fun loadRecordPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            Glide
                .with(context)
                .load(image) // Uri or URL of the image
                .centerCrop() // Scale type of the image.
                .into(imageView) // the view in which the image will be loaded.
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}