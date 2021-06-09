package com.example.klorophyl.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.klorophyl.ListLeaderboardAdapter
import com.example.klorophyl.R
import com.example.klorophyl.databinding.FragmentLeaderboardBinding
import com.example.klorophyl.model.*
import com.example.klorophyl.model.Map

class LeaderboardFragment : Fragment() {

    private var _binding : FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<User> = arrayListOf()
    private var listKecamatan : MutableList<String> = arrayListOf()
    private var kecamatan : String = ""
    private lateinit var usersViewModel: ListUsersViewModel
    private lateinit var mapViewModel: ListMapViewModel

    companion object {
        private val TAG = LeaderboardFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_leaderboard, listKecamatan)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())

        activity?.let {
            usersViewModel = ViewModelProvider(it, factory)[ListUsersViewModel::class.java]
            mapViewModel = ViewModelProvider(it, factory)[ListMapViewModel::class.java]
        }

        mapViewModel.getMap().observe(viewLifecycleOwner, { data ->
            getListKecamatan(data)
        })

        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            kecamatan = binding.autoCompleteTextView.text.toString()
            Log.e(TAG, kecamatan)

            usersViewModel.getUsers().observe(viewLifecycleOwner, { data ->
                getUserLeaderboard(data)
            })
        }
    }

    private fun showRecyclerList() {
        binding.rvInfos.layoutManager = LinearLayoutManager(activity)
        val listLeaderboardAdapter = ListLeaderboardAdapter(list)
        binding.rvInfos.adapter = listLeaderboardAdapter
    }

    private fun getListKecamatan(list: List<Map>){
        for(i in list.indices){
            listKecamatan.add(list[i].name)
        }
    }

    private fun getUserLeaderboard(listUser: List<Users>){
        list.clear()
        for(i in listUser.indices){
            if(listUser[i].location == kecamatan) {
                list.add(
                    User(
                        listUser[i]._id,
                        listUser[i].fullName,
                        null,
                        null,
                        null,
                        listUser[i].points,
                        listUser[i].avatar
                    )
                )
                showRecyclerList()
            }
        }
    }
}