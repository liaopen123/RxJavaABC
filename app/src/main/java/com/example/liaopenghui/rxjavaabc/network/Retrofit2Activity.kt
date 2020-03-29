package com.example.liaopenghui.rxjavaabc.network

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.liaopenghui.rxjavaabc.R
import com.example.liaopenghui.rxjavaabc.constants.AppServer
import com.example.liaopenghui.rxjavaabc.constants.Constants
import kotlinx.android.synthetic.main.activity_retrofit2.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class Retrofit2Activity : AppCompatActivity() {
    private lateinit var server: AppServer
    val TAG = "Retrofit2Activity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit2)
        initRtrofit()
        initClick()
    }
    private fun initRtrofit() {
        val retrofit = Retrofit.Builder().baseUrl(Constants.Host).build()
        server = retrofit.create(AppServer::class.java)
    }
    private fun initClick() {
        button.setOnClickListener {
            server.getBlog(2).enqueue(object :Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.e(TAG,"${response.body().toString()}")
                }

            })
        }

        button2.setOnClickListener {
            server.testFormUrlEncoded("lph",12).enqueue(object:Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.e(TAG,"${response.body().toString()}")
                }

            })
        }

        button3.setOnClickListener {
            val map = mapOf("username" to "廖鹏辉")
            server.testFormUrlEncoded2(map).enqueue(object:Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.e(TAG,"${response.body().toString()}")
                }

            })
        }

    }




}
