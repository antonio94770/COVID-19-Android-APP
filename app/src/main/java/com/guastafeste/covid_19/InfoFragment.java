package com.guastafeste.covid_19;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment che gestisce le informazioni generali riguadanti l'app.
 */
public class InfoFragment extends Fragment {

    private TextView descrizione_sezioni;
    private TextView descrizione_dati;
    private TextView descrizione_raccolta_dati;

    /**
     * Semplice stampa di stringhe in formato html per la gestione dei colori e del bold per alcune parole;
     * infine viene gestista la creazione della view del fragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_info, container,false);

        descrizione_sezioni = myInflatedView.findViewById(R.id.spiegazione_sezioni);
        descrizione_dati = myInflatedView.findViewById(R.id.spiegazione_dati);
        descrizione_raccolta_dati = myInflatedView.findViewById(R.id.spiegazione_raccolta_dati);

        getActivity().setTitle("Informazioni");

        //descrizione_raccolta_dati.setMovementMethod(LinkMovementMethod.getInstance());

        String spiegazione_sezioni = "<b>• Home:</b> Mostra gli ultimi dati forniti dalla protezione civile.<br><br>" +
                "<b>• Grafici: </b>Contiene 3 grafici, il primo mostra l'andamento nazionale relativo ai casi totali, deceduti e guariti; il secondo grafico mostra l'andamento del numero di contagi giornalieri; infine, l'ultimo istogramma mostra la situazione dei contagiati.<br><br>" +
                "<b>• Regioni/Province: </b>Lista contenente le regioni con i loro principali dati; è possibile cliccare su una regione per poter accedere alle province relative a quella determinata ragione. Se dovreste trovate delle incongruenze tra i nuovi casi della regione delle sue province è dato dal fatto che alcuni casi sono in fase di definizione.<br><br>" +
                "<b>• Condividi dati nazionali: </b> Con questa funzionalità è possibile condividere gli ultimi dati ottenuti dall'app.<br><br>";
        descrizione_sezioni.setText(Html.fromHtml(spiegazione_sezioni));

        String spiegazione_dati = "<b><font color='#FF0000'>• Totale casi:</font></b> totale persone risultate positive.<br><br>" +
                "<b><font color='#FF9800'>• Totale positivi:</font></b> totale persone attualmente positive sia ospedalizzate che in isolamento domiciliare.<br><br>" +
                "<b><font color='#000000'>• Deceduti:</font></b> persone decedute  (in attesa di verifica ISS).<br><br>" +
                "<b><font color='#4CAF50'>• Dimessi guariti:</font></b> totale persone clinicamente guarite.<br><br>" +
                "<b><font color='#009688'>• Ricoverati con sintomi:</font></b> totale persone ricoverate con sintomi positivi.<br><br>" +
                "<b><font color='#3D0A8C'>• Terapia intensiva:</font></b> totale persone che si trovano in terapiva intensiva.<br><br>" +
                "<b><font color='#9A558E'>• Ospedalizzati:</font></b> totale persone positive che si trovano in ospedale.<br><br>" +
                "<b><font color='#FF5F75ED'>• Isolamento domiciliare:</font></b> totale persone che si trovano in isolamento domiciliare.<br><br>" +
                "<b><font color='#E91E63'>• Tamponi:</font></b> numero di tamponi effettuati.<br><br>";
        descrizione_dati.setText(Html.fromHtml(spiegazione_dati));


        String spiegazione_raccolta_dati = "I dati raccolti dall'app vengono raccolti dal <b>Dipartimento della Protezione Civile</b> che vengono aggiornati quotidianamente alle <b>ore 18:30</b>.<br><br>" +
                "La repository da cui vengono raccolti i dati è la seguente: <a href=\"https://github.com/pcm-dpc/COVID-19\">Dati COVID-19 Italia</a><br><br>" +
                "<i>Dati forniti dal Ministero della Salute<br>" +
                "Elaborazione e gestione dati a cura del Dipartimento della Protezione Civile</i><br>" +
                "<i>Licenza: <a href=\"https://creativecommons.org/licenses/by/4.0/deed.en\">CC-BY-4.0</a> - <a href=\"https://github.com/pcm-dpc/COVID-19/blob/master/LICENSE\">Visualizza licenza</a></i>";
        descrizione_raccolta_dati.setText(Html.fromHtml(spiegazione_raccolta_dati));


        //AlarmHelper.Companion.getInstance().setAlarm(myInflatedView.getContext());


        return myInflatedView;
    }
}
