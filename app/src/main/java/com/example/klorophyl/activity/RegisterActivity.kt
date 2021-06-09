package com.example.klorophyl.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.klorophyl.R
import com.example.klorophyl.api.ApiService
import com.example.klorophyl.model.SignupRequest
import com.example.klorophyl.response.SignupResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var et_username: TextInputEditText
    private lateinit var rg_locations: RadioGroup
    private lateinit var rb_location1: RadioButton
    private lateinit var rb_location2: RadioButton
    private lateinit var rb_location3: RadioButton
    private lateinit var et_email: TextInputEditText
    private lateinit var et_password: TextInputEditText
    private lateinit var btn_register: MaterialButton
    private lateinit var btn_login: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        et_username = findViewById(R.id.et_username) as TextInputEditText
        rg_locations = findViewById(R.id.rg_locations) as RadioGroup
        rb_location1 = findViewById(R.id.rb_location1) as RadioButton
        rb_location2 = findViewById(R.id.rb_location2) as RadioButton
        rb_location3 = findViewById(R.id.rb_location3) as RadioButton
        et_email = findViewById(R.id.et_email) as TextInputEditText
        et_password = findViewById(R.id.et_password) as TextInputEditText
        btn_register = findViewById(R.id.btn_register) as MaterialButton
        btn_login = findViewById(R.id.btn_login) as MaterialButton

        btn_register.setOnClickListener(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
            R.id.btn_register -> {
                if (validation()) {
                    val json = JSONObject()
                    json.put("email", et_email.text.toString())
                    json.put("userName", et_username.text.toString())
                    json.put("password", et_password.text.toString())

                    val selectedId: Int = rg_locations.getCheckedRadioButtonId()

                    var location : String = ""

                    if (selectedId == rb_location1.getId()) {
                        location = rb_location1.text.toString()
                    } else if (selectedId == rb_location2.getId()) {
                        location = rb_location2.text.toString()
                    } else if (selectedId == rb_location3.getId()) {
                        location = rb_location3.text.toString()
                    } else {
                        Toast.makeText(applicationContext,"Anda belum memilih lokasi",Toast.LENGTH_SHORT).show()
                    }

                    ApiService.loginApiCall().createUser(
                        SignupRequest(
                            et_username.text.toString(),
                            et_email.text.toString(),
                            et_password.text.toString(),
                            location
                        )
                    ).enqueue(object : Callback<SignupResponse> {
                        override fun onResponse(
                            call: Call<SignupResponse>,
                            response: Response<SignupResponse>
                        ) {

                            Log.d("Response::::", response.body().toString())
                            val loginResponse: SignupResponse
                            loginResponse = response.body()!!
                            if (loginResponse.status) {
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Akun anda telah terdaftar",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                        }

                    })
                }
            }
        }
    }

    private fun validation(): Boolean {
        var value = true

        val email = et_email.text.toString().trim()
        val password = et_password.text.toString().trim()
        val username = et_username.text.toString().trim()

        if (username.isEmpty()) {
            et_username.error = "Nama pengguna harus diisi"
            et_username.requestFocus()
            value = false
        }
        if (email.isEmpty()) {
            et_email.error = "Email harus diisi"
            et_email.requestFocus()
            value = false
        }
        if (password.isEmpty()) {
            et_password.error = "Password harus diisi"
            et_password.requestFocus()
            value = false
        }

        return value;
    }

}