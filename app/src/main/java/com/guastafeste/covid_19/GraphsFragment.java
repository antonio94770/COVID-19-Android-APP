package com.guastafeste.covid_19;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment che gestisce i grafici del Coronavirus.
 */
public class GraphsFragment extends Fragment {

    private View myInflatedView;
    private static PieChart pieChart;
    private static LineChart mLineGraphAndamentoNazionale;
    private static LineChart mLineGraphNuoviPositivi;

    private SwipeRefreshLayout swipeRefreshLayout;

    private GlobalDataReceiver main_task;
    private GlobalDataReceiver refresh_task;

    /**
     * Viene chiamato il task asincrono per la raccolta dei dati e viene gestista la creazione della view del fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_graphs, container,false);
        pieChart = (PieChart)  myInflatedView.findViewById(R.id.piechart);

        mLineGraphAndamentoNazionale = (LineChart) myInflatedView.findViewById(R.id.linechart_andamento_nazionale);
        mLineGraphNuoviPositivi = (LineChart) myInflatedView.findViewById(R.id.linechart_casi);

        getActivity().setTitle("Grafici");

        main_task = new GlobalDataReceiver(myInflatedView, false);
        main_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");


        //Inizializzazione e inserimento della funzione RefreshList all'interno del listener.
        swipeRefreshLayout = myInflatedView.findViewById(R.id.swipeRefreshLayoutGraphs);
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
     * Grafico della differenza dei nuovi positivi giornalieri.
     * @param globalStats
     * @param myInflatedView
     */
    public static void LineChartNuoviPositivi(CoronavirusStat[] globalStats, View myInflatedView)
    {
        ArrayList<ILineDataSet> dataSets;
        List<String> xAxisValues = new ArrayList<>();
        String temp;

        //DATA
        for (int i=0; i < globalStats.length; i++) {
            temp = globalStats[i].getData().substring(0, globalStats[i].getData().indexOf("T"));
            xAxisValues.add(i, temp); //Dynamic x-axis labels
        }

        List<Entry> incomeEntries = new ArrayList<>();

        //POSITIVI GIORNALIERI
        incomeEntries.add(new Entry(0, 0));
        float temp_difference;
        for(int i=1;i < globalStats.length; i++)
        {
            temp_difference = Float.parseFloat(globalStats[i].getTotale_casi()) - Float.parseFloat(globalStats[i-1].getTotale_casi());
            incomeEntries.add(new Entry(i,temp_difference));
        }

        dataSets = new ArrayList<>();
        LineDataSet set1;

        set1 = new LineDataSet(incomeEntries, "Positivi");
        set1.setColor(Color.rgb(255, 0, 0));
        set1.setValueTextColor(Color.rgb(255, 0, 0));
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setLineWidth(1.8f);
        set1.setCircleRadius(4f);
        set1.setCircleColor(Color.RED);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.RED);
        set1.setFillColor(Color.RED);
        set1.setFillAlpha(100);
        set1.setDrawHorizontalHighlightIndicator(true);

