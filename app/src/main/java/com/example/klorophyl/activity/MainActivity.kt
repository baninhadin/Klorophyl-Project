package com.example.klorophyl.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.klorophyl.R
import com.example.klorophyl.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public var username : String = ""

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)
        supportActionBar?.setTitle("Klorophyl")
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val navView: BottomNavigationView = findViewById(R.id.bnv)
        val navController = findNavController(R.id.nav_fragment)

        navView.setupWithNavController(navController)

        username = intent.getStringExtra(ProfileActivity.EXTRA_USERNAME)!!
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.navigation_account) {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.putExtra(ProfileActivity.EXTRA_USERNAME, username)
            startActivity(intent)
        }
        if (item.itemId == R.id.navigation_qrcode) {
            val intent = Intent(this@MainActivity, QrcodeActivity::class.java)
            intent.putExtra(QrcodeActivity.EXTRA_USERNAME, username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}