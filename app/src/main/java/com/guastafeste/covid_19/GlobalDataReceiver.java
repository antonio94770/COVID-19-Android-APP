package com.guastafeste.covid_19;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Classe per il processamento dei dati globali giornalieri.
 */
public class GlobalDataReceiver extends AsyncTask<String, Void, String>{

    private View root;
    private TextView data_aggiornamento;
    private TextView totale_casi;
    private TextView titolo_totale_casi;
    private TextView deceduti;
    private TextView titolo_deceduti;
    private TextView totale_attualmente_positivi;
    private TextView titolo_totale_attualmente_positivi;
    private TextView dimessi_guariti;
    private TextView titolo_dimessi_guariti;
    private TextView ricoverati_con_sintomi;
    private TextView titolo_ricoverati_con_sintomi;
    private TextView terapia_intensiva;
    private TextView titolo_terapia_intensiva;
    private TextView totale_ospedalizzati;
    private TextView titolo_ospedalizzati;
    private TextView isolamento_domiciliare;
    private TextView titolo_isolamento_domiciliare;
    private TextView tamponi;
    private TextView titolo_tamponi;

    private SwipeRefreshLayout swipeRefreshLayout;


    private CoronavirusStat[] globalStats;
    private DatabaseHelper helper;

    private String currentData;

    private String[] currentDatabaseData;
    private List<String> ListcurrentDatabaseData;

    private boolean updateText;

    private boolean readFromDatabase = false;

    private static boolean canAlreadyReadFromDatabase = true;


    /**
     * Costruttore a cui viene passata la view per ottenere il context e un booleano per differenziare la pagina iniziale
     * con quella dei grafici.
     * @param root
     * @param updateText
     */
    public GlobalDataReceiver(View root, boolean updateText) {
        helper = DatabaseHelper.getInstance(root.getContext());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        currentData = df.format(Calendar.getInstance().getTime());

        currentData = currentData.substring(0, currentData.indexOf("T"));

        this.updateText = updateText;
        this.root = root;
    }

    /**
     * Inizializzazione delle variabili che fanno riferimento alle TextView e caricamento dei dati dal Database.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(updateText) {
            data_aggiornamento = (TextView) root.findViewById(R.id.textView_data_aggioramento_globale);

            titolo_totale_casi = (TextView) root.findViewById(R.id.textView_testo_totale_casi_globale);
            totale_casi = (TextView) root.findViewById(R.id.textView_totale_casi_globale);

            deceduti = (TextView) root.findViewById(R.id.textView_deceduti_globale);
            titolo_deceduti = (TextView) root.findViewById(R.id.textView_testo_deceduti_globale);

            totale_attualmente_positivi = (TextView) root.findViewById(R.id.textView_totale_attualmente_positivi_globale);
            titolo_totale_attualmente_positivi = (TextView) root.findViewById(R.id.textView_testo_totale_attualmente_positivi_globale);

            dimessi_guariti = (TextView) root.findViewById(R.id.textView_dimessi_guariti_globale);
            titolo_dimessi_guariti = (TextView) root.findViewById(R.id.textView_testo_dimessi_guariti_globale);

            ricoverati_con_sintomi = (TextView) root.findViewById(R.id.textView_ricoverati_con_sintomi_globale);
            titolo_ricoverati_con_sintomi = (TextView) root.findViewById(R.id.textView_testo_ricoverati_con_sintomi_globale);

            terapia_intensiva = (TextView) root.findViewById(R.id.textView_terapia_intensiva_globale);
            titolo_terapia_intensiva = (TextView) root.findViewById(R.id.textView_testo_terapia_intensiva_globale);

            totale_ospedalizzati = (TextView) root.findViewById(R.id.textView_totale_ospedalizzati);
            titolo_ospedalizzati = (TextView) root.findViewById(R.id.textView_testo_totale_ospedalizzati);

            isolamento_domiciliare = (TextView) root.findViewById(R.id.textView_isolamento_domiciliare);
            titolo_isolamento_domiciliare = (TextView) root.findViewById(R.id.textView_testo_isolamento_domiciliare);

            tamponi = (TextView) root.findViewById(R.id.textView_tamponi);
            titolo_tamponi = (TextView) root.findViewById(R.id.textView_testo_tamponi);


        }

        ReadFromDatabase();
    }

    /**
     * Se la data odierna non è presente nel database chiamo la funzione per la lettura del json.
     * @param urls
     * @return
     */
    @Override
    protected String doInBackground(String... urls) {
        Log.i("DATA", currentData);
        if(!helper.checkDatabaseGlobalData(currentData)) {
            readFromDatabase = false;
            try {
                return ReadGlobalStatFromJson(urls);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            readFromDatabase = true;

        return null;
    }

    /**
     * Se non devo leggere dal database, passo al processamento del json.
     * @param s
     */
    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);


        if(!readFromDatabase) {
            ProcessJson(s);
        }
    }

