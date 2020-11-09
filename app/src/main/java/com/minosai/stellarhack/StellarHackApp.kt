package com.minosai.stellarhack

import android.app.Application
import com.minosai.stellarhack.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StellarHackApp : Application() {

    companion object {
        lateinit var instance: StellarHackApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidContext(this@StellarHackApp)
            modules(appComponent)
        }
    }

}