package com.guastafeste.covid_19;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegionalDataReceiver extends AsyncTask<String, Void, String>{

    private View root;
    private RecyclerView.Adapter mAdapter;

    private CoronavirusStat[] regionalStats;
    private DatabaseHelper helper;

    private String currentData;

    private String[] currentDatabaseData;
    private List<String> ListcurrentDatabaseData;
    private ArrayList<RegionItem> regionList;



    public RegionalDataReceiver(View root, RecyclerView.Adapter adapter, ArrayList<RegionItem> list) {
        helper = new DatabaseHelper(root.getContext());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        currentData = df.format(Calendar.getInstance().getTime());

        mAdapter = adapter;
        regionList = list;
        this.root = root;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        regionList.get(1).setrDescription("Ciao bello");
        mAdapter.notifyDataSetChanged();

        if(!helper.checkDatabaseRegionalData(currentData.toString())) {
            ReadFromDatabase();
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        return ReadRegionalStatFromJson(urls);
    }

    private void publishProgress(String s)
    {
        Log.i("REGIONI: ", "PROVO A VEDERE SE ENTRA QUA");
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);

        ProcessJson(s);
    }

    private String ReadRegionalStatFromJson(String[] urls)
    {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try
        {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while(data != -1)
            {
                char current = (char) data;
                result += current;



                if(data == ']')
                    publishProgress(result);
                data = reader.read();
                Log.i("DECISIONE SERVER", result);

            }

            return result;

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private void ProcessJson(String json)
    {
        Log.i("DECISIONE SERVER", "CAZZO MA PERCHE' NON ENTRA");


        try
        {
            JSONArray arr = new JSONArray(json);
            regionalStats = new CoronavirusStat[arr.length()];

            Log.i("DECISIONE SERVER", "MA CI ENTRO QUA?");

            if(!helper.checkDatabaseRegionalData(currentData)) {

                Log.i("DECISIONE SERVER", "DEVO RISCARICARE");

                for (int i = 0; i < arr.length(); i++) {
                    regionalStats[i] = new CoronavirusStat();

                    regionalStats[i].setData(arr.getJSONObject(i).getString("data").toString());
                    regionalStats[i].setDenominazione_regione(arr.getJSONObject(i).getString("denominazione_regione").toString().replace("'", " "));
                    regionalStats[i].setDeceduti(arr.getJSONObject(i).getString("deceduti"));
                    regionalStats[i].setTotale_positivi(arr.getJSONObject(i).getString("totale_positivi"));
                    regionalStats[i].setDimessi_guariti(arr.getJSONObject(i).getString("dimessi_guariti"));
                    regionalStats[i].setTerapia_intensiva(arr.getJSONObject(i).getString("terapia_intensiva"));


                    if(ListcurrentDatabaseData != null)
                        if(!ListcurrentDatabaseData.contains(regionalStats[i].getData()))
                            helper.insertRegionalStats(regionalStats[i]);
                }

                ReadFromDatabase();

            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ReadFromDatabase() {

        /*Log.i("DECISIONE", "STO CERCANDO DI LEGGERE DAL DATABASE");

        Cursor cursor = helper.getAllDatabaseRegional();

        Log.i("DECISIONE", String.valueOf(cursor.getCount()))
        ;

        regionalStats = new CoronavirusStat[cursor.getCount()];
        currentDatabaseData = new String[cursor.getCount()];

        cursor.close();

        int i = 0;

        if (cursor.getCount() > 0) {

            for (RegionItem regione : regionList) {
                Log.i("PROVA", regione.getrTitle());
                cursor = helper.getDatabaseRegional(regione.getrTitle());
                Log.i("CONTATORE REGIONE", String.valueOf(cursor.getCount()));


                while (cursor.moveToNext()) {
                    Log.i("PROVA", cursor.getString(0));
                    regionalStats[i] = new CoronavirusStat();
                    regionalStats[i].setData(cursor.getString(0));
                    regionalStats[i].setDenominazione_regione(cursor.getString(1));
                    regionalStats[i].setDeceduti(cursor.getString(2));
                    regionalStats[i].setTotale_attualmente_positivi(cursor.getString(3));
                    regionalStats[i].setDimessi_guariti(cursor.getString(4));
                    regionalStats[i].setTerapia_intensiva(cursor.getString(5));



                    i++;
                }

                currentDatabaseData[i-1] = regionalStats[i-1].getData();
                Log.i("PROVA", currentDatabaseData[i-1].toString());

                updateItems(regione, regionalStats[i-1], regionalStats[i-1]);

                cursor.close();
            }


        }

        ListcurrentDatabaseData = Arrays.asList(currentDatabaseData);*/
    }


    private void updateItems(RegionItem regione, CoronavirusStat stat, CoronavirusStat stat_prec)
    {
        regione.setrDescription("Deceduti: " + stat.getDeceduti() + "(" + stat_prec.getDeceduti()+ ")");
        mAdapter.notifyDataSetChanged();
    }


    public CoronavirusStat[] getRegionalStats()
    {
        return regionalStats;
    }
}
