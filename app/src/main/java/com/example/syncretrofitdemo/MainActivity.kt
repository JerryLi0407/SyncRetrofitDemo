package com.example.syncretrofitdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.travel.taipei/")
                .addConverterFactory(GsonConverterFactory.create()) // Gson
                .build()

            val apiService = retrofit.create(TaipeiTourApiService::class.java)
            val response = apiService.getTaipeiTourData().execute()
            if (response.isSuccessful) {
                val body = response.body()?.data
                withContext(Dispatchers.Main) {
                    val tvData: TextView = findViewById(R.id.tvData)
                    if (body != null) {
                        tvData.text = "${body.get(0).name} \n${body.get(0).introduction} \n\n${body.get(0).address} \n${body.get(0).url}"
                    } else {
                        tvData.text = "No Data"
                    }
                }
            }
        }
    }
}