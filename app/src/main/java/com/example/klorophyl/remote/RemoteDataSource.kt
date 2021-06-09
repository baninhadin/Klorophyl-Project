package com.example.klorophyl.remote

import com.example.klorophyl.api.ApiConfig
import com.example.klorophyl.model.UpdatePointsRequest
import com.example.klorophyl.repo.EspressoIdlingResource
import com.example.klorophyl.response.ChallengeResponse
import com.example.klorophyl.response.IpuResponse
import com.example.klorophyl.response.OuterData
import com.example.klorophyl.response.UsersResponse

class RemoteDataSource {
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    suspend fun getMapData(
        callback: LoadMapDataCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.getMap().let { list ->
            callback.onMapDataReceived(
                list
            )
            EspressoIdlingResource.decrement()
        }
    }

    suspend fun getUsersData(
        callback: LoadUsersDataCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.getUsers().let { list ->
            callback.onUsersDataReceived(
                list
            )
            EspressoIdlingResource.decrement()
        }
    }

    suspend fun getScannedData(
        url: String,
        callback: LoadScannedDataCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.getScanned(url).let { list ->
            callback.onScannedDataReceived(
                list
            )
            EspressoIdlingResource.decrement()
        }
    }

    suspend fun getChallengesData(
        district: String,
        callback: LoadChallengesDataCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.getChallenges(district).let { list ->
            callback.onChallengesDataReceived(
                list
            )
            EspressoIdlingResource.decrement()
        }
    }

    suspend fun getUserData(
        username: String,
        callback: LoadUserDataCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.getUser(username).let { user ->
            callback.onUserDataReceived(
                user
            )
            EspressoIdlingResource.decrement()
        }
    }

    suspend fun updateUserPoints(
        updatePointsRequest: UpdatePointsRequest,
        username: String,
        callback: LoadUserPointsCallback
    ) {
        EspressoIdlingResource.increment()
        ApiConfig.instance.updateUser(updatePointsRequest, username).let { user ->
            callback.onUserPointsReceived(
                user
            )
            EspressoIdlingResource.decrement()
        }
    }

    interface LoadMapDataCallback {
        fun onMapDataReceived(response: List<IpuResponse>)
    }

    interface LoadUsersDataCallback {
        fun onUsersDataReceived(response: List<UsersResponse>)
    }

    interface LoadUserDataCallback {
        fun onUserDataReceived(response: UsersResponse)
    }

    interface LoadUserPointsCallback {
        fun onUserPointsReceived(response: UsersResponse)
    }

    interface LoadScannedDataCallback {
        fun onScannedDataReceived(response: ChallengeResponse)
    }

    interface LoadChallengesDataCallback {
        fun onChallengesDataReceived(response: List<OuterData>)
    }
}