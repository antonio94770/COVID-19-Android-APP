package com.guastafeste.covid_19;

/**
 * Classe per definire l'oggetto provincia che identificherà un elemento della recyclerView.
 */
public class ProvinceItem {

    private int rImage;
    private String rNomeProvincia;
    private String rInfo;

    /**
     * Costruttore in cui viene impostata l'immagine, il nome della provincia e la descrizione dei casi.
     * @param imageResource
     * @param provincia
     * @param info
     */
    public ProvinceItem(int imageResource, String provincia, String info)
    {
        rImage = imageResource;
        rNomeProvincia = provincia;
        rInfo = info;
    }

    public int getrImage() {
        return rImage;
    }

    public void setrImage(int rImage) {
        this.rImage = rImage;
    }

    public String getrNomeProvincia() {
        return rNomeProvincia;
    }

    public void setrNomeProvincia(String rNomeProvincia) {
        this.rNomeProvincia = rNomeProvincia;
    }

    public String getrInfo() {
        return rInfo;
    }

    public void setrInfo(String rInfo) {
        this.rInfo = rInfo;
    }
}
