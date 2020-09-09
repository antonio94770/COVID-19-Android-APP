package com.guastafeste.covid_19

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import java.io.InputStream


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var drawer: DrawerLayout? = null

    private lateinit var mAdView : AdView

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Ciclo di vita activity
        application.registerActivityLifecycleCallbacks(AppLifecycleTrackerMainActivity())


        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Prendo il DrawerLayout, e la relativa nav_view e setto il listener
        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //Toggle del simbolo in alto a sinistra del menu
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()


        //AD INIZIO CARICO IL FRAGMENT HOME
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        //ADS
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER
        adView.adUnitId = "ca-app-pub-8495213389899569/6651945471"

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        AlarmHelper.instance.setAlarm(applicationContext)
    }


    /**
     * In base all'elemento selezionato nel menu viene cambiato il fragment nel container con quello selezionato
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment(), "fragment_home").commit()
            R.id.nav_map -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, InfoFragment(), "fragment_info").commit()
            R.id.nav_search -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, RegionFragment(), "fragment_region").commit()
            R.id.nav_graphs -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GraphsFragment(), "fragment_graphs").commit()
            R.id.nav_share -> ShareStats()
        }
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Quando premo indietro gestisco la chiusura
     */
    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /**
     * Condivisione con intent esplicito degli ultimi dati nazionali.
     */
    fun ShareStats(): Unit {

        var message: String = "COVID-19 Andamento nazionale\n\n"

        //Ottengo i dati
        val helper = DatabaseHelper.getInstance(this)
        val cursor = helper.allDatabaseGlobal

        var stats = Array(cursor.count) { CoronavirusStat() }

        var i = 0

        //Scorro i dati
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                stats[i] = CoronavirusStat()
                stats[i].data = cursor.getString(0)
                stats[i].totale_casi = cursor.getString(1)
                stats[i].variazione_totale_positivi = cursor.getString(2)
                stats[i].deceduti = cursor.getString(3)
                stats[i].totale_positivi = cursor.getString(4)
                stats[i].dimessi_guariti = cursor.getString(5)
                stats[i].ricoverati_con_sintomi = cursor.getString(6)
                stats[i].terapia_intensiva = cursor.getString(7)
                stats[i].totale_ospedalizzati = cursor.getString(8)
                stats[i].isolamento_domiciliare = cursor.getString(9)
                stats[i].tamponi = cursor.getString(10)
                i++
            }


            message = message.plus("Aggiornato in data " + stats[cursor.count - 1].data.subSequence(0, stats[cursor.count - 1].data.indexOf('T')) + "\n\n")

            val temp_difference_totale_casi = stats[cursor.count - 1].totale_casi.toInt() - stats[cursor.count - 2].totale_casi.toInt()
            val temp_difference_totale_positivi = stats[cursor.count - 1].totale_positivi.toInt() - stats[cursor.count - 2].totale_positivi.toInt()
            val temp_difference_deceduti = stats[cursor.count - 1].deceduti.toInt() - stats[cursor.count - 2].deceduti.toInt()
            val temp_difference_dimessi_guariti = stats[cursor.count - 1].dimessi_guariti.toInt() - stats[cursor.count - 2].dimessi_guariti.toInt()
            val temp_ricoverati_con_sintomi = stats[cursor.count - 1].ricoverati_con_sintomi.toInt() - stats[cursor.count - 2].ricoverati_con_sintomi.toInt()
            val temp_difference_terapia_intensiva = stats[cursor.count - 1].terapia_intensiva.toInt() - stats[cursor.count - 2].terapia_intensiva.toInt()
            val temp_difference_totale_ospedalizzati = stats[cursor.count - 1].totale_ospedalizzati.toInt() - stats[cursor.count - 2].totale_ospedalizzati.toInt()
            val temp_difference_isolamento_domiciliare = stats[cursor.count - 1].isolamento_domiciliare.toInt() - stats[cursor.count - 2].isolamento_domiciliare.toInt()
            val temp_difference_tamponi = stats[cursor.count - 1].tamponi.toInt() - stats[cursor.count - 2].tamponi.toInt()

            if(temp_difference_totale_casi >= 0)
                message = message.plus("Totale casi: +" + temp_difference_totale_casi + " (" + stats[cursor.count - 1].totale_casi + ")\n")
            else
                message = message.plus("Totale casi: " + temp_difference_totale_casi + " (" + stats[cursor.count - 1].totale_casi + ")\n")

            if(temp_difference_totale_positivi >= 0)
                message = message.plus("Totale positivi: +" + temp_difference_totale_positivi + " (" + stats[cursor.count - 1].totale_positivi + ")\n")
            else
                message = message.plus("Totale positivi: " + temp_difference_totale_positivi + " (" + stats[cursor.count - 1].totale_positivi + ")\n")

            if(temp_difference_deceduti >= 0)
                message = message.plus("Deceduti: +" + temp_difference_deceduti + " (" + stats[cursor.count - 1].deceduti + ")\n")
            else
                message = message.plus("Deceduti: " + temp_difference_deceduti + " (" + stats[cursor.count - 1].deceduti + ")\n")

            if(temp_difference_dimessi_guariti >= 0)
                message = message.plus("Dimessi guariti: +" + temp_difference_dimessi_guariti + " (" + stats[cursor.count - 1].dimessi_guariti  + ")\n")
            else
                message = message.plus("Dimessi guariti: " + temp_difference_dimessi_guariti + " (" + stats[cursor.count - 1].dimessi_guariti  + ")\n")


            if(temp_ricoverati_con_sintomi >= 0)
                message = message.plus("Ricoverati con sintomi: +" + temp_ricoverati_con_sintomi + " (" + stats[cursor.count - 1].ricoverati_con_sintomi + ")\n")
            else
                message = message.plus("Ricoverati con sintomi: " + temp_ricoverati_con_sintomi + " (" + stats[cursor.count - 1].ricoverati_con_sintomi + ")\n")

            if(temp_difference_terapia_intensiva >= 0)
                message = message.plus("Terapia intensiva: +" + temp_difference_terapia_intensiva + " (" + stats[cursor.count - 1].terapia_intensiva + ")\n")
            else
                message = message.plus("Terapia intensiva: " + temp_difference_terapia_intensiva + " (" + stats[cursor.count - 1].terapia_intensiva + ")\n")

            if(temp_difference_totale_ospedalizzati >= 0)
                message = message.plus("Ospedalizzati: +" + temp_difference_totale_ospedalizzati + " (" + stats[cursor.count - 1].totale_ospedalizzati + ")\n")
            else
                message = message.plus("Ospedalizzati: " + temp_difference_totale_ospedalizzati + " (" + stats[cursor.count - 1].totale_ospedalizzati + ")\n")

            if(temp_difference_isolamento_domiciliare >= 0)
                message = message.plus("Isolamento domiciliare: +" + temp_difference_isolamento_domiciliare + " (" + stats[cursor.count - 1].isolamento_domiciliare + ")\n")
            else
                message = message.plus("Isolamento domiciliare: " + temp_difference_isolamento_domiciliare + " (" + stats[cursor.count - 1].isolamento_domiciliare + ")\n")

            if(temp_difference_tamponi >= 0)
                message = message.plus("Tamponi: +" + temp_difference_tamponi + " (" + stats[cursor.count - 1].tamponi + ")\n")
            else
                message = message.plus("Tamponi: " + temp_difference_tamponi + " (" + stats[cursor.count - 1].tamponi + ")\n")


            //Creo l'intent
            val intent:Intent = Intent().apply {
                action = Intent.ACTION_SEND

                //putExtra(Intent.EXTRA_TEXT, "$message\n\nhttps://play.google.com/store/apps/details?id=com.bigfishgames.mergetalesgoog&hl=it")
                putExtra(Intent.EXTRA_TEXT, message)
                type = "image/jpg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Condividi su"))
        }

        Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()


    }

}