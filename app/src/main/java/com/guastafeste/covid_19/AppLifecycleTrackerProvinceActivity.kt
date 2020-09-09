package com.guastafeste.covid_19

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

/**
 * Classe che gestisce il ciclo di vita della ProvinceActivity.
 */
class AppLifecycleTrackerProvinceActivity : Application.ActivityLifecycleCallbacks  {
    /**
    contatore che mi permette di capire se ho appena aperto l'app o se sto tornando dalla lista di app aperte
     */
    private var numStarted = 1

    /**
    Dncremento il valore di numStarted.
     */
    override fun onActivityPaused(activity: Activity) {
        Log.i("PAUSE_PROVINCE", "onActivityPaused");
        if(activity.javaClass.simpleName == "ProvinceActivity") {
            numStarted--
        }

    }

    override fun onActivityStarted(activity: Activity?) {
        Log.i("START_PROVINCE", "onActivityStarted");

    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.i("DESTROYED_PROVINCE", "onActivityDestroyed");
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        Log.i("SaveInstance_PROVINCE", "onActivitySaveInstanceState");
    }

    override fun onActivityStopped(activity: Activity?) {
        Log.i("STOPPED_PROVINCE", "ok onActivityStopped");
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.i("CREATED_PROVINCE", "onActivityCreated");


    }

    /**
    Se numStarted == 0 vuol dire che sono tornato dalla lista di app aperte e aggiorno i dati.
    Se numStarted != 1 incremento il valore di numStarted
     */
    override fun onActivityResumed(activity: Activity) {

        if (numStarted == 0) {
            if(activity.javaClass.simpleName == "ProvinceActivity") {
                val myActivity: ProvinceActivity = activity as ProvinceActivity

                myActivity.RefreshList()
            }
            Log.i("BACKGROUND", "ok tornato");
        }
        if(numStarted != 1)
            numStarted++
    }

}