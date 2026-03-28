package com.example.windwidget.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    private val isMphKey = booleanPreferencesKey("is_mph")

    val isMph: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[isMphKey] ?: false
    }

    suspend fun setUnit(isMph: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isMphKey] = isMph
        }
    }

    suspend fun getIsMph(): Boolean {
        return context.dataStore.data.first()[isMphKey] ?: false
    }
}
