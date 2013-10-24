package com.jorose.moviesquare;

import android.app.Application;

/**
 * Created by jordanrose on 10/24/13.
 */
public  class  Global extends Application {
    private String venueID = "";
    private String eventID = "";
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
}
