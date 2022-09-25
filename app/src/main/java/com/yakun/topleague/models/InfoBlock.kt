package com.yakun.topleague.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for InfoBlock with required fields.
 */
@Parcelize
data class InfoBlock(
    var record_id: String = "",
    var number: Int = 0,
    var is_img: Boolean = false,
    var text: String = "",
    var image: String = "",
    var image_uri: Uri? = null,
    var info_block_id: String = "",
) : Parcelable