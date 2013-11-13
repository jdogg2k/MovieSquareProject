package com.jorose.moviesquare;

/**
 * Created by jrose on 11/8/13.
 */
public class Venue {
    private int id;
    private String venue_id;
    private String venue_type;
    private String venue_name;
    private float lat;
    private float lng;

    public Venue(){}

    public Venue(String venue_id, String venue_type, String venue_name, float lat, float lng) {
        super();
        this.venue_id = venue_id;
        this.venue_type = venue_type;
        this.venue_name = venue_name;
        this.lat = lat;
        this.lng = lng;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Venue [id=" + id + ", venue_id=" + venue_id + ", venue_type=" + venue_type + ", venue_name=" + venue_name + ", lat=" + lat + ", lng=" + lng + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int venID) {
        id = venID;
    }

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_idParam) {
        venue_id = venue_idParam;
    }

    public String getVenue_type() {
        return venue_type;
    }

    public void setVenue_type(String venue_typeParam) {
        venue_type = venue_typeParam;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_nameParam) {
        venue_name = venue_nameParam;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float latParam) {
        lat = latParam;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lngParam) {
        lng = lngParam;
    }
}
