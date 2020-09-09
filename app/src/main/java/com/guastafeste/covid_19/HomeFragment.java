package com.guastafeste.covid_19;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Fragment che gestisce i dati quotidiani sul Coronavirus.
 */
public class HomeFragment extends Fragment {

    private GlobalDataReceiver main_task;
    private GlobalDataReceiver refresh_task;
    private View myInflatedView;

    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Viene chiamato il task asincrono per la raccolta dei dati e viene gestista la creazione della view del fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myInflatedView = inflater.inflate(R.layout.fragment_home, container,false);

        main_task = new GlobalDataReceiver(myInflatedView, true);
        main_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");

        //Inizializzazione e inserimento della funzione RefreshList all'interno del listener.
        swipeRefreshLayout = myInflatedView.findViewById(R.id.swipeRefreshLayoutGlobale);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshList();

            }
        });

        getActivity().setTitle("Andamento Nazionale COVID-19");

        //AlarmHelper.Companion.getInstance().setAlarm(myInflatedView.getContext());


        return myInflatedView;
    }


    /**
     * Funzione per il refresh della dei dati, richiamando i task asincroni solo se il task principale è terminato, o se questo nuovo task è terminato.
     */
    public void RefreshList()
    {
        if(myInflatedView != null) {
            if (refresh_task == null) {
                refresh_task = new GlobalDataReceiver(myInflatedView, true);
                if (main_task.getStatus() == AsyncTask.Status.FINISHED) {
                    Log.i("1", "ENTRO LA PRIMA VOLTA");
                    refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");
                } else
                    swipeRefreshLayout.setRefreshing(false);
            } else if (refresh_task.getStatus() == AsyncTask.Status.FINISHED) {
                Log.i("1", "ENTRO LA SECONDA VOLTA");
                refresh_task.cancel(true);
                refresh_task = new GlobalDataReceiver(myInflatedView, true);
                refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }


}
