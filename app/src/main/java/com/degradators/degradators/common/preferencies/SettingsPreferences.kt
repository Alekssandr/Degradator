package com.degradators.degradators.common.preferencies

import android.content.SharedPreferences

class SettingsPreferences(sharedPreferences: SharedPreferences) {

    var clientId: String by StringPreference(sharedPreferences, CLIENT_ID)
    var userId: String by StringPreference(sharedPreferences, USER_ID)
    var token: String by StringPreference(sharedPreferences, TOKEN)
    var isFirstOpen: Boolean by BooleanPreference(sharedPreferences, FIRST_OPEN)

    private companion object Key {
        const val CLIENT_ID = "CLIENT_ID"
        const val USER_ID = "USER_ID"
        const val TOKEN = "TOKEN"
        const val FIRST_OPEN = "FIRST_OPEN"
    }
}