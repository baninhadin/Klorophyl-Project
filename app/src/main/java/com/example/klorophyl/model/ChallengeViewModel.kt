package com.example.klorophyl.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.klorophyl.data.ItemRepository

class ChallengeViewModel constructor(private val itemRepository: ItemRepository) : ViewModel() {
    var district : String = ""
    var url : String = ""
    var username : String = ""

    fun getChallenges() : LiveData<List<Challenge>> = itemRepository.getChallengesData(district)

    fun getScanned() : LiveData<Challenge> = itemRepository.getScannedData(url)

    fun updateUserPoints(points : Int) = itemRepository.updateUserPoints(UpdatePointsRequest(points) , username)

    fun setSelectedUser(username : String){
        this.username = username
    }

    fun setSelectedChallenges(district : String){
        this.district = district
    }

    fun setScannedChallenge(url : String){
        this.url = url
        url.replace("%2F", "/")
    }
}