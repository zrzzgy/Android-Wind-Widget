package com.example.windwidget.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WindApi {
    @GET("v1/forecast")
    suspend fun getWind(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "wind_speed_10m",
        @Query("forecast_days") forecastDays: Int = 1
    ): WindResponse
}
