package com.jorose.moviesquare;

import android.content.Context;
import android.widget.ListView;

import java.util.Calendar;

/**
 * Created by jrose on 11/8/13.
 */
public class MovieHelper {
    public String SaveMovie(String title, String fID, String tID, float rating, Context c){
        MySQLiteHelper db = new MySQLiteHelper(c);

        //Movie badMovie = db.getMovie(5);
        //db.deleteMovie(badMovie);

        String thisDate;
        Calendar cal = Calendar.getInstance();
        thisDate = cal.getTime().toString();

        String newID = db.addMovie(new Movie(title, fID, tID, rating, thisDate, null));

        return newID;
    }

    public void RemoveMovie(String mID, Context c){
        MySQLiteHelper db = new MySQLiteHelper(c);
        Movie badMovie = db.getMovie(Integer.parseInt(mID));
        db.deleteMovie(badMovie);
    }

    public void SaveVenue(String venue_id, String venue_name, float lat, float lng, Context c){
        MySQLiteHelper db = new MySQLiteHelper(c);

        Venue v = db.getVenue(venue_id);

        if (v == null) {
            //doesn't exist, let's add it
            Venue newVenue = new Venue();
            newVenue.setVenue_id(venue_id);
            newVenue.setVenue_name(venue_name);
            newVenue.setLat(lat);
            newVenue.setLng(lng);

            String vType;
            if (venue_name.contains("AMC")){
                vType = "AMC";
            } else if (venue_name.contains("Regal")){
                vType = "Regal";
            } else {
                vType = "Other";
            }

            newVenue.setVenue_type(vType);

            db.addVenue(newVenue);

        }
    }
}
