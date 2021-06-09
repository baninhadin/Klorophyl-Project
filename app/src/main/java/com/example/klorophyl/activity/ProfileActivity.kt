package com.example.klorophyl.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.klorophyl.R
import com.example.klorophyl.databinding.ActivityProfileBinding
import com.example.klorophyl.model.ProfileViewModel
import com.example.klorophyl.model.Users
import com.example.klorophyl.model.ViewModelFactory

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var username : String? = ""

    private lateinit var binding: ActivityProfileBinding
    private lateinit var data : Users

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgbtnBack.setOnClickListener(this)

        getProfile()
    }

    private fun getProfile(){
        val factory = ViewModelFactory.getInstance(this)
        val viewModel = ViewModelProvider(this@ProfileActivity, factory)[ProfileViewModel::class.java]

        username = intent.getStringExtra(EXTRA_USERNAME)

        username?.let {
            viewModel.setSelectedUser(it)
        }
        viewModel.getUser().observe(this, { item ->
            data = item
            showProfile()
        })
    }

    private fun showProfile(){
        binding.tvFullname.text = data.fullName
        binding.tvEmail.text = data.email
        binding.tvLocation.text = data.location
        binding.tvPoints.text = data.points.toString()
        binding.tvUsername.text = data.userName

        Glide.with(this)
            .load(data.avatar)
            .into(binding.profileAvatar)
    }

    /*private fun getProfile() {
        ApiService.loginApiCall().getProfile(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                Log.d("Response User ::::", response.body().toString())
                if (response.body()!!.status){
                    tv_username.setText(response.body()!!.data.userName)
                    tv_email.setText(response.body()!!.data.email)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
//                            Log.d("error::::",t?.message)
            }

        })
    }*/

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.imgbtn_back -> {
                val intent = Intent(this@ProfileActivity, MainActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_USERNAME, username)
                startActivity(intent)
            }
        }
    }
}