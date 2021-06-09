package com.example.klorophyl.api

import com.example.klorophyl.model.SigninRequest
import com.example.klorophyl.model.SignupRequest
import com.example.klorophyl.model.UpdatePointsRequest
import com.example.klorophyl.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    companion object {
        //const val BASE_URL = "https://hidden-will-313103.uc.r.appspot.com/"
        const val REGISTER_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoiNjBiM2MzMmU2NTUxNDAwMDE1YWNlNjU3In0sImlhdCI6MTYyMjM5MzY0NiwiZXhwIjoxNjIyODI1NjQ2fQ.WXtjI_scPCDOQGs5jvAsHuE2Ue-6aLwd50MKLNCzjdQ"

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