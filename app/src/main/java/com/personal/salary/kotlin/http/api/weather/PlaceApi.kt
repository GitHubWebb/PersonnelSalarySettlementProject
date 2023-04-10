package com.personal.salary.kotlin.http.api.weather

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.CaiYunBaseUrl
import com.personal.salary.kotlin.model.weather.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

@CaiYunBaseUrl
interface PlaceApi {

    @GET("v2/place?lang=zh_CN")
    fun searchPlace(
        @Query("token") token: String,
        @Query("query") query: String
    ): Call<PlaceResponse>

    companion object : PlaceApi by ServiceCreator.create()
}