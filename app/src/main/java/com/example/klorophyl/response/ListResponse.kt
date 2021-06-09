package com.example.klorophyl.response

data class ListResponse<T> (
    val results: List<T>? = null
)