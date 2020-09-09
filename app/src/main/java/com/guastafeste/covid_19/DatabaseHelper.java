package com.guastafeste.covid_19;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe per la gestione e creazione di database e tabelle.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Uso una sola istanza della classe Database Helper
     */
    private static DatabaseHelper mInstance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "coronavirus.db";
    private static final String TABLE_NAME_GLOBAL = "coronavirus_globale";
    private static final String TABLE_NAME_REGIONAL = "coronavirus_regionale";
    private static final String TABLE_NAME_PROVINCE = "coronavirus_province";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATA = "data";
    private static final String COLUMN_DENOMINAZIONE_REGIONE = "denominazione_regione";
    private static final String COLUMN_DENOMINAZIONE_PROVINCIA = "denominazione_provincia";
    private static final String COLUMN_TOTALE_CASI = "totale_casi";
    private static final String COLUMN_VARIAZIONE_TOTALE_POSITIVI = "variazione_totale_positivi";
    private static final String COLUMN_DECEDUTI = "deceduti";
    private static final String COLUMN_TOTALE_ATTUALMENTE_POSITIVI = "totale_attualmente_positivi";
    private static final String COLUMN_DIMESSI_GUARITI = "dimessi_guariti";
    private static final String COLUMN_RICOVERATI_CON_SINTOMI = "ricoverati_con_sintomi";
    private static final String COLUMN_TERAPIA_INTENSIVA = "terapia_intensiva";
    private static final String COLUMN_TOTALE_OSPEDALIZZATI = "totale_ospedalizzati";
    private static final String COLUMN_ISOLAMENTO_DOMICILIARE = "isolamento_domiciliare";
    private static final String COLUMN_TAMPONI = "tamponi";

    SQLiteDatabase db;

    private static final String GLOBAL_TABLE_CREATE = "create table coronavirus_globale (id integer PRIMARY KEY AUTOINCREMENT NOT NULL , " +
        "data text not null , totale_casi text not null , variazione_totale_positivi text not null, deceduti text not null , totale_attualmente_positivi text not null , dimessi_guariti text not null , ricoverati_con_sintomi text not null , terapia_intensiva text not null, totale_ospedalizzati text not null, isolamento_domiciliare text not null, tamponi text not null);";
    private static final String REGIONAL_TABLE_CREATE = "create table coronavirus_regionale (id integer PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "data text not null, denominazione_regione text not null , totale_casi text not null, deceduti text not null , totale_attualmente_positivi text not null , dimessi_guariti text not null , ricoverati_con_sintomi text not null, terapia_intensiva text not null, totale_ospedalizzati text not null, isolamento_domiciliare text not null, tamponi text not null);";
    private static final String PROVINCE_TABLE_CREATE = "create table coronavirus_province (id integer PRIMARY KEY AUTOINCREMENT NOT NULL , " +
            "data text not null, denominazione_regione text not null , denominazione_provincia text not null , totale_casi text not null);";


    /**
     * Singola istanza, singleton
     */
    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mInstance;
    }


    /**
     * Costruttore.
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creazione tabelle.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GLOBAL_TABLE_CREATE);
        db.execSQL(REGIONAL_TABLE_CREATE);
        db.execSQL(PROVINCE_TABLE_CREATE);
        this.db = db;
    }



    /**
     * Cancello le tabelle se viene modificata la loro struttura.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " +TABLE_NAME_GLOBAL;
        db.execSQL(query);
        String query2 = "DROP TABLE IF EXISTS " +TABLE_NAME_REGIONAL;
        db.execSQL(query2);
        String query3 = "DROP TABLE IF EXISTS " + TABLE_NAME_PROVINCE;
        db.execSQL(query3);
        this.onCreate(db);
    }

    /**
     * Inserimento valori globali nel database.
     * @param globalStat
     */
    public void insertGlobalStats(CoronavirusStat globalStat) {
        db = this.getWritableDatabase();

        String query = "select * from " + TABLE_NAME_GLOBAL;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        ContentValues contentValues = new ContentValues();
        //contentValues.put(COLUMN_ID, count);
        contentValues.put(COLUMN_DATA, globalStat.getData());
        contentValues.put(COLUMN_TOTALE_CASI, globalStat.getTotale_casi());
        contentValues.put(COLUMN_VARIAZIONE_TOTALE_POSITIVI, globalStat.getVariazione_totale_positivi());
        contentValues.put(COLUMN_DECEDUTI, globalStat.getDeceduti());
        contentValues.put(COLUMN_TOTALE_ATTUALMENTE_POSITIVI, globalStat.getTotale_positivi());
        contentValues.put(COLUMN_DIMESSI_GUARITI, globalStat.getDimessi_guariti());
        contentValues.put(COLUMN_RICOVERATI_CON_SINTOMI, globalStat.getRicoverati_con_sintomi());
        contentValues.put(COLUMN_TERAPIA_INTENSIVA, globalStat.getTerapia_intensiva());
        contentValues.put(COLUMN_TOTALE_OSPEDALIZZATI, globalStat.getTotale_ospedalizzati());
        contentValues.put(COLUMN_ISOLAMENTO_DOMICILIARE, globalStat.getIsolamento_domiciliare());
        contentValues.put(COLUMN_TAMPONI, globalStat.getTamponi());

        db.insert(TABLE_NAME_GLOBAL, null, contentValues);
        cursor.close();
    }

    /**
     * Inserimento valori regionali nel database.
     * @param regionalStat
     */
    public void insertRegionalStats(CoronavirusStat regionalStat) {
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        //contentValues.put(COLUMN_ID, count);
        contentValues.put(COLUMN_DATA, regionalStat.getData());
        contentValues.put(COLUMN_DENOMINAZIONE_REGIONE, regionalStat.getDenominazione_regione());
        contentValues.put(COLUMN_TOTALE_CASI, regionalStat.getTotale_casi());
        contentValues.put(COLUMN_DECEDUTI, regionalStat.getDeceduti());
        contentValues.put(COLUMN_TOTALE_ATTUALMENTE_POSITIVI, regionalStat.getTotale_positivi());
        contentValues.put(COLUMN_DIMESSI_GUARITI, regionalStat.getDimessi_guariti());
        contentValues.put(COLUMN_RICOVERATI_CON_SINTOMI, regionalStat.getRicoverati_con_sintomi());
        contentValues.put(COLUMN_TERAPIA_INTENSIVA, regionalStat.getTerapia_intensiva());
        contentValues.put(COLUMN_TOTALE_OSPEDALIZZATI, regionalStat.getTotale_ospedalizzati());
        contentValues.put(COLUMN_ISOLAMENTO_DOMICILIARE, regionalStat.getIsolamento_domiciliare());
        contentValues.put(COLUMN_TAMPONI, regionalStat.getTamponi());

        db.insert(TABLE_NAME_REGIONAL, null, contentValues);
    }

    /**
     * Cancello le righe dalla tabella Regioni se abbiamo più di due set di valori
     */
    public void deleteFromRegioni()
    {
        //Cerca se ci sono più di 2 regioni
        db = this.getWritableDatabase();
        String query_search = "SELECT * FROM " + TABLE_NAME_REGIONAL;
        Cursor cursor_search = db.rawQuery(query_search,null);

        int n = cursor_search.getCount();

        int i = 0;
        if(n > 21*2)
        {
            while (i < n - 2*21)
            {
                cursor_search.moveToNext();
                String row_id = cursor_search.getString(0);
                String query_delete = "DELETE from " + TABLE_NAME_REGIONAL + " WHERE id='" + row_id+"'";
                db.execSQL(query_delete);
                i++;
            }

        }

        cursor_search.close();
    }

    /**
     * Inserimento valori delle province nel database.
     * @param regionalStat
     */
    public void insertProvinceStats(CoronavirusStat regionalStat) {
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        //contentValues.put(COLUMN_ID, count);
        contentValues.put(COLUMN_DATA, regionalStat.getData());
        contentValues.put(COLUMN_DENOMINAZIONE_REGIONE, regionalStat.getDenominazione_regione());
        contentValues.put(COLUMN_DENOMINAZIONE_PROVINCIA, regionalStat.getDenominazione_provincia());
        contentValues.put(COLUMN_TOTALE_CASI, regionalStat.getTotale_casi());

        db.insert(TABLE_NAME_PROVINCE, null, contentValues);
    }

    /**
     * Cancello le righe dalla tabella Province se abbiamo più di due set di valori
     */
    public void deleteFromProvince() {

        //Cerca se ci sono più di 2 province
        db = this.getWritableDatabase();
        String query_search = "SELECT * FROM " + TABLE_NAME_PROVINCE;
        Cursor cursor_search = db.rawQuery(query_search,null);

        int n = cursor_search.getCount();

        int i = 0;
        if(n > 21*2)
        {
            while (i < n - 2*107)
            {
                cursor_search.moveToNext();
                String row_id = cursor_search.getString(0);
                String query_delete = "DELETE from " + TABLE_NAME_PROVINCE + " WHERE id='" + row_id+"'";
                db.execSQL(query_delete);
                i++;
            }

        }

        cursor_search.close();
    }

    /**
     * Controllo se nella tabella dei dati globali viene trovata la tabella passata come parametro.
     * @param data_passed
     * @return True se trovo un valore, false altrimenti
     */
    public boolean checkDatabaseGlobalData(String data_passed) {
        db = this.getReadableDatabase();
        String query = "select data  from "+TABLE_NAME_GLOBAL+ " where data LIKE '%"+ data_passed +"%'";
        String a;
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }


    /**
     * Controllo se nella tabella dei dati regionali viene trovata la tabella passata come parametro.
     * @param data_passed
     * @return True se trovo un valore, false altrimenti
     */
    public boolean checkDatabaseRegionalData(String data_passed) {
        db = this.getReadableDatabase();
        String query = "select data  from "+TABLE_NAME_REGIONAL+ " where data LIKE '%"+ data_passed +"%'";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }

    /**
     * Controllo se nella tabella dei dati province viene trovata la tabella passata come parametro.
     * @param data_passed
     * @return True se trovo un valore, false altrimenti
     */
    public boolean checkDatabaseProvinceData(String data_passed) {
        db = this.getReadableDatabase();
        String query = "select data  from "+ TABLE_NAME_PROVINCE + " where data LIKE '%"+ data_passed +"%'";

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.getCount() > 0) {
            cursor.close();
            return true;
        }

        cursor.close();
        return false;
    }


    /**
     * Prendo tutti i valori dalla tabella dei dati globali
     * @return cursor della tabella dei dati globali
     */
    public Cursor getAllDatabaseGlobal() {
        db = this.getReadableDatabase();
        String query = "select data, totale_casi, variazione_totale_positivi, deceduti, totale_attualmente_positivi, dimessi_guariti, ricoverati_con_sintomi, terapia_intensiva, totale_ospedalizzati, isolamento_domiciliare, tamponi from "+ TABLE_NAME_GLOBAL + " order by data asc";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    /**
     * Prendo tutti i valori dalla tabella dei dati regionali
     * @return cursor della tabella dei dati regionali
     */
    public Cursor getAllDatabaseRegional() {
        db = this.getReadableDatabase();
        String query = "select data, denominazione_regione , totale_casi, deceduti, totale_attualmente_positivi, dimessi_guariti, ricoverati_con_sintomi, terapia_intensiva, totale_ospedalizzati, isolamento_domiciliare, tamponi from "+ TABLE_NAME_REGIONAL;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }


    /**
     * Funzione per ottenere le province dalle regioni
     * @param regione
     * @return
     */
    public Cursor getDatabaseProvinceFromRegione(String regione) {
        db = this.getReadableDatabase();
        String query;

        if(regione.equals("Valle d'Aosta"))
        {
            query = "select data, denominazione_regione , denominazione_provincia, totale_casi from "+ TABLE_NAME_PROVINCE + " where denominazione_regione='Valle d''Aosta'";
        }
        else
            query = "select data, denominazione_regione , denominazione_provincia, totale_casi from "+ TABLE_NAME_PROVINCE + " where denominazione_regione='" + regione + "'";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }


}
