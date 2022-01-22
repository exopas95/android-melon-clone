package com.example.androidmelonclone

import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {
    @GET("/melon/list")
    fun getSongList(): Call<ArrayList<Melon>>
}