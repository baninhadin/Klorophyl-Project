package com.example.klorophyl.response

import com.example.klorophyl.model.Users

class SigninResponse (
    val token: String?, val message:String, val data: Users
)