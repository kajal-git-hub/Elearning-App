package com.student.competishun.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.student.competishun.gatekeeper.type.UpdateUserInput
import com.student.competishun.ui.adapter.createGson

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "Competishun_Prefs", Context.MODE_PRIVATE
    )

    private val gson : Gson = createGson()

    companion object {
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_UPDATE_USER_INPUT = "updateUserInput"

    }

    var accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, value).apply()

            Log.e("sharedPreferences token", accessToken.toString())
        }

    var refreshToken: String?
        get() = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        set(value) {
            sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, value).apply()
        }
    var updateUserInput: UpdateUserInput?
        get() {
            val json = sharedPreferences.getString(KEY_UPDATE_USER_INPUT, null)
            return if (json != null) gson.fromJson(json, UpdateUserInput::class.java) else null
        }
        set(value) {
            val json = gson.toJson(value)
            sharedPreferences.edit().putString(KEY_UPDATE_USER_INPUT, json).apply()
        }
    fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }
}
