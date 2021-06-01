package com.example.sociogreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sociogreen.databinding.FragmentLeaderboardBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import org.json.JSONArray
import org.json.JSONObject

class LeaderboardFragment : Fragment() {
    private var _binding : FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<User> = arrayListOf()
    private var listKecamatan : MutableList<String> = arrayListOf()

    companion object {
        private val TAG = LeaderboardFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        getListKecamatan()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_leaderboard, listKecamatan)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserLeaderboard()
    }

    private fun showRecyclerList() {
        rv_infos.layoutManager = LinearLayoutManager(activity)
        val listLeaderboardAdapter = ListLeaderboardAdapter(list)
        rv_infos.adapter = listLeaderboardAdapter
    }

    private fun getListKecamatan() {
        val client = AsyncHttpClient()
        val url = "https://klorophyl-bangkit.herokuapp.com/api/data/current"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseArray = JSONArray(result)
                    for(i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val name: String = responseObject.getString("name")

                        listKecamatan.add(name)
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserLeaderboard() {
        val client = AsyncHttpClient()
        val url = "https://klorophyl-bangkit.herokuapp.com/api/users"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseArray = JSONArray(result)
                    for(i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val username: String = responseObject.getString("userName")
                        getUserProfile(username)
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfile(uName: String){
        val client = AsyncHttpClient()
        val url = "https://klorophyl-bangkit.herokuapp.com/api/profile/$uName"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val id: String = responseObject.getString("_id")
                    val name: String = responseObject.getString("fullName")
                    val points: Int = responseObject.getInt("points")
                    val avatar: String = responseObject.getString("avatar")

                    list.add(
                        User(
                            id,
                            name,
                            null,
                            null,
                            null,
                            points,
                            avatar
                        )
                    )
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}