package com.example.klorophyl.data

import androidx.lifecycle.LiveData
import com.example.klorophyl.model.Challenge
import com.example.klorophyl.model.Map
import com.example.klorophyl.model.UpdatePointsRequest
import com.example.klorophyl.model.Users

interface ItemDataSource {
    fun getMapData(): LiveData<List<Map>>

    fun getUsersData(): LiveData<List<Users>>

    fun getUserData(username: String): LiveData<Users>

    fun getChallengesData(district: String): LiveData<List<Challenge>>

    fun getScannedData(url: String): LiveData<Challenge>

    fun updateUserPoints(updatePointsRequest: UpdatePointsRequest, username: String)
}