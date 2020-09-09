package com.guastafeste.covid_19;

/**
 * Classe per la gestione dei diversi parametri che può assumere una dato relativo al coronavirus.
 */
public class CoronavirusStat {
    private String data;

    private String denominazione_regione;
    private String denominazione_provincia;
    private String totale_casi;
    private String deceduti;
    private String totale_positivi;
    private String dimessi_guariti;
    private String ricoverati_con_sintomi;
    private String terapia_intensiva;
    private String variazione_totale_positivi;
    private String latitudine;
    private String longitudine;
    private String tamponi;
    private String totale_ospedalizzati;
    private String isolamento_domiciliare;

    public CoronavirusStat()
    {
        this.data = "";
        this.denominazione_regione = "";
        this.totale_casi = "";
        this.deceduti = "";
        this.totale_positivi = "";
        this.dimessi_guariti = "";
        this.ricoverati_con_sintomi = "";
        this.terapia_intensiva = "";
        this.latitudine = "";
        this.longitudine = "";
        this.tamponi = "";
        this.totale_ospedalizzati = "";
        this.isolamento_domiciliare = "";
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRicoverati_con_sintomi() {
        return ricoverati_con_sintomi;
    }

    public void setRicoverati_con_sintomi(String ricoverati_con_sintomi) {
        this.ricoverati_con_sintomi = ricoverati_con_sintomi;
    }

    public String getTotale_positivi() {
        return totale_positivi;
    }

    public void setTotale_positivi(String totale_positivi) {
        this.totale_positivi = totale_positivi;
    }

    public String getDimessi_guariti() {
        return dimessi_guariti;
    }

    public void setDimessi_guariti(String dimessi_guariti) {
        this.dimessi_guariti = dimessi_guariti;
    }

    public String getDeceduti() {
        return deceduti;
    }

    public void setDeceduti(String deceduti) {
        this.deceduti = deceduti;
    }

    public String getTotale_casi() {
        return totale_casi;
    }

    public void setTotale_casi(String totale_casi) {
        this.totale_casi = totale_casi;
    }

    public String getTerapia_intensiva() {
        return terapia_intensiva;
    }

    public void setTerapia_intensiva(String terapia_intensiva) {
        this.terapia_intensiva = terapia_intensiva;
    }

    public String getDenominazione_regione() {
        return denominazione_regione;
    }

    public void setDenominazione_regione(String denominazione_regione) {
        this.denominazione_regione = denominazione_regione;
    }

    public String getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(String latitudine) {
        this.latitudine = latitudine;
    }

    public String getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(String longitudine) {
        this.longitudine = longitudine;
    }

    public String getTamponi() {
        return tamponi;
    }

    public void setTamponi(String tamponi) {
        this.tamponi = tamponi;
    }

    public String getTotale_ospedalizzati() {
        return totale_ospedalizzati;
    }

    public void setTotale_ospedalizzati(String totale_ospedalizzati) {
        this.totale_ospedalizzati = totale_ospedalizzati;
    }

    public String getIsolamento_domiciliare() {
        return isolamento_domiciliare;
    }

    public void setIsolamento_domiciliare(String isolamento_domiciliare) {
        this.isolamento_domiciliare = isolamento_domiciliare;
    }

    public String getDenominazione_provincia() {
        return denominazione_provincia;
    }

    public void setDenominazione_provincia(String denominazione_provincia) {
        this.denominazione_provincia = denominazione_provincia;
    }

    public String getVariazione_totale_positivi() {
        return variazione_totale_positivi;
    }

    public void setVariazione_totale_positivi(String variazione_totale_positivi) {
        this.variazione_totale_positivi = variazione_totale_positivi;
    }
}
