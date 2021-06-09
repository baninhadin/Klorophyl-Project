package com.example.klorophyl.response

data class IpuResponse (
    var name: String,
    var airQuality: AqResponse,
    var geo: GeoResponse
)