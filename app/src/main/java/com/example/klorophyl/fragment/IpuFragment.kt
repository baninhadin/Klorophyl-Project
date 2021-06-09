package com.example.klorophyl.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.klorophyl.R
import com.example.klorophyl.model.ListMapViewModel
import com.example.klorophyl.model.Map
import com.example.klorophyl.model.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class IpuFragment : Fragment(), GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var aqi : Int = 0
    private lateinit var viewModel: ListMapViewModel

    companion object{
        private val TAG = IpuFragment::class.java.simpleName
    }

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        val placeholder = LatLng(-6.1603721, 106.8473377)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(placeholder, 12.0f))
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ipu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ipuFragment = childFragmentManager.findFragmentById(R.id.ipu) as SupportMapFragment?
        ipuFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())

        activity?.let {
            viewModel = ViewModelProvider(it, factory)[ListMapViewModel::class.java]
        }

        viewModel.getMap().observe(viewLifecycleOwner, { data ->
            getMap(data)
        })
    }

    override fun onMarkerClick(p0: Marker): Boolean{
        viewModel.getMap().observe(viewLifecycleOwner, { data ->
            getMapAqi(data, p0.title)
        })
        Toast.makeText(activity, "Indeks Pencemaran Udara: $aqi", Toast.LENGTH_SHORT).show()

        return false
    }

    private fun getMap(list: List<Map>){
        for(i in list.indices){
            var district = LatLng(list[i].latitude, list[i].longitude)
            map.addMarker(MarkerOptions().position(district).title(list[i].name))
        }
    }

    private fun getMapAqi(list: List<Map>, name: String){
        for(i in list.indices){
            if(list[i].name == name){
                aqi = list[i].aqi
            }
        }
    }
}