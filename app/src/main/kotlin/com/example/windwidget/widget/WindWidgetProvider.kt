package com.example.windwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.windwidget.MainActivity
import com.example.windwidget.R
import com.example.windwidget.data.PreferencesManager
import com.example.windwidget.data.WindRepository
import com.example.windwidget.location.LocationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WindWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.example.windwidget.ACTION_REFRESH"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, WindWidgetProvider::class.java)
            )
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_wind)

        // Set up click on widget body to open app
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPending = PendingIntent.getActivity(
            context, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_content, openAppPending)

        // Set up refresh button click
        val refreshIntent = Intent(context, WindWidgetProvider::class.java).apply {
            action = ACTION_REFRESH
        }
        val refreshPending = PendingIntent.getBroadcast(
            context, 1, refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_refresh, refreshPending)

        // Show loading state
        views.setTextViewText(R.id.tv_wind_speed, "...")
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Fetch data in background
        scope.launch {
            try {
                val location = LocationHelper.getCurrentLocation(context)
                if (location != null) {
                    val response = WindRepository.getHourlyWind(
                        location.latitude, location.longitude
                    )
                    val prefs = PreferencesManager(context)
                    val isMph = prefs.getIsMph()

                    // Get current hour's wind speed
                    val currentHour = java.time.LocalTime.now().hour
                    val windSpeedKmh = response.hourly.windSpeed.getOrElse(currentHour) {
                        response.hourly.windSpeed.firstOrNull() ?: 0.0
                    }

                    val displaySpeed = if (isMph) windSpeedKmh * 0.621371 else windSpeedKmh
                    val unit = if (isMph) "mph" else "km/h"

                    views.setTextViewText(
                        R.id.tv_wind_speed,
                        String.format("%.0f", displaySpeed)
                    )
                    views.setTextViewText(R.id.tv_wind_unit, unit)
                } else {
                    views.setTextViewText(R.id.tv_wind_speed, "--")
                }
            } catch (_: Exception) {
                views.setTextViewText(R.id.tv_wind_speed, "!")
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
