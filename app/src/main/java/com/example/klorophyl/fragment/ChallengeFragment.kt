package com.example.klorophyl.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.klorophyl.activity.DetailChallengeActivity
import com.example.klorophyl.activity.MainActivity
import com.example.klorophyl.adapter.ChallengeAdapter
import com.example.klorophyl.databinding.FragmentChallengeBinding

import com.example.klorophyl.model.*

class ChallengeFragment : Fragment() {

    private var _binding : FragmentChallengeBinding? = null
    private val binding get() = _binding!!
    private var list: ArrayList<Challenge> = arrayListOf()
    private lateinit var challengeViewModel: ChallengeViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var data : Users
    private var location : String = ""

    companion object {
        private val TAG = ChallengeFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireActivity())

        activity?.let {
            challengeViewModel = ViewModelProvider(it, factory)[ChallengeViewModel::class.java]
            profileViewModel = ViewModelProvider(it, factory)[ProfileViewModel::class.java]
        }

        getUser()
    }

    private fun showRecyclerList() {
        binding.rvChallenge.layoutManager = LinearLayoutManager(activity)
        val challengeAdapter = ChallengeAdapter(list)
        binding.rvChallenge.adapter = challengeAdapter

        challengeAdapter.setOnItemClickCallback(object : ChallengeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Challenge) {
                showSelectedInfo(data)
            }
        })
    }

    private fun showSelectedInfo(data: Challenge) {
        val detailChallengeActivity = Intent(activity, DetailChallengeActivity::class.java)
        detailChallengeActivity.putExtra(DetailChallengeActivity.EXTRA_DATA, data)
        startActivity(detailChallengeActivity)
    }

    private fun getListChallenge(listChallenge: List<Challenge>){
        list.clear()
        for(i in listChallenge.indices){
            list.add(
                Challenge(
                    listChallenge[i].name,
                    listChallenge[i].description,
                    listChallenge[i].points,
                    listChallenge[i].qrcode
                )
            )
            showRecyclerList()
        }
    }

    private fun getUser(){
        (activity as MainActivity).username.let{
            profileViewModel.setSelectedUser(it)
        }

        profileViewModel.getUser().observe(viewLifecycleOwner, { item ->
            data = item
            getLocation()
            data.location.let{
                challengeViewModel.setSelectedChallenges(it)
            }

            challengeViewModel.getChallenges().observe(viewLifecycleOwner, { data ->
                getListChallenge(data)
            })
        })
    }

    private fun getLocation(){
        if(data.location == "Jakarta Central (US Consulate), Indonesia"){
            data.location = "Central"
        }else if(data.location == "Jakarta South (US Consulate), Indonesia"){
            data.location = "South"
        }else if(data.location == "Jakarta, Indonesia"){
            data.location = "Jakarta"
        }
    }
}