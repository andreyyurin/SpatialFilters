package ru.sad.smoothimage

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ru.sad.smoothimage.di.mainModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            loadKoinModules(
                listOf(
                    mainModule
                )
            )
        }
    }
}