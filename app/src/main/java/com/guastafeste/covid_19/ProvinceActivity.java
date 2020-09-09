package com.guastafeste.covid_19;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

public class ProvinceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProvinceAdapter mAdapter;

    private String nomeRegione;
    private ArrayList<ProvinceItem> provinceList;

    private ProvinceDataReceiver main_task;
    private ProvinceDataReceiver refresh_task;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean whiteFlag = false;

    private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplication().registerActivityLifecycleCallbacks(new AppLifecycleTrackerProvinceActivity());


        setContentView(R.layout.activity_province);



        //Raccolgo il nome della ragione dalla main activity.
        Intent intent = getIntent();
        nomeRegione = intent.getStringExtra(RegionFragment.REGION_NAME_EXTRA);

        //Gestione della collapsingToolbar.
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_provincie);
        final CollapsingToolbarLayout collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final TextView textView = (TextView) findViewById(R.id.toolbar_title_province);

        //Differenziazione della freccia in base alla alla bandiera della regione.
        if(nomeRegione.equals("P.A. Bolzano") || nomeRegione.equals("Emilia-Romagna") || nomeRegione.equals("Marche") || nomeRegione.equals("Puglia") || nomeRegione.equals("Sardegna") || nomeRegione.equals("Toscana")) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
            whiteFlag = true;
        }
        else
        {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_);
            whiteFlag = false;
        }

        //apre il navigation drawer quando premo indietro.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Gestione del comportamento Dell'AppBarLayout in base a quando è aperta o quando è limitata.
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == toolbar.getHeight() - collapsingToolbarLayout.getHeight()) {

                    if (textView.getVisibility() != View.VISIBLE) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(nomeRegione);
                        if(whiteFlag)
                            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_);
                    }
                } else {
                    if (textView.getVisibility() != View.GONE) {
                        textView.setVisibility(View.GONE);
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_);
                        if(whiteFlag)
                            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
                    }
                }
            }
        });

        provinceList = new ArrayList<>();

        //RECYCLER VIEW
        mRecyclerView = findViewById(R.id.recyclerView_provincie);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ProvinceAdapter(provinceList);



        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        main_task = new ProvinceDataReceiver(this, mAdapter, provinceList, nomeRegione);
        main_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province.json");


        //ADS
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-8495213389899569/2994953255");

        mAdView = findViewById(R.id.adView_province);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //ALARM
        AlarmHelper.Companion.getInstance().repeatAlarm(getApplicationContext(), 1);


        //Gestione dello swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutProvince);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshList();

            }
        });

    }

    /**
     * Funzione per il refresh della dei dati, richiamando i task asincroni solo se il task principale è terminato, o se questo nuovo task è terminato.
     */
    public void RefreshList()
    {
        if(refresh_task == null)
        {
            refresh_task = new ProvinceDataReceiver(this, mAdapter, provinceList, nomeRegione);
            if(main_task.getStatus() == AsyncTask.Status.FINISHED) {
                Log.i("1", "ENTRO LA PRIMA VOLTA");
                refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json");
            }
            else
                swipeRefreshLayout.setRefreshing(false);
        }

        else if(refresh_task.getStatus() == AsyncTask.Status.FINISHED)
        {
            Log.i("1", "ENTRO LA SECONDA VOLTA");
            refresh_task.cancel(true);
            refresh_task = new ProvinceDataReceiver(this, mAdapter, provinceList, nomeRegione);
            refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-regioni.json");
        }
        else
        {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
