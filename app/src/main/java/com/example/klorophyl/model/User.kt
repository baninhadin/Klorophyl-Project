package com.example.klorophyl.model

data class User (
    var id: String = "",
    var userName: String = "",
    var email: String? ="",
    var password: String? = "",
    var createDate: String? = "",
    var points: Int? = 0,
    var avatar: String? = ""
)