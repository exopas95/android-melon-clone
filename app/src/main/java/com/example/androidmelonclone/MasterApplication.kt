package com.example.androidmelonclone

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MasterApplication : Application() {
    lateinit var service: RetrofitService

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        createRetrofit()
    }

    private fun createRetrofit() {
        val header = Interceptor {
            val original = it.request()
            if (checkIsLogin() and (getUserToken() != null)) {
                val request = original
                    .newBuilder()
                    .header("Authorization", "token " + getUserToken()!!)
                    .build()
                it.proceed(request)
            } else {
                it.proceed(original)
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(header)
            .addNetworkInterceptor(StethoInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        service = retrofit.create(RetrofitService::class.java)
    }

    private fun checkIsLogin(): Boolean {
        val sharedPreference = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("login_sp", "null")
        return token != "null"
    }

    private fun getUserToken(): String? {
        val sharedPreference = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sharedPreference.getString("login_sp", "null")
        return if (token == "null") null else token
    }
}
