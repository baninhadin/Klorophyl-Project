package com.example.klorophyl.data

import android.content.Context
import com.example.klorophyl.remote.RemoteDataSource

object Injection {
    fun provideItemRepository(context: Context): ItemRepository {
        val remoteDataSource = RemoteDataSource.getInstance()
        return ItemRepository.getInstance(remoteDataSource)
    }
}