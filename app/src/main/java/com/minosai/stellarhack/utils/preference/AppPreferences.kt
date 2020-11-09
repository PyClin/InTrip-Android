package com.minosai.stellarhack.utils.preference

import android.content.SharedPreferences

class AppPreferences(private val sharedPreferences: SharedPreferences) {

    private companion object Key {
        const val PREF_ACCESS_TOKEN = "ACCESS_TOKEN"
        const val PREF_REFRESH_TOKEN = "REFRESH_TOKEN"
        const val PREF_USERNAME = "USERNAME"
        const val PREF_USER_TYPE = "USER_TYPE"
        const val PREF_USER_ID = "USER_ID"
        const val PREF_WALLET_BALANCE = "WALLET_BALANCE"
    }

    var accessToken: String? by sharedPreferences.string(PREF_ACCESS_TOKEN)
    var refreshToken: String? by sharedPreferences.string(PREF_REFRESH_TOKEN)

    var username: String? by sharedPreferences.string(PREF_USERNAME)
    var userType: String? by sharedPreferences.string(PREF_USER_TYPE)
    var userId: Int by sharedPreferences.int(PREF_USER_ID)
    var walletBalance: Float by sharedPreferences.float(PREF_WALLET_BALANCE)

    fun resetCache() = sharedPreferences.edit().clear().apply()
}