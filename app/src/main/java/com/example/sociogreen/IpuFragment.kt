package com.example.sociogreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class IpuFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var aqi : Int = 0

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        getMapData()

        val placeholder = LatLng(-6.1603721, 106.8473377)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeholder, 12.0f))
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ipu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ipuFragment = childFragmentManager.findFragmentById(R.id.ipu) as SupportMapFragment?
        ipuFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    override fun onMarkerClick(p0: Marker): Boolean{
        getMapAqi(p0.title)
        Toast.makeText(activity, "Indeks Pencemaran Udara: $aqi", Toast.LENGTH_SHORT).show()

        return false
    }

    private fun getMapAqi(name : String) {
        val client = AsyncHttpClient()
        val url = "https://klorophyl-bangkit.herokuapp.com/api/data/current"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val responseArray = JSONArray(result)
                    for(i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)

                        if(responseObject.getString("name") == name){
                            val aqArray = responseObject.getJSONObject("airQuality")
                            aqi = aqArray.getInt("aqi")
                        }
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

    private fun getMapData() {
        val client = AsyncHttpClient()
        val url = "https://klorophyl-bangkit.herokuapp.com/api/data/current"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val responseArray = JSONArray(result)
                    for(i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val name: String = responseObject.getString("name")

                        val geoArray = responseObject.getJSONObject("geo")
                        val latitude = geoArray.getDouble("latitude")
                        val longitude = geoArray.getDouble("longitude")

                        val district = LatLng(latitude, longitude)  // this is New York
                        map.addMarker(MarkerOptions().position(district).title(name))
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
}