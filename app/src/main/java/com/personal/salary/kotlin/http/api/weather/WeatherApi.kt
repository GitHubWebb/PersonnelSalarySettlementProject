package com.personal.salary.kotlin.http.api.weather

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.CaiYunBaseUrl
import com.personal.salary.kotlin.model.weather.DailyResponse
import com.personal.salary.kotlin.model.weather.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

@CaiYunBaseUrl
interface WeatherApi {

    @GET("v2.5/{token}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<RealtimeResponse>

    @GET("v2.5/{token}/{lng},{lat}/daily.json")
    fun getDailyWeather(
        @Path("token") token: String,
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Call<DailyResponse>

    companion object : WeatherApi by ServiceCreator.create()
}