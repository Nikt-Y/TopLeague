package com.yakun.topleague.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Course with required fields.
 */
@Parcelize
data class Course(
    var title: String = "",
    var image: String = "",
    var course_id: String ="",
    ): Parcelable