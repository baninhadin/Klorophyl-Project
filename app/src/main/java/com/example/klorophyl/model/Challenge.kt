package com.example.klorophyl.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Challenge (
    var name : String,
    var description : String,
    var points : Int,
    var qrcode : String
) : Parcelable
