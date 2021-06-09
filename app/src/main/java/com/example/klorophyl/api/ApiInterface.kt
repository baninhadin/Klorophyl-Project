package com.example.klorophyl.api

import com.example.klorophyl.BuildConfig
import com.example.klorophyl.model.SigninRequest
import com.example.klorophyl.model.SignupRequest
import com.example.klorophyl.model.UpdatePointsRequest
import com.example.klorophyl.model.User
import com.example.klorophyl.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    companion object {
        //const val BASE_URL = "https://hidden-will-313103.uc.r.appspot.com/"
        const val REGISTER_API_KEY = BuildConfig.REGISTER_API_KEY
    }

    @POST("api/users/?api_key=$REGISTER_API_KEY")
    fun createUser(
        @Body signupRequest: SignupRequest
    ): Call<SignupResponse>

    @POST("api/auth/")
    fun loginUser(
        @Body signinRequest: SigninRequest
    ): Call<SigninResponse>

    @PATCH("api/users/{username}")
    suspend fun updateUser(
        @Body updatePointsRequest: UpdatePointsRequest,
        @Path("username") username : String
    ): UsersResponse

    @GET("api/data/current")
    suspend fun getMap() : List<IpuResponse>

    @GET("api/users?sort=points")
    suspend fun getUsers() : List<UsersResponse>

    @GET("api/users/{username}")
    suspend fun getUser(@Path("username") username: String) : UsersResponse

    @GET("api/challenge/{district}")
    suspend fun getChallenges(@Path("district") district : String) : List<OuterData>

    @GET("{url}")
    suspend fun getScanned(@Path("url", encoded = true) url : String) : ChallengeResponse
}