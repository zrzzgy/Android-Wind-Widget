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

        // Set up click on whole widget to open app
        val openAppIntent = Intent(context, MainActivity::class.java)
        val openAppPending = PendingIntent.getActivity(
            context, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, openAppPending)

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

        // Use goAsync() to keep the broadcast receiver alive while we do
        // async work. Without this, Android kills the process ~10s after
        // onReceive/onUpdate returns, which races with the network call.
        val pendingResult = goAsync()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                var location = LocationHelper.getCurrentLocation(context)

                // Retry once after a short delay if location was null
                if (location == null) {
                    kotlinx.coroutines.delay(2000)
                    location = LocationHelper.getCurrentLocation(context)
                }

                if (location != null) {
                    val response = WindRepository.getHourlyWind(
                        location.latitude, location.longitude
                    )
                    val prefs = PreferencesManager(context)
                    val isMph = prefs.getIsMph()

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
                    views.setTextViewText(R.id.tv_wind_speed, "location is null")
                }
            } catch (ex: Exception) {
                views.setTextViewText(R.id.tv_wind_speed, ex.message)
            } finally {
                appWidgetManager.updateAppWidget(appWidgetId, views)
                // Signal that async work is done — Android can now reclaim
                // the process. This extends the receiver lifetime to ~30s.
                pendingResult.finish()
            }
        }
    }
}
