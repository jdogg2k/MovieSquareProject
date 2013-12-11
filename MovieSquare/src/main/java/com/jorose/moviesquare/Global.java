package com.jorose.moviesquare;

import android.app.Application;

/**
 * Created by jordanrose on 10/24/13.
 */
public  class  Global extends Application {
    private String venueID = "";
    private String eventID = "";
    private String checkinMovieName = "";
    private String checkinMovieID = "0";
    private Boolean useDelay = false;
    public String get_venue() {
        return venueID;
    }
    public void set_venue(String venueID) {
        this.venueID = venueID;
    }
    public String get_event() {
        return eventID;
    }
    public void set_event(String eventID) {
        this.eventID = eventID;
    }
    public String get_checkinMovieName() {
        return checkinMovieName;
    }
    public void set_checkinMovieName(String movieName) {
        this.checkinMovieName = movieName;
    }
    public String get_checkinMovieID() {
        return checkinMovieID;
    }
    public void set_checkinMovieID(String mID) {
        this.checkinMovieID = mID;
    }
}
