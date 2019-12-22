package ru.leoltron.onmeeting

import android.app.Application
import android.content.Context
import net.danlew.android.joda.JodaTimeAndroid

class OnMeetingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        JodaTimeAndroid.init(this)
    }

    companion object {

        var appContext: Context? = null
            private set
    }
}