    /**
     * Lettura di un file json da url
     * @param urls
     * @return
     * @throws IOException
     */
    private String ReadGlobalStatFromJson(String[] urls) throws IOException {
        BufferedReader reader = null;

        try {
            URL url = new URL(urls[0]);

            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                reader.close();
        }

        return null;
    }

    /**
     * Viene utilizzata la libreria GSON per deserializzare i dati, e dopodichè viene chiamato il task asincrono per il caricamento dei dati all'interno del database.
     * @param json
     */
    private void ProcessJson(String json)
    {

        canAlreadyReadFromDatabase = false;

        Log.i("DECISIONE", "DEVO SCARICARE");
        Gson gson = new Gson();
        globalStats = gson.fromJson(json, CoronavirusStat[].class);


        if(globalStats != null) {
            AsyncInsertIntoDatabase task = new AsyncInsertIntoDatabase(globalStats, ListcurrentDatabaseData, root.getContext(), "globale", this);
            task.execute();
        }

        updateTextView();
    }


    /**
     * Lettura dei dati dal database.
     */
    public void ReadFromDatabase() {

        Log.i("DECISIONE", "STO CERCANDO DI LEGGERE DAL SERVER");
        Cursor cursor = helper.getAllDatabaseGlobal();

        globalStats = new CoronavirusStat[cursor.getCount()];
        currentDatabaseData = new String[cursor.getCount()];

        int i = 0;

        if(canAlreadyReadFromDatabase) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    globalStats[i] = new CoronavirusStat();
                    globalStats[i].setData(cursor.getString(0));
                    globalStats[i].setTotale_casi(cursor.getString(1));
                    globalStats[i].setVariazione_totale_positivi(cursor.getString(2));
                    globalStats[i].setDeceduti(cursor.getString(3));
                    globalStats[i].setTotale_positivi(cursor.getString(4));
                    globalStats[i].setDimessi_guariti(cursor.getString(5));
                    globalStats[i].setRicoverati_con_sintomi(cursor.getString(6));
                    globalStats[i].setTerapia_intensiva(cursor.getString(7));
                    globalStats[i].setTotale_ospedalizzati(cursor.getString(8));
                    globalStats[i].setIsolamento_domiciliare(cursor.getString(9));
                    globalStats[i].setTamponi(cursor.getString(10));

                    currentDatabaseData[i] = globalStats[i].getData();
                    i++;
                }

