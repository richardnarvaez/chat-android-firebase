package ec.richardnarvaez.chatf

import android.app.Application
import android.util.Log
import ec.richardnarvaez.chatf.Utils.FirebaseUtils

class App : Application(), LifeCycleDelegate {

    override fun onCreate() {
        super.onCreate()
        val lifeCycleHandler = AppLifecycleHandler(this)
        registerLifecycleHandler(lifeCycleHandler)
    }

    override fun onAppBackgrounded() {
        Log.e("CICLO", "App in background")
        FirebaseUtils.setLiveState(false)
    }

    override fun onAppForegrounded() {
        Log.e("CICLO", "App in foreground")
        FirebaseUtils.setLiveState(true)
    }

    private fun registerLifecycleHandler(lifeCycleHandler: AppLifecycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler)
        registerComponentCallbacks(lifeCycleHandler)
    }

}