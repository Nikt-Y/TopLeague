package com.yakun.topleague.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A data model class for Product with required fields.
 */
@Parcelize
data class Task(
    var course_id: String = "",
    var title: String = "",
    var image: String = "",
    var answer: String = "",
    var solved: Boolean = false,
    var task_id: String = "",
) : Parcelable