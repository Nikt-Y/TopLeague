package com.yakun.topleague.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Product with required fields.
 */
@Parcelize
data class Lecture(
    var course_id: String = "",
    var title: String = "",
    var image: String = "",
    var lecture_id: String = "",
) : Parcelable