package com.example.klorophyl.model

import com.google.gson.annotations.SerializedName

class SigninRequest (
    @SerializedName("userName") var username: String,
    @SerializedName("password") var password: String
)