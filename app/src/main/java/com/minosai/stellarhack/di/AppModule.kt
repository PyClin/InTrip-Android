package com.minosai.stellarhack.di

import com.minosai.stellarhack.utils.Constants
import com.minosai.stellarhack.utils.events.EventManager
import com.minosai.stellarhack.utils.preference.AppPreferences
import com.minosai.stellarhack.utils.preference.customPrefs
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        customPrefs(
            androidContext(),
            Constants.PREFS
        )
    }

    single { AppPreferences(get()) }

    single { EventManager() }

}