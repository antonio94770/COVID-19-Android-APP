package com.guastafeste.covid_19;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe per il processamento dei dati delle province.
 */
public class ProvinceDataReceiver extends AsyncTask<String, Void, String>{

    private Context root;
    private String nome_regione;

    private ProvinceAdapter mAdapter;

    private CoronavirusStat[] provinceStats;
    private DatabaseHelper helper;

    private TextView textViewCaricamento;
    private ImageView imageViewRegione;

    private String currentData;
    private DateFormat df;
    private String[] currentDatabaseData;
    List<String> ListcurrentDatabaseData;

    private ArrayList<ProvinceItem> provinceList;


    private boolean readFromDatabase = false;

    private static boolean canAlreadyReadFromDatabase = true;


    /**
     * Costruttore
     * @param root
     * @param adapter
     * @param list
     * @param nome_regione
     */
    public ProvinceDataReceiver(Context root, ProvinceAdapter adapter, ArrayList<ProvinceItem> list, String nome_regione) {
        helper = DatabaseHelper.getInstance(root);

        df = new SimpleDateFormat("yyyy-MM-dd'T'");
        currentData = df.format(Calendar.getInstance().getTime());
        currentData = currentData.substring(0, currentData.indexOf("T"));

        textViewCaricamento = ((ProvinceActivity) root).findViewById(R.id.textViewCaricamentoProvince);
        imageViewRegione = ((ProvinceActivity) root).findViewById(R.id.imageViewRegioneInProvince);
        imageViewRegione.setImageResource(R.drawable.flag_of_molise);


        Log.i("DATA DEL TELEFONO: ", currentData);
        this.root = root;
        this.mAdapter = adapter;
        this.provinceList = list;
        this.nome_regione = nome_regione;

        nome_regione = nome_regione.replace("-", "_");
        nome_regione = nome_regione.replace(" ", "_");
        nome_regione = nome_regione.replace(".", "_");
        nome_regione = nome_regione.replace("'", "_");

        Log.i("Nome regione", nome_regione);

        int id = ((ProvinceActivity) root).getResources().getIdentifier("com.guastafeste.covid_19:drawable/flag_of_" + nome_regione.toLowerCase(), null, null);
        imageViewRegione.setImageResource(id);
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
        if(!helper.checkDatabaseProvinceData(currentData)) {
            readFromDatabase = false;
            try {
                return ReadProvinceStatFromJson(urls);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            readFromDatabase = true;

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
    private String ReadProvinceStatFromJson(String[] urls) throws IOException {
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
        Log.i("CURRENT DATA ", currentData);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        String currentData2 = df.format(cal.getTime());

        String temp = "{\n" +"        \"data\": \"" + currentData2;

        if(json != null) {
            String json_limited = "[\n" + "    " + json.substring(json.indexOf(temp));

            Gson gson = new Gson();
            provinceStats = gson.fromJson(json_limited, CoronavirusStat[].class);

            AsyncInsertIntoDatabase task = new AsyncInsertIntoDatabase(provinceStats, ListcurrentDatabaseData, root, "provincia", this);
            task.execute();
        }
    }

    /**
     * Lettura dei dati dal database.
     */
    public void ReadFromDatabase()
    {

        Log.i("DECISIONE", "STO CERCANDO DI LEGGERE DAL DATABASE");

        Cursor cursor = helper.getDatabaseProvinceFromRegione(nome_regione);

        //Log.i("DECISIONE", String.valueOf(cursor.getCount()));

        provinceStats = new CoronavirusStat[cursor.getCount()];
        currentDatabaseData = new String[cursor.getCount()];

        int i = 0;

        if (cursor.getCount() > 0) {
            if(canAlreadyReadFromDatabase) {
                while (cursor.moveToNext()) {
                    if (!cursor.getString(2).equals("In fase di definizione/aggiornamento")) {
                        provinceStats[i] = new CoronavirusStat();
                        provinceStats[i].setData(cursor.getString(0));
                        provinceStats[i].setDenominazione_regione(cursor.getString(1));
                        provinceStats[i].setDenominazione_provincia(cursor.getString(2));
                        provinceStats[i].setTotale_casi(cursor.getString(3));


                        //Log.i("PROVINCIE", provinceStats[i].getDenominazione_provincia().toString());


                        currentDatabaseData[i] = provinceStats[i].getData();
                        i++;
                    }
                }


                ListcurrentDatabaseData = Arrays.asList(currentDatabaseData);
                updateItems();
            }
        }
        else
        {
            ListcurrentDatabaseData = Arrays.asList(currentDatabaseData);
        }


        cursor.close();
    }


    /**
     * Aggiorno i valori della recycler view.
     */
    private void updateItems()
    {
        int id;
        String nome_immagine_provincia;
        provinceList.clear();
        Set<String> uniqueData = new HashSet<String>(ListcurrentDatabaseData);
        Log.i("Grandezza numero date", String.valueOf(uniqueData.size()));
        int numero_province = provinceStats.length/uniqueData.size();
        Log.i("Grandezza numero provin", String.valueOf(numero_province));
        if(provinceStats.length > 1) {
            int i = provinceStats.length - numero_province;

            for (int j = i; j < provinceStats.length; j++) {
                int temp_difference = Integer.parseInt(provinceStats[j].getTotale_casi()) - Integer.parseInt(provinceStats[j - numero_province].getTotale_casi());

                nome_immagine_provincia = provinceStats[j].getDenominazione_provincia().toLowerCase();
                nome_immagine_provincia = nome_immagine_provincia.replace("-", "_");
                nome_immagine_provincia = nome_immagine_provincia.replace(" ", "_");
                nome_immagine_provincia = nome_immagine_provincia.replace(".", "_");
                nome_immagine_provincia = nome_immagine_provincia.replace("'", "_");
                nome_immagine_provincia = nome_immagine_provincia.replace("ì", "i");

                id = ((ProvinceActivity) root).getResources().getIdentifier("com.guastafeste.covid_19:drawable/"+ nome_immagine_provincia + "_stemma", null, null);
                provinceList.add(new ProvinceItem(id, provinceStats[j].getDenominazione_provincia(), "Nuovi casi: " + temp_difference + "\n\nTotale casi: " + provinceStats[j].getTotale_casi() + "\n\nCasi giorno precedente: "
                        + provinceStats[j - numero_province].getTotale_casi()));
            }
            mAdapter.notifyDataSetChanged();
            textViewCaricamento.setText("AGGIORNATO IN DATA: " + provinceStats[provinceStats.length-1].getData().substring(0, provinceStats[provinceStats.length-1].getData().indexOf("T")));

            /*RecyclerView recycle =(RecyclerView)((ProvinceActivity) root).findViewById(R.id.recyclerView_provincie);
            recycle.addItemDecoration(new DividerItemDecoration(root, 0));*/

            //A fine modifica imposto a false il refresh dello swipeRefreshLayout.
            SwipeRefreshLayout swipeRefreshLayout = ((ProvinceActivity) root).findViewById(R.id.swipeRefreshLayoutProvince);
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
