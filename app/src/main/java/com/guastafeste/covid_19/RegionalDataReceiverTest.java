package com.guastafeste.covid_19;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Classe per il processamento dei dati delle province.
 */
public class RegionalDataReceiverTest extends AsyncTask<String, Void, String>{

    private View root;
    private RecyclerView.Adapter mAdapter;

    private CoronavirusStat[] regionalStats;
    private DatabaseHelper helper;

    private DateFormat df;

    private String currentData;

    private String[] currentDatabaseData;
    List<String> ListcurrentDatabaseData;
    private ArrayList<RegionItem> regionList;

    private boolean readFromDatabase = false;

    private TextView textViewCaricamento;

    private static boolean canAlreadyReadFromDatabase = true;


    /**
     * Costruttore
     * @param root
     * @param adapter
     * @param list
     */
    public RegionalDataReceiverTest(View root, RecyclerView.Adapter adapter, ArrayList<RegionItem> list) {
        helper = DatabaseHelper.getInstance(root.getContext());

        df = new SimpleDateFormat("yyyy-MM-dd'T'");
        currentData = df.format(Calendar.getInstance().getTime());
        currentData = currentData.substring(0, currentData.indexOf("T"));


        Log.i("DATA DEL TELEFONO: ", currentData);
        textViewCaricamento =  root.findViewById(R.id.textViewCaricamentoRegioni);

        mAdapter = adapter;
        regionList = list;
        this.root = root;
    }

    /**
     * Caricamento dei dati dal Database.
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ReadFromDatabase();
    }


    /**
     * Se la data odierna non è presente nel database chiamo la funzione per la lettura del json.
     * @param urls
     * @return
     */
    @Override
    protected String doInBackground(String... urls) {
        if(!helper.checkDatabaseRegionalData(currentData)) {
            readFromDatabase = false;
            try {
                return ReadRegionalStatFromJson(urls);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else readFromDatabase = true;

        return null;
    }

    /**
     * Se non devo leggere dal database passo al processamento del json.
     * @param s
     */
    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);

        if(!readFromDatabase)
            ProcessJson(s);
    }

