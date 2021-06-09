package com.example.klorophyl.response

data class AqResponse (
    var aqi: Int,
    var co: Double,
    var o3: Double,
    var so2: Double,
    var no2: Double,
    var pm10: Double,
    var pm25: Double
)