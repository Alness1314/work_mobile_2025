package com.susess.cv360.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.susess.cv360.model.auth.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("session_prefs")

class SessionDataStore(private val context: Context) {
    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_SESSION = booleanPreferencesKey("session")
    }

    val sessionFlow: Flow<Session> = context.dataStore.data.map { prefs ->
        Session(
            token = prefs[KEY_TOKEN],
            username = prefs[KEY_USERNAME],
            isLoggedIn = prefs[KEY_SESSION]?: false
        )
    }

    suspend fun saveSession(session: Session) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = session.token ?: ""
            prefs[KEY_USERNAME] = session.username ?: ""
            prefs[KEY_SESSION] = session.isLoggedIn
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}