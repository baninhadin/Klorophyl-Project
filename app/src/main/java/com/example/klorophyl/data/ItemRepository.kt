package com.example.klorophyl.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.klorophyl.model.Challenge
import com.example.klorophyl.model.Map
import com.example.klorophyl.model.UpdatePointsRequest
import com.example.klorophyl.model.Users
import com.example.klorophyl.remote.RemoteDataSource
import com.example.klorophyl.response.ChallengeResponse
import com.example.klorophyl.response.IpuResponse
import com.example.klorophyl.response.OuterData
import com.example.klorophyl.response.UsersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemRepository private constructor(private val remoteDataSource: RemoteDataSource) : ItemDataSource {
    companion object {
        @Volatile
        private var instance: ItemRepository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): ItemRepository =
            instance ?: synchronized(this) {
                instance ?: ItemRepository(remoteDataSource)
            }
    }

    override fun getMapData(): LiveData<List<Map>> {
        val result = MutableLiveData<List<Map>>()
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getMapData(object : RemoteDataSource.LoadMapDataCallback{
                override fun onMapDataReceived(response: List<IpuResponse>) {
                    val list = ArrayList<Map>()
                    for (resp in response){
                        val map = Map(
                            resp.name,
                            resp.airQuality.aqi,
                            resp.geo.latitude,
                            resp.geo.longitude
                        )
                        list.add(map)
                    }
                    result.postValue(list)
                }
            })
        }
        return result
    }

    override fun getUsersData(): LiveData<List<Users>> {
        val result = MutableLiveData<List<Users>>()
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getUsersData(object : RemoteDataSource.LoadUsersDataCallback{
                override fun onUsersDataReceived(response: List<UsersResponse>) {
                    val list = ArrayList<Users>()
                    for (resp in response){
                        val users = Users(
                            resp.points,
                            resp.avatar,
                            resp.createDate,
                            resp._id,
                            resp.fullName,
                            resp.userName,
                            resp.email,
                            resp.password,
                            resp.location,
                            resp.__v
                        )
                        list.add(users)
                    }
                    result.postValue(list)
                }
            })
        }
        return result
    }

    override fun getUserData(username: String): LiveData<Users> {
        val result = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getUserData(username, object : RemoteDataSource.LoadUserDataCallback{
                override fun onUserDataReceived(response: UsersResponse) {
                    val user = Users(
                        response.points,
                        response.avatar,
                        response.createDate,
                        response._id,
                        response.fullName,
                        response.userName,
                        response.email,
                        response.password,
                        response.location,
                        response.__v
                    )

                    result.postValue(user)
                }
            })
        }

        return result
    }

    override fun getChallengesData(district: String): LiveData<List<Challenge>> {
        val result = MutableLiveData<List<Challenge>>()
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getChallengesData(district, object : RemoteDataSource.LoadChallengesDataCallback{
                override fun onChallengesDataReceived(response: List<OuterData>) {
                    val list = ArrayList<Challenge>()
                    for (resp in response){
                        val challenge_co = Challenge(
                            resp.challenge_co.name,
                            resp.challenge_co.description,
                            resp.challenge_co.points,
                            resp.challenge_co.qrcode
                        )
                        list.add(challenge_co)
                        val challenge_pm10 = Challenge(
                            resp.challenge_pm10.name,
                            resp.challenge_pm10.description,
                            resp.challenge_pm10.points,
                            resp.challenge_pm10.qrcode
                        )
                        list.add(challenge_pm10)
                        val challenge_o3 = Challenge(
                            resp.challenge_o3.name,
                            resp.challenge_o3.description,
                            resp.challenge_o3.points,
                            resp.challenge_o3.qrcode
                        )
                        list.add(challenge_o3)
                        val challenge_so2 = Challenge(
                            resp.challenge_so2.name,
                            resp.challenge_so2.description,
                            resp.challenge_so2.points,
                            resp.challenge_so2.qrcode
                        )
                        list.add(challenge_so2)
                        val challenge_no2 = Challenge(
                            resp.challenge_no2.name,
                            resp.challenge_no2.description,
                            resp.challenge_no2.points,
                            resp.challenge_no2.qrcode
                        )
                        list.add(challenge_no2)
                    }
                    result.postValue(list)
                }
            })
        }

        return result
    }

    override fun getScannedData(url: String): LiveData<Challenge> {
        url.replace("%2F", "/")
        val result = MutableLiveData<Challenge>()
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.getScannedData(url, object : RemoteDataSource.LoadScannedDataCallback{
                override fun onScannedDataReceived(response: ChallengeResponse) {
                    val scanned = Challenge(
                        response.name,
                        response.description,
                        response.points,
                        response.qrcode
                    )

                    result.postValue(scanned)
                }
            })
        }

        return result
    }

    override fun updateUserPoints(updatePointsRequest: UpdatePointsRequest, username: String) {
        CoroutineScope(Dispatchers.IO).launch {
            remoteDataSource.updateUserPoints(updatePointsRequest, username, object : RemoteDataSource.LoadUserPointsCallback{
                override fun onUserPointsReceived(response: UsersResponse) {

                }
            })
        }
    }
}