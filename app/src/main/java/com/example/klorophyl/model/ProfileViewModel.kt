package com.example.klorophyl.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.klorophyl.data.ItemRepository

class ProfileViewModel constructor(private val itemRepository: ItemRepository) : ViewModel() {
    private var username : String = ""

    fun getUser() : LiveData<Users> = itemRepository.getUserData(username)

    fun setSelectedUser(username : String){
        this.username = username
    }
}