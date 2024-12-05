package com.example.eventsproject.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private const val PREF_NAME = "events_project_prefs"
    private const val KEY_LOGIN_RESPONSE = "login_response"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginResponse(context: Context, response: String) {
        getPreferences(context).edit().putString(KEY_LOGIN_RESPONSE, response).apply()
    }

    fun getLoginResponse(context: Context): String? {
        return getPreferences(context).getString(KEY_LOGIN_RESPONSE, null)
    }

    fun clearLoginResponse(context: Context) {
        getPreferences(context).edit().remove(KEY_LOGIN_RESPONSE).apply()
    }
}
