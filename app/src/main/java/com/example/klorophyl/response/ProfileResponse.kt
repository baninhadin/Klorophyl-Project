package com.example.klorophyl.response

import com.example.klorophyl.model.User

data class ProfileResponse (
    val status: Boolean, val data: User
)