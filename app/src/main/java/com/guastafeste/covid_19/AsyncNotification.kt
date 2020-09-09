package com.guastafeste.covid_19

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL


/**
 * Gestione asincrona della notifica
 */
internal class AsyncNotification(val context: Context) : AsyncTask<String?, Void?, String?>() {
    private var globalStats: Array<CoronavirusStat>? = null


    /**
     * Chiamo la funzione che prova a leggere i dati odierni
     */
    override fun doInBackground(vararg params: String?): String? {
        try {
            return ReadJson(params[0])
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Dopo aver provato a leggere faccio il parse dei dati ottenuti dal file json
     */
    override fun onPostExecute(url: String?) {
        super.onPostExecute(url)

        ProcessJson(url)
    }

    /**
     * Lettura di un file json da url
     */
    private fun ReadJson(json: String?): String?
    {
        var reader: BufferedReader? = null

        try {
            val url = URL(json)
            reader = BufferedReader(InputStreamReader(url.openStream()))
            val buffer = StringBuffer()
            var read: Int
            val chars = CharArray(1024)
            while (reader.read(chars).also { read = it } != -1) {
                buffer.append(chars, 0, read)
            }

            return buffer.toString()


        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            reader?.close()
        }

        return null
    }


    /**
     * Viene utilizzata la libreria GSON per deserializzare i dati
     */
    private fun ProcessJson(json: String?) {

        //Deserializzazione
        val gson = Gson()
        globalStats = gson.fromJson<Array<CoronavirusStat>>(json, Array<CoronavirusStat>::class.java)

        //Controllo dato ottenuto dal json è già nel database
        if (globalStats != null) {
            Log.i("DATI DI OGGI", globalStats!![0].data)
            CheckCurrentLastFile(globalStats!![0].data, globalStats)
        }
    }

    /**
     * Se la data del nuovo file non è presente nella lista di date all'interno del database, mando la notifica,
     * fermo l'allarme e faccio partire l'allarme per il prossimo giorno.
     */
    private fun CheckCurrentLastFile(data: String?, globalStat: Array<CoronavirusStat>?)
    {
        val helper = DatabaseHelper.getInstance(context)
        if(!helper.checkDatabaseGlobalData(data!!.substring(0, data.indexOf("T"))))
        {
            AlarmHelper.instance.ShowNotification(context, globalStat)
            AlarmHelper.instance.stopAlarm()
            AlarmHelper.instance.repeatAlarm(context, 1)
        }

    }
}