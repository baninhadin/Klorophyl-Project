package com.example.klorophyl.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.klorophyl.R
import com.example.klorophyl.databinding.ActivityDetailChallengeBinding
import com.example.klorophyl.model.Challenge

class DetailChallengeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailChallengeBinding

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getChallenge()
    }

    private fun getChallenge() {
        val data = intent.getParcelableExtra<Challenge>(EXTRA_DATA) as Challenge

        binding.tvChallengeName.text = data.name
        binding.tvChallengeDesc.text = data.description
        binding.tvChallengePoint.text = data.points.toString()
    }
}
