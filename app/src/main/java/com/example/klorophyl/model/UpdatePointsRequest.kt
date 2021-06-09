package com.example.klorophyl.model

import com.google.gson.annotations.SerializedName

class UpdatePointsRequest (
    @SerializedName("points") var points: Int
)