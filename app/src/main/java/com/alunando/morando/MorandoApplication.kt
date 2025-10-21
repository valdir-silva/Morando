package com.alunando.morando

import android.app.Application
import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.di.appModule
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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
            androidLogger()
            androidContext(this@MorandoApplication)
            modules(appModule)
        }

        // Faz login anônimo automaticamente (apenas se não for MOCK)
        if (BuildConfig.BACKEND_TYPE != "MOCK") {
            applicationScope.launch {
                authManager.ensureAuthenticated()
            }
        }
    }
}
