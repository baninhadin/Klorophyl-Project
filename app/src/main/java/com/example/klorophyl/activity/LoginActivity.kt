package com.example.klorophyl.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.klorophyl.R
import com.example.klorophyl.api.ApiService
import com.example.klorophyl.model.SigninRequest
import com.example.klorophyl.response.SigninResponse
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var et_username : TextInputEditText
    private lateinit var et_password : TextInputEditText
    private lateinit var btn_login : MaterialButton
    private lateinit var btn_register : MaterialButton
    private lateinit var tv_forgot_password : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        et_username = findViewById(R.id.et_username) as TextInputEditText
        et_password = findViewById(R.id.et_password) as TextInputEditText
        btn_login = findViewById(R.id.btn_login) as MaterialButton
        btn_register = findViewById(R.id.btn_register) as MaterialButton
        tv_forgot_password = findViewById(R.id.tv_forgot_password) as TextView

        btn_login.setOnClickListener(this)
        btn_register.setOnClickListener(this)
        tv_forgot_password.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_register -> {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            R.id.btn_login -> {
                if (validation()) {
                    val json = JSONObject()
                    json.put("username", et_username.text.toString())
                    json.put("password", et_password.text.toString())

                    ApiService.loginApiCall().loginUser(
                        SigninRequest(
                            et_username.text.toString(),
                            et_password.text.toString()
                        )
                    ).enqueue(object : Callback<SigninResponse> {
                        override fun onResponse(
                            call: Call<SigninResponse>,
                            response: Response<SigninResponse>
                        ) {

                            Log.d("Response::::", response.body().toString())
                            if (response.body()!!.token != null){
                                finish()
                                Toast.makeText(applicationContext, "Login successful", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.putExtra(MainActivity.EXTRA_USERNAME, et_username.text.toString())
                                startActivity(intent)
                            }else{
                                Toast.makeText(applicationContext, "Incorrect username or password!", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_LONG).show()
                        }

                    })
                }
            }
        }
    }

    private fun validation(): Boolean {
        var value = true

        val password = et_password.text.toString().trim()
        val name = et_username.text.toString().trim()

        if (password.isEmpty()) {
            et_password.error = "Password required"
            et_password.requestFocus()
            value = false
        }

        if (name.isEmpty()) {
            et_username.error = "Username required"
            et_username.requestFocus()
            value = false
        }

        return value;
    }
}