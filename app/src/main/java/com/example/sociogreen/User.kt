package com.example.sociogreen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var _id: String = "",
    var username: String = "",
    var email: String? ="",
    var password: String? = "",
    var createDate: String? = "",
    var points: Int? = 0,
    var avatar: String? = ""
) : Parcelable