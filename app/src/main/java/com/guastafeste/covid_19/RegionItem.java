package com.guastafeste.covid_19;

/**
 * Classe per definire l'oggetto regione che identificherà un elemento della recyclerView.
 */
public class RegionItem {
    private int rImage;
    private String rTitle;
    private String rDescription;

    /**
     * Costruttore in cui viene impostata l'immagine, il nome della regione e la descrizione dei casi.
     * @param imageResource
     * @param regione
     * @param descrizione
     */
    public RegionItem(int imageResource, String regione, String descrizione)
    {
        rImage = imageResource;
        rTitle = regione;
        rDescription = descrizione;
    }

    public void setrImage(int rImage) {
        this.rImage = rImage;
    }

    public void setrTitle(String rTitle) {
        this.rTitle = rTitle;
    }

    public void setrDescription(String rDescription) {
        this.rDescription = rDescription;
    }

    public int getrImage() {
        return rImage;
    }

    public String getrTitle() {
        return rTitle;
    }

    public String getrDescription() {
        return rDescription;
    }


}