                updateTextView();
            }
        }

        cursor.close();

        ListcurrentDatabaseData = Arrays.asList(currentDatabaseData);
    }


    /**
     * Aggiorno le TextView o i grafici in base alla variabile booleana "updateText"
     */
    private void updateTextView()
    {
        int temp_difference_totale_casi = 0;
        int temp_difference_deceduti = 0;
        int temp_difference_attualmente_positivi = 0;
        int temp_difference_dimessi_guariti = 0;
        int temp_difference_ricoverati_con_sintomi = 0;
        int temp_difference_terapia_intensiva = 0;
        int temp_difference_ospedalizzati = 0;
        int temp_difference_isolamento_domiciliare = 0;
        int temp_difference_tamponi = 0;


        if(globalStats != null) {

            if (updateText) {
                data_aggiornamento.setText("AGGIORNATO IN DATA: " + globalStats[globalStats.length - 1].getData().substring(0, globalStats[globalStats.length - 1].getData().indexOf("T")));

                temp_difference_totale_casi = Integer.parseInt(globalStats[globalStats.length - 1].getTotale_casi()) - Integer.parseInt(globalStats[globalStats.length - 2].getTotale_casi());
                if (temp_difference_totale_casi > 0) {
                    totale_casi.setText("+" + Integer.toString(temp_difference_totale_casi));
                } else {
                    totale_casi.setText(Integer.toString(temp_difference_totale_casi));
                }
                titolo_totale_casi.setText("TOTALE CASI" + "\n(" + globalStats[globalStats.length - 1].getTotale_casi() + ")");


                temp_difference_deceduti = Integer.parseInt(globalStats[globalStats.length - 1].getDeceduti()) - Integer.parseInt(globalStats[globalStats.length - 2].getDeceduti());
                if (temp_difference_deceduti > 0) {
                    deceduti.setText("+" + Integer.toString(temp_difference_deceduti));
                } else {
                    deceduti.setText(Integer.toString(temp_difference_deceduti));
                }
                titolo_deceduti.setText("DECEDUTI" + "\n(" + globalStats[globalStats.length - 1].getDeceduti() + ")");

                temp_difference_attualmente_positivi = Integer.parseInt(globalStats[globalStats.length - 1].getTotale_positivi()) - Integer.parseInt(globalStats[globalStats.length - 2].getTotale_positivi());
                if (temp_difference_attualmente_positivi > 0) {
                    totale_attualmente_positivi.setText("+" + Integer.toString(temp_difference_attualmente_positivi));
                } else {
                    totale_attualmente_positivi.setText(Integer.toString(temp_difference_attualmente_positivi));
                }
                titolo_totale_attualmente_positivi.setText("TOTALE POSITIVI" + "\n(" + globalStats[globalStats.length - 1].getTotale_positivi() + ")");


                temp_difference_dimessi_guariti = Integer.parseInt(globalStats[globalStats.length - 1].getDimessi_guariti()) - Integer.parseInt(globalStats[globalStats.length - 2].getDimessi_guariti());
                if (temp_difference_dimessi_guariti > 0) {
                    dimessi_guariti.setText("+" + Integer.toString(temp_difference_dimessi_guariti));
                } else {
                    dimessi_guariti.setText(Integer.toString(temp_difference_dimessi_guariti));

                }
                titolo_dimessi_guariti.setText("DIMESSI GUARITI" + "\n(" + globalStats[globalStats.length - 1].getDimessi_guariti() + ")");


                temp_difference_ricoverati_con_sintomi = Integer.parseInt(globalStats[globalStats.length - 1].getRicoverati_con_sintomi()) - Integer.parseInt(globalStats[globalStats.length - 2].getRicoverati_con_sintomi());
                if (temp_difference_ricoverati_con_sintomi > 0) {
                    ricoverati_con_sintomi.setText("+" + Integer.toString(temp_difference_ricoverati_con_sintomi));
                } else {
                    ricoverati_con_sintomi.setText(Integer.toString(temp_difference_ricoverati_con_sintomi));
                }
                titolo_ricoverati_con_sintomi.setText("RICOVERATI CON SINTOMI" + "\n(" + globalStats[globalStats.length - 1].getRicoverati_con_sintomi() + ")");


                temp_difference_terapia_intensiva = Integer.parseInt(globalStats[globalStats.length - 1].getTerapia_intensiva()) - Integer.parseInt(globalStats[globalStats.length - 2].getTerapia_intensiva());
                if (temp_difference_terapia_intensiva > 0) {
                    terapia_intensiva.setText("+" + Integer.toString(temp_difference_terapia_intensiva));
                } else {
                    terapia_intensiva.setText(Integer.toString(temp_difference_terapia_intensiva));
                }
                titolo_terapia_intensiva.setText("TERAPIA INTENSIVA" + "\n(" + globalStats[globalStats.length - 1].getTerapia_intensiva() + ")");


                temp_difference_ospedalizzati = Integer.parseInt(globalStats[globalStats.length - 1].getTotale_ospedalizzati()) - Integer.parseInt(globalStats[globalStats.length - 2].getTotale_ospedalizzati());
                if (temp_difference_ospedalizzati > 0) {
                    totale_ospedalizzati.setText("+" + Integer.toString(temp_difference_ospedalizzati));
                } else {
                    totale_ospedalizzati.setText(Integer.toString(temp_difference_ospedalizzati));
                }
                titolo_ospedalizzati.setText("OSPEDALIZZATI" + "\n(" + globalStats[globalStats.length - 1].getTotale_ospedalizzati() + ")");


                temp_difference_isolamento_domiciliare = Integer.parseInt(globalStats[globalStats.length - 1].getIsolamento_domiciliare()) - Integer.parseInt(globalStats[globalStats.length - 2].getIsolamento_domiciliare());
                if (temp_difference_isolamento_domiciliare > 0) {
                    isolamento_domiciliare.setText("+" + Integer.toString(temp_difference_isolamento_domiciliare));
                } else {
                    isolamento_domiciliare.setText(Integer.toString(temp_difference_isolamento_domiciliare));
                }
                titolo_isolamento_domiciliare.setText("ISOLAMENTO DOMICILIARE" + "\n(" + globalStats[globalStats.length - 1].getIsolamento_domiciliare() + ")");


                temp_difference_tamponi = Integer.parseInt(globalStats[globalStats.length - 1].getTamponi()) - Integer.parseInt(globalStats[globalStats.length - 2].getTamponi());
                if (temp_difference_tamponi > 0) {
                    tamponi.setText("+" + Integer.toString(temp_difference_tamponi));
                } else {
                    tamponi.setText(Integer.toString(temp_difference_tamponi));
                }
                titolo_tamponi.setText("TAMPONI" + "\n(" + globalStats[globalStats.length - 1].getTamponi() + ")");





                swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayoutGlobale);
            } else {
                GraphsFragment.LineChartNuoviPositivi(globalStats, root);
                GraphsFragment.CreatePieChartMortiGuaritiTE(globalStats);
                GraphsFragment.LineChartAndamentoNazionale(globalStats, root);

                TextView textViewCaricamento = (TextView) root.findViewById(R.id.textViewCaricamentoGrafici);
                textViewCaricamento.setText("AGGIORNATO IN DATA: " + globalStats[globalStats.length - 1].getData().substring(0, globalStats[globalStats.length - 1].getData().indexOf("T")));


                swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayoutGraphs);
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * Funzione per settare la variabile che definisce se posso già leggere dal database o se è ancora occupato.
     * @param bool
     */
    public void setCanAlreadyReadFromDatabase(boolean bool)
    {
        canAlreadyReadFromDatabase = bool;
    }

}
