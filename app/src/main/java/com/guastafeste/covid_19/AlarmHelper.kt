package com.guastafeste.covid_19

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

/**
 * Classe che gestisce le notifiche e l'alarmManager per la gestione dei tempi di invio delle notifiche
 */
class AlarmHelper {



    private object HOLDER {
        val INSTANCE = AlarmHelper()
    }

    companion object {
        val instance: AlarmHelper by lazy { HOLDER.INSTANCE }
    }

    var context: Context? = null


    /**
     * Setto l'allarme che inizia alle 18 e ripete con un intervallo di ogni ora
     */
    fun setAlarm(context: Context) {

        val alarmUp = PendingIntent.getBroadcast(
                context,
                100,
                Intent(context, Notification_receiver::class.java),
                PendingIntent.FLAG_NO_CREATE) != null

        if(!alarmUp) {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()

            cal.set(Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR)
            cal.set(Calendar.HOUR_OF_DAY, 18)
            cal.set(Calendar.MINUTE, 0)

            try {
                Log.i("MA QUA ENTRO?", "PROVA")
                val someIntent = Intent(context, Notification_receiver::class.java)

                var pendingIntent = PendingIntent.getBroadcast(
                        context,
                        100,
                        someIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                var alarms = context.getSystemService(
                        Context.ALARM_SERVICE) as AlarmManager
                alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_HOUR, pendingIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else
            Log.i("ALARMS", "STA GIA ANDANDO E NON LO RICREO")
    }

    /**
     * Funzione che ripete l'allarme il giorno dopo e che viene chiamata dopo aver inviata la notifica
     */
    fun repeatAlarm(context: Context, addDays: Int) {

        val alarmUp = PendingIntent.getBroadcast(
                context,
                100,
                Intent(context, Notification_receiver::class.java),
                PendingIntent.FLAG_NO_CREATE) != null

        if(!alarmUp) {

            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()

            if (addDays > 0)
                cal.add(Calendar.DAY_OF_YEAR, 1)

            cal.set(Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR)
            cal.set(Calendar.HOUR_OF_DAY, 18)
            cal.set(Calendar.MINUTE, 0)

            try {
                val someIntent = Intent(context, Notification_receiver::class.java)
                var pendingIntent = PendingIntent.getBroadcast(
                        context,
                        100,
                        someIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
                var alarms = context.getSystemService(
                        Context.ALARM_SERVICE) as AlarmManager
                        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, 1000 * 60 * 10 , pendingIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Funzione per stoppare l'allarme dato l'intent
     */
    fun stopAlarm() {
        if(this.context != null) {
            Log.i("STOP TIMER", "niente più timer")
            val someIntent = Intent(this.context, Notification_receiver::class.java) // intent to be launched
            // note this could be getActivity if you want to launch an activity
            var pendingIntent = PendingIntent.getBroadcast(
                    context,
                    100,
                    someIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            var alarms = this.context!!.getSystemService(
                    Context.ALARM_SERVICE) as AlarmManager
            alarms.cancel(pendingIntent)
        }

    }

    /**
     * Funzione che gestisce la creazione di una notifica su Channel 1 in caso di una build da Oreo in avanti
     */
    fun ShowNotification(context: Context, globalStat: Array<CoronavirusStat>?)
    {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val repeating_intent = Intent(context, MainActivity::class.java)

        //Tutte le activity on top verranno chiuse e questo intent sarà in top
        repeating_intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        //Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
        val pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel 1"
            val descriptionText = "Importanza alta"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel_1_ID", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "channel_1_ID")
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle("COVID-19 Italia")
                .setContentText("Nuovi dati comunicati dalla protezione civile.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.InboxStyle()
                        .addLine("Totali casi: " + globalStat!![0].totale_casi)
                        .addLine("Totale positivi: " + globalStat[0].totale_positivi)
                        .addLine("Deceduti: " + globalStat[0].deceduti)
                        .addLine("Dimessi Guariti: " + globalStat[0].dimessi_guariti)
                        .addLine("Terapia intensiva: " + globalStat[0].terapia_intensiva)
                        .addLine("Ospedalizzati: " + globalStat[0].totale_ospedalizzati)
                        .addLine("Isolamento domiciliare: " + globalStat[0].isolamento_domiciliare)
                        .addLine("Tamponi: " + globalStat[0].tamponi)
                        .setBigContentTitle("Dati aggiornati in data: " + globalStat[0].data.substring(0, globalStat[0].data.indexOf("T"))))
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.parseColor("#6200EE"))
                .setAutoCancel(true)

        notificationManager.notify(1, builder.build())

    }
}