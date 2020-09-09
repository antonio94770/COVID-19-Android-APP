package com.guastafeste.covid_19

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


/**
 * Classe che viene richiamata dall'intent dell'alarm.
 */
class Notification_receiver : BroadcastReceiver() {

    private val url_global_stat_today = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale-latest.json"

    override fun onReceive(context: Context, intent: Intent) {

        //Viene chiamata quando avviene il boot del telefono.
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            AlarmHelper.instance.setAlarm(context)
        }


        //Chiamo il task asincrono che cerca se ci sono nuovi dati e in caso positivo manda una notifica.
        val notification_task = AsyncNotification(context)
        notification_task.execute(url_global_stat_today)

    }

}
