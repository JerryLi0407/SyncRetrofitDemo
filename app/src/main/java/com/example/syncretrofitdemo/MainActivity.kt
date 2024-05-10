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

        // 建立 Retrofit 實例的 Builder
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.travel.taipei/")
            .addConverterFactory(GsonConverterFactory.create()) // Gson
            .build()

        // 通過 Retrofit 實例創建 API 服務接口的實現
        val apiService = retrofit.create(TaipeiTourApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                apiService.getTaipeiTourData().execute()
            } catch (e: Exception) {
                null
            }

            withContext(Dispatchers.Main) {
                val tvData: TextView = findViewById(R.id.tvData)
                if (response?.isSuccessful == true) {
                    // 獲得響應體中的數據部分
                    val body = response.body()?.data
                    if (body.isNullOrEmpty()) {
                        tvData.text = "No Data" // 若無數據，顯示“無數據”
                    } else {
                        // 若有數據，將數據顯示在 TextView 上
                        tvData.text = "${body.get(0).name} \n${body.get(0).introduction} " +
                                "\n\n${body.get(0).address} \n${body.get(0).url}"
                    }
                } else {
                    // 若 HTTP 響應失敗，顯示錯誤信息
                    tvData.text = "Error: ${response?.code()} ${response?.message()}"
                }
            }
        }
    }
}