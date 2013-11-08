package com.jorose.moviesquare;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by jrose on 11/8/13.
 */
public class MovieHelper {
    public void SaveMovie(String title, String fID, String tID, float rating, Context c){
        MySQLiteHelper db = new MySQLiteHelper(c);

        String thisDate;
        Calendar cal = Calendar.getInstance();
        thisDate = cal.getTime().toString();

        db.addMovie(new Movie(title, fID, tID, rating, thisDate));

       // Movie badMovie = db.getMovie(1);
       // db.deleteMovie(badMovie);

        db.getAllMovies();
    }
}
