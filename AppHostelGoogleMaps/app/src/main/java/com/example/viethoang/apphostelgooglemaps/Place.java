package com.example.viethoang.apphostelgooglemaps;

public class Place {
    private String id_place;
    private String lnglat;
    private String title;

    public Place() {
    }

    public Place(String id_place, String lnglat, String title) {
        this.id_place = id_place;
        this.lnglat = lnglat;
        this.title = title;
    }

    public String getId_place() {
        return id_place;
    }

    public void setId_place(String id_place) {
        this.id_place = id_place;
    }

    public String getLnglat() {
        return lnglat;
    }

    public void setLnglat(String lnglat) {
        this.lnglat = lnglat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
