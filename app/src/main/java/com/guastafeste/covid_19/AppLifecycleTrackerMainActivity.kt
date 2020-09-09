package com.guastafeste.covid_19

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * Classe che gestisce il ciclo di vita della MainActivity.
 */
class AppLifecycleTrackerMainActivity :  Application.ActivityLifecycleCallbacks {

    /**
    contatore che mi permette di capire se ho appena aperto l'app o se sto tornando dalla lista di app aperte
     */
    private var numStarted = 0


    override fun onActivityPaused(activity: Activity) {
        Log.i("PAUSED", "onActivityPaused");
    }

    override fun onActivityStarted(activity: Activity?) {
        /**
        Se numStarted == 0 vuol dire che sono tornato dalla lista di app aperte.
        In base al fragment in cui mi trovo aggiorno i dati.
        Infine incremento il valore di numStarted.
         */
        if (numStarted == 0) {


            if(activity?.javaClass?.simpleName.equals("MainActivity")) {
                val myActivity: MainActivity = activity as MainActivity

                val fragmentManager = myActivity.supportFragmentManager

                if (fragmentManager.fragments.size >= 1) {

                    val f: Fragment? = fragmentManager.findFragmentById(R.id.fragment_container)
                    if (f is HomeFragment) {
                        f.RefreshList()
                    } else if (f is GraphsFragment) {
                        f.RefreshList()
                    } else if (f is RegionFragment) {
                        f.RefreshList()
                    }
                }
            }
            Log.i("BACKGROUND", "ok tornato");
        }
        numStarted++
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.i("DESTROYED", "onActivityDestroyed");
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Log.i("SaveIstance", "onActivitySaveInstanceState");
    }

    /**
     * Controllo se numStarted == 0 significa che sono tornato dalla lista di app aperte.
     * Infine decremento il valore di numStarted.
     */
    override fun onActivityStopped(activity: Activity?) {
        if(activity?.javaClass?.simpleName.equals("MainActivity")) {
            numStarted--
            if (numStarted == 0) {
                Log.i("BACKGROUND", "ok background");
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.i("Activity created", "onActivityCreated");
    }

    override fun onActivityResumed(activity: Activity) {
        Log.i("Activity resumed", "onActivityResumed");
    }

}