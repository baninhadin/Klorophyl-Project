package com.example.klorophyl.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://hidden-will-313103.uc.r.appspot.com/"

    private val httpClient = OkHttpClient.Builder().apply {
    }.build()



    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder().apply {
            client(ApiWorker.client)
            baseUrl(BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
        }
    }

    val instance: ApiInterface by lazy {
        retrofit
            .build()
            .create(ApiInterface::class.java)
    }
}