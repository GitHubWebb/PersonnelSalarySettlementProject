package com.personal.salary.kotlin.db.dao

import com.hjq.gson.factory.GsonFactory
import com.personal.salary.kotlin.model.weather.Place
import com.tencent.mmkv.MMKV

object PlaceDao {

    fun savePlace(place: Place) {
        getMMKV().putString("place", GsonFactory.getSingletonGson().toJson(place))
    }

    fun getSavedPlace(): Place {
        val placeJson = getMMKV().getString("place", "")
        return GsonFactory.getSingletonGson().fromJson(placeJson, Place::class.java)
    }

    fun isSaved() = getMMKV().containsKey("place")

    private fun getMMKV(): MMKV =
        MMKV.defaultMMKV(MMKV.SINGLE_PROCESS_MODE, "sunny_weather")
}