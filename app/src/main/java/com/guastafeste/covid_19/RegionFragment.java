package com.guastafeste.covid_19;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

/**
 * Fragment che gestisce la recycler view e i dati delle regioni per il Coronavirus.
 */
public class RegionFragment extends Fragment {

    public static final String REGION_NAME_EXTRA = "com.example.guastafeste.REGION_NAME_PASSED";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RegionAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RegionalDataReceiverTest main_task;
    private RegionalDataReceiverTest refresh_task;
    private ArrayList<RegionItem> regionList;
    private View myInflatedView;

    /**
     * Viene chiamato il task asincrono per la raccolta dei dati e viene gestista la creazione della recycler view e della view del fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myInflatedView = inflater.inflate(R.layout.fragment_region, container,false);

        getActivity().setTitle("Regioni");

        //CREAZIONE ARRAY DI RegionItem.
        regionList = new ArrayList<>();
        regionList.add(new RegionItem(R.drawable.flag_of_abruzzo, "Abruzzo", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_basilicata, "Basilicata", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_p_a__bolzano, "P.A. Bolzano", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_calabria, "Calabria", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_campania, "Campania", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_emilia_romagna, "Emilia-Romagna", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_friuli_venezia_giulia, "Friuli Venezia Giulia", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_lazio, "Lazio", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_liguria, "Liguria", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_lombardia, "Lombardia", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_marche, "Marche", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_molise, "Molise", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_piemonte, "Piemonte", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_puglia, "Puglia", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_sardegna, "Sardegna", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_sicilia, "Sicilia", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_toscana, "Toscana", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_p_a__trento, "P.A. Trento", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_umbria, "Umbria", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_valle_d_aosta, "Valle d'Aosta", "Caricamento..."));
        regionList.add(new RegionItem(R.drawable.flag_of_veneto, "Veneto", "Caricamento..."));

        //SETUP RecyclerView
        mRecyclerView = myInflatedView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(myInflatedView.getContext());
        mAdapter = new RegionAdapter(regionList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        //Quando premo su una regione chiama la funzione openProvinceActivity.
        mAdapter.setOnItemClickListener(new RegionAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Log.i("HO CLICCATO: ", regionList.get(position).getrTitle());
                openProvinceActivity(regionList.get(position).getrTitle());
            }
        });

        //Creazione task asincrono.
        main_task = new RegionalDataReceiverTest(myInflatedView, mAdapter, regionList);
        main_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json");

        //Inizializzazione e inserimento della funzione RefreshList all'interno del listener.
        swipeRefreshLayout = myInflatedView.findViewById(R.id.swipeRefreshLayoutRegioni);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshList();

            }
        });

        //AlarmHelper.Companion.getInstance().setAlarm(myInflatedView.getContext());

        return myInflatedView;
    }

    /**
     * Gestione della creazione di un nuovo intent quando clicco su un elemento della recyclerView
     * @param region_name
     */
    public void openProvinceActivity(String region_name)
    {
        Intent intent = new Intent(this.getContext(), ProvinceActivity.class);
        intent.putExtra(REGION_NAME_EXTRA, region_name);
        startActivity(intent);
    }


    /**
     * Funzione per il refresh della dei dati, richiamando i task asincroni solo se il task principale è terminato, o se questo nuovo task è terminato.
     */
    public void RefreshList()
    {
        if(myInflatedView != null) {
            if (refresh_task == null) {
                refresh_task = new RegionalDataReceiverTest(myInflatedView, mAdapter, regionList);
                if (main_task.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.i("1", "ENTRO LA PRIMA VOLTA");
                    refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json");
                } else
                    swipeRefreshLayout.setRefreshing(false);
            } else if (refresh_task.getStatus() == AsyncTask.Status.FINISHED) {
                Log.i("1", "ENTRO LA SECONDA VOLTA");
                refresh_task.cancel(true);
                refresh_task = new RegionalDataReceiverTest(myInflatedView, mAdapter, regionList);
                refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json");
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
