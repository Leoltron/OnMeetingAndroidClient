package ru.leoltron.onmeeting

import android.app.Application
import android.content.Context

class OnMeetingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {

        var appContext: Context? = null
            private set
    }
}
