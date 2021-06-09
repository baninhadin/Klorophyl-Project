package com.example.klorophyl.response

data class OuterData (
    var challenge_co : ChallengeResponse,
    var challenge_pm10 : ChallengeResponse,
    var challenge_o3 : ChallengeResponse,
    var challenge_so2 : ChallengeResponse,
    var challenge_no2 : ChallengeResponse,
    var date : String?,
    var location : String?
)