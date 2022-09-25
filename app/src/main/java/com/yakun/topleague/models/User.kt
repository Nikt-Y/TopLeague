package com.yakun.topleague.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: String = "",
    val gender: String = "Male",
    val admin: Int = 0
) : Parcelable