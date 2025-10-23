package com.alunando.morando

import android.app.Application
import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.di.appModule
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Application class do app Morando
 */
class MorandoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val authManager: AuthManager by inject()

    override fun onCreate() {
        super.onCreate()

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // Inicializa Koin
        startKoin {
            // androidLogger() removido para reduzir logs
            androidContext(this@MorandoApplication)
            modules(appModule)
        }

        // Login será feito na tela de Login, não automaticamente
        android.util.Log.d("MorandoApp", "App iniciado - flavor: ${BuildConfig.BACKEND_TYPE}")
    }
}
