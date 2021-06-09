package com.example.klorophyl.activity

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.klorophyl.databinding.ActivityQrcodeBinding
import com.example.klorophyl.model.*

class QrcodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrcodeBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var data : Challenge
    private lateinit var user : Users

    private var addPoints : Int = 0

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        private const val CAMERA_REQ = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPermissions()
        codeScanner()
    }

    private fun codeScanner() {
        val factory = ViewModelFactory.getInstance(this)
        val viewModel = ViewModelProvider(this@QrcodeActivity, factory)[ChallengeViewModel::class.java]
        val profileViewModel = ViewModelProvider(this@QrcodeActivity, factory)[ProfileViewModel::class.java]

        val username = intent.getStringExtra(EXTRA_USERNAME)



        codeScanner = CodeScanner(this, binding.scnChallenge)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.tvText.text = it.text

                    it.text.let {
                        viewModel.setScannedChallenge(it)
                    }

                    viewModel.getScanned().observe(this@QrcodeActivity, { item ->
                        data = item
                        addPoints += data.points

                        username?.let{
                            viewModel.setSelectedUser(it)
                        }

                        username?.let{
                            profileViewModel.setSelectedUser(it)
                        }

                        profileViewModel.getUser().observe(this@QrcodeActivity, { item ->
                            user = item
                            addPoints += data.points
                        })

                        viewModel.updateUserPoints(addPoints)

                        val mIntent = Intent(this@QrcodeActivity, MainActivity::class.java)
                        startActivity(mIntent)
                    })
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "codeScanner: ${it.message}")
                }
            }

            binding.scnChallenge.setOnClickListener {
                codeScanner.startPreview()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQ
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQ -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}