    /**
     * Lettura di un file json da url
     * @param urls
     * @return
     * @throws IOException
     */
    private String ReadRegionalStatFromJson(String[] urls) throws IOException {
        BufferedReader reader = null;

        try {
            URL url = new URL(urls[0]);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

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

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        String currentData2 = df.format(cal.getTime());

        String temp = "{\n" +"        \"data\": \"" + currentData2;

        if(json != null) {
            String json_limited = "[\n" + "    " + json.substring(json.indexOf(temp));

            Log.i("DECISIONE", "DEVO SCARICARE");
            Gson gson = new Gson();
            regionalStats = gson.fromJson(json_limited, CoronavirusStat[].class);


            AsyncInsertIntoDatabase task = new AsyncInsertIntoDatabase(regionalStats, ListcurrentDatabaseData, root.getContext(), "regione", this);
            task.execute();

            updateItems();
        }
    }


    /**
     * Lettura dei dati dal database.
     */
    public void ReadFromDatabase() {

        Log.i("DECISIONE", "STO CERCANDO DI LEGGERE DAL DATABASE");

        Cursor cursor = helper.getAllDatabaseRegional();

        regionalStats = new CoronavirusStat[cursor.getCount()];
        currentDatabaseData = new String[cursor.getCount()];

        int i = 0;

        if(canAlreadyReadFromDatabase == true) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    regionalStats[i] = new CoronavirusStat();
                    regionalStats[i].setData(cursor.getString(0));
                    regionalStats[i].setDenominazione_regione(cursor.getString(1));
                    regionalStats[i].setTotale_casi(cursor.getString(2));
                    regionalStats[i].setDeceduti(cursor.getString(3));
                    regionalStats[i].setTotale_positivi(cursor.getString(4));
                    regionalStats[i].setDimessi_guariti(cursor.getString(5));
                    regionalStats[i].setRicoverati_con_sintomi(cursor.getString(6));
                    regionalStats[i].setTerapia_intensiva(cursor.getString(7));
                    regionalStats[i].setTotale_ospedalizzati(cursor.getString(8));
                    regionalStats[i].setIsolamento_domiciliare(cursor.getString(9));
                    regionalStats[i].setTamponi(cursor.getString(10));


                    currentDatabaseData[i] = regionalStats[i].getData();
                    i++;
                }
                updateItems();
            }
        }

        ListcurrentDatabaseData = Arrays.asList(currentDatabaseData);

        cursor.close();

    }

    /**
     * Calcolo e aggiorno i valori della recycler view.
     */
    private void updateItems()
    {

        int temp_difference_totale_casi = 0;
        String string_temp_difference_totale_casi;
        int temp_difference_deceduti = 0;
        String string_temp_difference_deceduti;
        int temp_difference_attualmente_positivi = 0;
        String string_temp_difference_attualmente_positivi;
        int temp_difference_dimessi_guariti = 0;
        String string_temp_difference_dimessi_guariti;
        int temp_difference_ricoverati_con_sintomi = 0;
        String string_temp_difference_ricoverati_con_sintomi;
        int temp_difference_terapia_intensiva = 0;
        String string_difference_terapia_intensiva;
        int temp_difference_ospedalizzati = 0;
        String string_difference_ospedalizzati;
        int temp_difference_isolamento_domiciliare = 0;
        String string_difference_isolamento_domiciliare;
        int temp_difference_tamponi = 0;
        String string_difference_tamponi;

        if(regionalStats.length > 1) {
            int i = regionalStats.length - 1;

            for (int j = regionList.size() - 1; j >= 0; j--) {
                temp_difference_totale_casi = Integer.parseInt(regionalStats[i].getTotale_casi()) - Integer.parseInt(regionalStats[i - regionList.size()].getTotale_casi());
                if (temp_difference_totale_casi >= 0) {
                    string_temp_difference_totale_casi = "+" + String.valueOf(temp_difference_totale_casi);
                } else {
                    string_temp_difference_totale_casi = String.valueOf(temp_difference_totale_casi);
                }

                temp_difference_deceduti = Integer.parseInt(regionalStats[i].getDeceduti()) - Integer.parseInt(regionalStats[i - regionList.size()].getDeceduti());
                if (temp_difference_deceduti >= 0) {
                    string_temp_difference_deceduti = "+" + String.valueOf(temp_difference_deceduti);
                } else {
                    string_temp_difference_deceduti = String.valueOf(temp_difference_deceduti);
                }

                temp_difference_attualmente_positivi = Integer.parseInt(regionalStats[i].getTotale_positivi()) - Integer.parseInt(regionalStats[i - regionList.size()].getTotale_positivi());
                if (temp_difference_attualmente_positivi >= 0) {
                    string_temp_difference_attualmente_positivi = "+" + String.valueOf(temp_difference_attualmente_positivi);
                } else {
                    string_temp_difference_attualmente_positivi = String.valueOf(temp_difference_attualmente_positivi);
                }

                temp_difference_dimessi_guariti = Integer.parseInt(regionalStats[i].getDimessi_guariti()) - Integer.parseInt(regionalStats[i - regionList.size()].getDimessi_guariti());
                if (temp_difference_dimessi_guariti >= 0) {
                    string_temp_difference_dimessi_guariti = "+" + String.valueOf(temp_difference_dimessi_guariti);
                } else {
                    string_temp_difference_dimessi_guariti = String.valueOf(temp_difference_dimessi_guariti);
                }

                temp_difference_ricoverati_con_sintomi = Integer.parseInt(regionalStats[i].getRicoverati_con_sintomi()) - Integer.parseInt(regionalStats[i - regionList.size()].getRicoverati_con_sintomi());
                if (temp_difference_ricoverati_con_sintomi >= 0) {
                    string_temp_difference_ricoverati_con_sintomi = "+" + String.valueOf(temp_difference_ricoverati_con_sintomi);
                } else {
                    string_temp_difference_ricoverati_con_sintomi = String.valueOf(temp_difference_ricoverati_con_sintomi);
                }

                temp_difference_terapia_intensiva = Integer.parseInt(regionalStats[i].getTerapia_intensiva()) - Integer.parseInt(regionalStats[i - regionList.size()].getTerapia_intensiva());
                if (temp_difference_terapia_intensiva >= 0) {
                    string_difference_terapia_intensiva = "+" + String.valueOf(temp_difference_terapia_intensiva);
                } else {
                    string_difference_terapia_intensiva = String.valueOf(temp_difference_terapia_intensiva);
                }

                temp_difference_ospedalizzati = Integer.parseInt(regionalStats[i].getTotale_ospedalizzati()) - Integer.parseInt(regionalStats[i - regionList.size()].getTotale_ospedalizzati());
                if (temp_difference_ospedalizzati >= 0) {
                    string_difference_ospedalizzati = "+" + String.valueOf(temp_difference_ospedalizzati);
                } else {
                    string_difference_ospedalizzati = String.valueOf(temp_difference_ospedalizzati);
                }

                temp_difference_isolamento_domiciliare = Integer.parseInt(regionalStats[i].getIsolamento_domiciliare()) - Integer.parseInt(regionalStats[i - regionList.size()].getIsolamento_domiciliare());
                if (temp_difference_isolamento_domiciliare >= 0) {
                    string_difference_isolamento_domiciliare = "+" + String.valueOf(temp_difference_isolamento_domiciliare);
                } else {
                    string_difference_isolamento_domiciliare = String.valueOf(temp_difference_isolamento_domiciliare);
                }

                temp_difference_tamponi = Integer.parseInt(regionalStats[i].getTamponi()) - Integer.parseInt(regionalStats[i - regionList.size()].getTamponi());
                if (temp_difference_tamponi >= 0) {
                    string_difference_tamponi = "+" + String.valueOf(temp_difference_tamponi);
                } else {
                    string_difference_tamponi = String.valueOf(temp_difference_tamponi);
                }

                regionList.get(j).setrDescription("Totale casi: " + string_temp_difference_totale_casi + " (" + regionalStats[i].getTotale_casi() + ")\n" +
                        "Deceduti: " + string_temp_difference_deceduti + " (" + regionalStats[i].getDeceduti() + ")\n" +
                        "Totale positivi: " + string_temp_difference_attualmente_positivi + " (" + regionalStats[i].getTotale_positivi() + ")\n" +
                        "Guariti: " + string_temp_difference_dimessi_guariti + " (" + regionalStats[i].getDimessi_guariti() + ")\n" +
                        "Terapia intensiva: " + string_difference_terapia_intensiva + " (" + regionalStats[i].getTerapia_intensiva() + ")\n" +
                        "Ospedalizzati: " + string_difference_ospedalizzati + " (" + regionalStats[i].getTotale_ospedalizzati() + ")\n" +
                        "Tamponi: " + string_difference_tamponi + " (" + regionalStats[i].getTamponi() + ")");
                i--;
            }
            mAdapter.notifyDataSetChanged();

            textViewCaricamento.setText("AGGIORNATO IN DATA: " + regionalStats[i + 1].getData().substring(0, regionalStats[i + 1].getData().indexOf("T")));
        }

        //A fine modifica imposto a false il refresh dello swipeRefreshLayout.
        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayoutRegioni);
        swipeRefreshLayout.setRefreshing(false);
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
