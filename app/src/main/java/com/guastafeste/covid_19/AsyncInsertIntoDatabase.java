package com.guastafeste.covid_19;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Gestione asincrona del caricamento dei dati nel database.
 * Utilizzo di 3 costruttori per i 3 diversi tipi di dati
 */
public class AsyncInsertIntoDatabase extends AsyncTask<Void, Void, Boolean> {

    private List<String> currentList;
    private DatabaseHelper helper;
    private CoronavirusStat[] stats;
    private String tipo_di_dato_da_inserire;

    private ProvinceDataReceiver provinceDataReceiver;
    private RegionalDataReceiverTest regionDataReceiver;
    private GlobalDataReceiver globalDataReceiver;

    /**
     * Costruttore per i dati regionali
     * @param stats
     * @param ListcurrentDatabaseData
     * @param root
     * @param tipo_di_dato_da_inserire
     * @param regionDataReceiver
     */
    public AsyncInsertIntoDatabase(CoronavirusStat[] stats, List<String> ListcurrentDatabaseData, Context root, String tipo_di_dato_da_inserire, RegionalDataReceiverTest regionDataReceiver) {
        this.currentList = ListcurrentDatabaseData;
        this.helper = DatabaseHelper.getInstance(root);
        this.stats = stats;
        this.tipo_di_dato_da_inserire = tipo_di_dato_da_inserire;
        this.regionDataReceiver = regionDataReceiver;
    }

    /**
     * Costruttore per i dati relativi alle province
     * @param stats
     * @param ListcurrentDatabaseData
     * @param root
     * @param tipo_di_dato_da_inserire
     * @param provinceDataReceiver
     */
    public AsyncInsertIntoDatabase(CoronavirusStat[] stats, List<String> ListcurrentDatabaseData, Context root, String tipo_di_dato_da_inserire, ProvinceDataReceiver provinceDataReceiver) {
        this.currentList = ListcurrentDatabaseData;
        this.helper = new DatabaseHelper(root);
        this.stats = stats;
        this.tipo_di_dato_da_inserire = tipo_di_dato_da_inserire;
        this.provinceDataReceiver = provinceDataReceiver;
    }

    /**
     * Costruttore per i dati giornalieri globali
     * @param stats
     * @param ListcurrentDatabaseData
     * @param root
     * @param tipo_di_dato_da_inserire
     * @param globalDataReceiver
     */
    public AsyncInsertIntoDatabase(CoronavirusStat[] stats, List<String> ListcurrentDatabaseData, Context root, String tipo_di_dato_da_inserire, GlobalDataReceiver globalDataReceiver) {
        this.currentList = ListcurrentDatabaseData;
        this.helper = new DatabaseHelper(root);
        this.stats = stats;
        this.tipo_di_dato_da_inserire = tipo_di_dato_da_inserire;
        this.globalDataReceiver = globalDataReceiver;
    }

    /**
     * In base al parametro passato controllo che dati devo inserire e in quale tabella.
     *
     * @param voids
     * @return ritorno true se modifico la provincia, false altrimenti
     */
    @Override
    protected Boolean doInBackground(Void... voids) {

        boolean new_row_inserted = false;
        switch (tipo_di_dato_da_inserire) {

            case "regione":
                new_row_inserted = false;
                regionDataReceiver.setCanAlreadyReadFromDatabase(false);
                for (int i = 0; i < stats.length; i++) {
                    if (!currentList.contains(stats[i].getData())) {
                        helper.insertRegionalStats(stats[i]);
                        new_row_inserted = true;
                    }
                }
                //Se inserisco dei nuovi dati tengo sempre al massimo 2 set di dati di giorni diversi nel database
                if(new_row_inserted)
                    helper.deleteFromRegioni();
                break;
            case "globale":
                globalDataReceiver.setCanAlreadyReadFromDatabase(false);
                for (int i = 0; i < stats.length; i++) {
                    if (!currentList.contains(stats[i].getData())) {
                        helper.insertGlobalStats(stats[i]);
                    }
                }
                break;
            case "provincia":
                new_row_inserted = false;
                provinceDataReceiver.setCanAlreadyReadFromDatabase(false);
                for (int i = 0; i < stats.length; i++) {
                    if (!currentList.contains(stats[i].getData()) && !stats[i].getDenominazione_provincia().equals("In fase di definizione/aggiornamento")) {
                        helper.insertProvinceStats(stats[i]);
                        new_row_inserted = true;
                    }
                }
                //Se inserisco dei nuovi dati tengo sempre al massimo 2 set di dati di giorni diversi nel database
                if(new_row_inserted)
                    helper.deleteFromProvince();
                return true;
        }



        return false;
    }

    /**
     * in base al valore booleano decido se caricare i dati in caso della provincia, o se semplicemente rendere il database di nuovo libero per gli altri casi
     * @param bool valore che indica se sto agendo nel caso delle province o meno.
     */
    @Override
    protected void onPostExecute(Boolean bool)
    {
        super.onPostExecute(bool);

        if(bool)
        {
            provinceDataReceiver.setCanAlreadyReadFromDatabase(true);
            provinceDataReceiver.ReadFromDatabase();
        }
        else
        {
            if(regionDataReceiver != null)
                regionDataReceiver.setCanAlreadyReadFromDatabase(true);
            if(globalDataReceiver != null)
                globalDataReceiver.setCanAlreadyReadFromDatabase(true);
        }
    }

}
