package com.alunando.morando

import android.app.Application
import com.alunando.morando.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application class do app Morando
 */
class MorandoApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // Inicializa Koin
        startKoin {
            androidLogger()
            androidContext(this@MorandoApplication)
            modules(appModule)
        }
    }
}

