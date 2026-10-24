package com.example.thuchanhandroid.data.pref

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(username: String, role: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    val isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false)

    val username: String?
        get() = prefs.getString(KEY_USERNAME, null)

    val role: String?
        get() = prefs.getString(KEY_ROLE, null)

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "user_session"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
        private const val KEY_LOGGED_IN = "is_logged_in"
    }
}
