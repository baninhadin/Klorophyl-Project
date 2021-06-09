package com.example.klorophyl.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.klorophyl.data.ItemRepository

class ListMapViewModel constructor(private val itemRepository: ItemRepository) : ViewModel() {
    fun getMap() : LiveData<List<Map>> = itemRepository.getMapData()
}