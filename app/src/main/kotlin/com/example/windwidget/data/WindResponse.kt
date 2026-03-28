package com.example.windwidget.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class WindResponse(
    val latitude: Double,
    val longitude: Double,
    val hourly: HourlyData
)

@JsonClass(generateAdapter = false)
data class HourlyData(
    val time: List<String>,
    @Json(name = "wind_speed_10m")
    val windSpeed: List<Double>
)
