package com.example.klorophyl.response

import com.example.klorophyl.model.User

class SignupResponse (
    val status: Boolean, val message:String, val data: User
)