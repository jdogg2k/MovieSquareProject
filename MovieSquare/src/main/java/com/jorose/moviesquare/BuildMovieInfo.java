package com.jorose.moviesquare;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jordanrose on 8/24/13.
 */
public class BuildMovieInfo {

    public void GetMovie(View v, String mName, String mID) {
        TextView tv = (TextView) v.findViewById(R.id.selMovieName);
        tv.setText(mName);

        JSONObject posterJson = GetMovieJSON(mName);
        String test = "test";
    }

    private JSONObject GetMovieJSON (String mName) {
        JSONParser jParser = new JSONParser();

       String mUrl = "";
        try {
            String newMovie = URLEncoder.encode(mName, "UTF-8");
            mUrl = "http://api.themoviedb.org/3/search/movie?api_key=8fea41d96d364362ed79192060a7f8f8&query=" + newMovie;
            Log.d("TEST", mUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // getting JSON string from URL
        JSONObject json = jParser.getJSONFromUrl(mUrl);

        return json;
    }
}
