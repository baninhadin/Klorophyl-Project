package com.example.klorophyl.model

import com.google.gson.annotations.SerializedName

class SignupRequest (
    @SerializedName("userName") var username: String,
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
    @SerializedName("location") var location: String
)