        //Formattazione per l'asse x delle date
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mLineGraphNuoviPositivi.getAxisLeft().getAxisMinimum();
            }
        });
        dataSets.add(set1);


        //Customizzazione
        mLineGraphNuoviPositivi.setTouchEnabled(true);
        mLineGraphNuoviPositivi.setDragEnabled(true);
        mLineGraphNuoviPositivi.setScaleEnabled(true);
        mLineGraphNuoviPositivi.setPinchZoom(false);
        mLineGraphNuoviPositivi.setDrawGridBackground(false);
        mLineGraphNuoviPositivi.setExtraLeftOffset(5);
        mLineGraphNuoviPositivi.setExtraRightOffset(30);

        //Per nascondere le linee di background
        mLineGraphNuoviPositivi.getXAxis().setDrawGridLines(true);
        mLineGraphNuoviPositivi.getAxisLeft().setDrawGridLines(false);
        mLineGraphNuoviPositivi.getAxisRight().setDrawGridLines(false);

        YAxis rightYAxis = mLineGraphNuoviPositivi.getAxisRight();
        rightYAxis.setEnabled(false);

        //Per nascondere i bordi
        mLineGraphNuoviPositivi.setDrawBorders(true);


        XAxis xAxis = mLineGraphNuoviPositivi.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        xAxis.setLabelCount(4, false);

        //Stringa nell'asse x
        mLineGraphNuoviPositivi.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData(dataSets);

        //Viene settato il MarkerView
        CustomMarkerView mv = new CustomMarkerView (myInflatedView.getContext(), R.layout.custom_marker_layout, incomeEntries, xAxisValues);
        mLineGraphNuoviPositivi.setMarker(mv);

        mLineGraphNuoviPositivi.setData(data);
        mLineGraphNuoviPositivi.animateX(2000);
        mLineGraphNuoviPositivi.invalidate();
        mLineGraphNuoviPositivi.getLegend().setEnabled(true);
        mLineGraphNuoviPositivi.getDescription().setEnabled(false);
    }

    /**
     * Grafico a torta della situazione dei contagiati.
     * @param globalStats
     */
    public static void CreatePieChartMortiGuaritiTE(CoronavirusStat[] globalStats)
    {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        //CARICAMENTO DEI DATI
        try {
            yValues.add(new PieEntry(Float.parseFloat(globalStats[globalStats.length-1].getDeceduti()), "Deceduti"));
            yValues.add(new PieEntry(Float.parseFloat(globalStats[globalStats.length-1].getDimessi_guariti()), "Guariti"));
            yValues.add(new PieEntry(Float.parseFloat(globalStats[globalStats.length-1].getTerapia_intensiva()), "Terapia Intensiva"));
            yValues.add(new PieEntry(Float.parseFloat(globalStats[globalStats.length-1].getTotale_ospedalizzati()), "Ospedalizzati"));
            yValues.add(new PieEntry(Float.parseFloat(globalStats[globalStats.length-1].getIsolamento_domiciliare()), "Isolamento Domiciliare"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //DIFFERENZIAMENTO DEI COLORI
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#000000"));
        colors.add(Color.parseColor("#4CAF50"));
        colors.add(Color.parseColor("#3D0A8C"));
        colors.add(Color.parseColor("#9A558E"));
        colors.add(Color.parseColor("#FF5F75ED"));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);


        PieData data = new PieData((dataSet));
        data.setValueTextSize(25f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(40f);

        pieChart.animateX(2000);


        pieChart.setNoDataText("Download...");

        pieChart.setData(data);

        pieChart.getLegend().setWordWrapEnabled(false);

        pieChart.invalidate();

    }


    /**
     * Grafico dell'andamento nazionale
     * @param globalStats
     * @param myInflatedView
     */
    public static void LineChartAndamentoNazionale(CoronavirusStat[] globalStats, View myInflatedView)
    {
        ArrayList<ILineDataSet> dataSets;
        List<String> xAxisValues = new ArrayList<>();
        String temp;

        //DATA
        for (int i=0; i < globalStats.length; i++) {
            temp = globalStats[i].getData().substring(0, globalStats[i].getData().indexOf("T"));
            xAxisValues.add(i, temp);
        }


        //TOTALE POSITIVI
        List<Entry> totale_casi = new ArrayList<>();
        for(int i = 0; i < globalStats.length; i++)
        {
            totale_casi.add(new Entry(i,Float.parseFloat(globalStats[i].getTotale_casi())));
        }

        //TOTALE DECEDUTI
        List<Entry> deceduti = new ArrayList<>();
        for(int i = 0; i < globalStats.length; i++)
        {
            deceduti.add(new Entry(i,Float.parseFloat(globalStats[i].getDeceduti())));
        }

        //TOTALE DECEDUTI
        List<Entry> dimessi_guariti = new ArrayList<>();
        for(int i = 0; i < globalStats.length; i++)
        {
            dimessi_guariti.add(new Entry(i,Float.parseFloat(globalStats[i].getDimessi_guariti())));
        }



        dataSets = new ArrayList<>();
        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;

        set1 = new LineDataSet(totale_casi, "Totale Casi");
        set1.setValueTextSize(20f);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(false);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setLineWidth(2f);
        set1.setCircleRadius(4f);
        set1.setCircleColor(Color.RED);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.RED);
        set1.setFillColor(Color.RED);
        set1.setFillAlpha(50);
        set1.setDrawHorizontalHighlightIndicator(true);
        //Formattazione dell'asse x delle date per i casi totali
        set1.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mLineGraphAndamentoNazionale.getAxisLeft().getAxisMinimum();
            }
        });

        set2 = new LineDataSet(deceduti, "Deceduti");
        set2.setValueTextSize(20f);
        set2.setMode(LineDataSet.Mode.LINEAR);
        set2.setCubicIntensity(0.2f);
        set2.setDrawFilled(false);
        set2.setDrawValues(false);
        set2.setDrawCircles(false);
        set2.setLineWidth(2f);
        set2.setCircleRadius(4f);
        set2.setCircleColor(Color.BLACK);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        set2.setColor(Color.BLACK);
        set2.setFillColor(Color.BLACK);
        set2.setDrawHorizontalHighlightIndicator(true);
        set2.setFillAlpha(255);
        //Formattazione dell'asse x delle date per i deceduti
        set2.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mLineGraphAndamentoNazionale.getAxisLeft().getAxisMinimum();
            }
        });

        //Set di dati dei guariti
        set3 = new LineDataSet(dimessi_guariti, "Guariti");
        set3.setValueTextSize(20f);
        set3.setMode(LineDataSet.Mode.LINEAR);
        set3.setCubicIntensity(0.2f);
        set3.setDrawFilled(false);
        set3.setDrawValues(false);
        set3.setDrawCircles(false);
        set3.setLineWidth(2f);
        set3.setCircleRadius(4f);
        set3.setCircleColor(Color.GREEN);
        set3.setHighLightColor(Color.rgb(244, 117, 117));
        set3.setColor(Color.GREEN);
        set3.setFillColor(Color.GREEN);
        set3.setFillAlpha(50);
        set3.setDrawHorizontalHighlightIndicator(false);
        //Formattazione dell'asse x delle date per i guariti
        set3.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return mLineGraphAndamentoNazionale.getAxisLeft().getAxisMinimum();
            }
        });
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);


        //Customizzazione
        mLineGraphAndamentoNazionale.setTouchEnabled(true);
        mLineGraphAndamentoNazionale.setDragEnabled(true);
        mLineGraphAndamentoNazionale.setScaleEnabled(true);
        mLineGraphAndamentoNazionale.setPinchZoom(false);
        mLineGraphAndamentoNazionale.setDrawGridBackground(false);
        mLineGraphAndamentoNazionale.setExtraLeftOffset(5);
        mLineGraphAndamentoNazionale.setExtraRightOffset(30);

        //Per nascondere le linee di background
        mLineGraphAndamentoNazionale.getXAxis().setDrawGridLines(true);
        mLineGraphAndamentoNazionale.getAxisLeft().setDrawGridLines(false);
        mLineGraphAndamentoNazionale.getAxisRight().setDrawGridLines(false);


        YAxis rightYAxis = mLineGraphAndamentoNazionale.getAxisRight();
        rightYAxis.setEnabled(false);

        //Per nascondere i bordi
        mLineGraphAndamentoNazionale.setDrawBorders(false);


        XAxis xAxis = mLineGraphAndamentoNazionale.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(4, false);


        //Stringa nell'asse x
        mLineGraphAndamentoNazionale.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        LineData data = new LineData(dataSets);

        //Viene settato il MarkerView
        CustomMarkerView mv = new CustomMarkerView (myInflatedView.getContext(), R.layout.custom_marker_layout, totale_casi, xAxisValues, deceduti, dimessi_guariti);
        mLineGraphAndamentoNazionale.setMarker(mv);

        mLineGraphAndamentoNazionale.setData(data);
        mLineGraphAndamentoNazionale.animateX(2000);
        mLineGraphAndamentoNazionale.invalidate();
        mLineGraphAndamentoNazionale.getLegend().setEnabled(true);
        mLineGraphAndamentoNazionale.getDescription().setEnabled(false);
    }

    /**
     * Funzione per il refresh della dei dati, richiamando i task asincroni solo se il task principale è terminato, o se questo nuovo task è terminato.
     */
    public void RefreshList()
    {
        if(myInflatedView != null) {
            if (refresh_task == null) {
                refresh_task = new GlobalDataReceiver(myInflatedView, false);
                if (main_task.getStatus() == AsyncTask.Status.FINISHED) {
                    refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");
                } else
                    swipeRefreshLayout.setRefreshing(false);
            } else if (refresh_task.getStatus() == AsyncTask.Status.FINISHED) {
                refresh_task.cancel(true);
                refresh_task = new GlobalDataReceiver(myInflatedView, false);
                refresh_task.execute("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-andamento-nazionale.json");
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}
