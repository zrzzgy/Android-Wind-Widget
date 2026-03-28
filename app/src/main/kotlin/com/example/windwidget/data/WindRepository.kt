package com.example.windwidget.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object WindRepository {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val api: WindApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(WindApi::class.java)

    suspend fun getHourlyWind(latitude: Double, longitude: Double): WindResponse {
        return api.getWind(latitude = latitude, longitude = longitude)
    }
}
