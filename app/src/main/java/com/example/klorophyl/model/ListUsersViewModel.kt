package com.example.klorophyl.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.klorophyl.data.ItemRepository

class ListUsersViewModel constructor(private val itemRepository: ItemRepository) : ViewModel() {
    fun getUsers() : LiveData<List<Users>> = itemRepository.getUsersData()
}