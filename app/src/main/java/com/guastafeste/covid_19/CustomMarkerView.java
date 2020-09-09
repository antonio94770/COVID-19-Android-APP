package com.guastafeste.covid_19;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

/**
 * Classe che estende MarkerView per la costruzione di markers sul grafico
 */
public class CustomMarkerView extends MarkerView {

    private List<String> data;
    private List<Entry> incomeEntries;
    private List<Entry> guariti;
    private List<Entry> deceduti;

    private TextView tvContent;
    public CustomMarkerView (Context context, int layoutResource, List<Entry> yAxisValues, List<String> xAxisValues) {
        super(context, layoutResource);

        incomeEntries = yAxisValues;
        data = xAxisValues;
        tvContent = (TextView) findViewById(R.id.textViewMarker);
    }

    /**
     * Inizializzazione del markerview con i valori dei relativi assi del grafico
     * @param context
     * @param layoutResource
     * @param yAxisValues
     * @param xAxisValues
     * @param yDeceduti
     * @param yGuariti
     */
    public CustomMarkerView (Context context, int layoutResource, List<Entry> yAxisValues, List<String> xAxisValues, List<Entry> yDeceduti, List<Entry> yGuariti) {
        super(context, layoutResource);

        incomeEntries = yAxisValues;
        data = xAxisValues;
        this.guariti = yGuariti;
        this.deceduti = yDeceduti;
        tvContent = (TextView) findViewById(R.id.textViewMarker);
    }

    /**
     * Callback che viene richiamata ogni volta che clicco su una parte del grafico, portando il MarkerView a ridisegnare.
     * @param e
     * @param highlight
     */
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        incomeEntries.indexOf(e);

        if(incomeEntries.indexOf(e) != -1) {
            tvContent.setBackgroundColor(Color.RED);
            tvContent.setText(data.get(incomeEntries.indexOf(e)) + "\n" + String.valueOf(e.getY()));
        }
        else if(deceduti.indexOf(e) != -1) {
            tvContent.setBackgroundColor(Color.BLACK);
            tvContent.setText(data.get(deceduti.indexOf(e)) + "\n" + String.valueOf(e.getY()));
        }
        else if(guariti.indexOf(e) != -1) {
            tvContent.setBackgroundColor(Color.GREEN);
            tvContent.setText(data.get(guariti.indexOf(e)) + "\n" + String.valueOf(e.getY()));
        }

        Log.i("PROVA", e.toString());
        super.refreshContent(e, highlight);
    }

    /**
     * Posizione in cui mostrare i miei dati sul grafico.
     * @return Coordinate.
     */
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth()), -getHeight());
    }
}