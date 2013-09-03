package com.jorose.moviesquare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MovieListJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jMovies = null;
        try {
            /** Retrieves all the elements in the 'countries' array */
            jMovies = jObject.getJSONObject("response").getJSONObject("events").getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getCountries with the array of json object
         * where each json object represent a country
         */
        return getMovies(jMovies);
    }

    private List<HashMap<String, String>> getMovies(JSONArray jMovies){
        int movieCount = jMovies.length();
        List<HashMap<String, String>> movieList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> movie = null;

        /** Taking each country, parses and adds to list object */
        for(int i=0; i<movieCount;i++){
            try {
                /** Call getCountry with country JSON object to parse the country */
                movie = getMovie((JSONObject)jMovies.get(i));
                movieList.add(movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return movieList;
    }

    /** Parsing the Country JSON object */
    private HashMap<String, String> getMovie(JSONObject jVenue){

        HashMap<String, String> movie = new HashMap<String, String>();
        String movieName;
        String movieID;
        String moviePoster;

        try {
            movieID = jVenue.getString("id");
            movieName = jVenue.getString("name");
           // moviePoster = getPoster(movieName);


            movie.put("name", movieName);
            movie.put("id", movieID);


            //movie.put("poster", moviePoster);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private String getPoster(String movieName){
        String thisPoster = "https://d3gtl9l2a4fn1j.cloudfront.net/t/p/w92/4jJlcCC2BemPBLyezOLcumGvI8Q.jpg";


        /**parse the movie poster here
         * http://api.themoviedb.org/3/search/movie?api_key=8fea41d96d364362ed79192060a7f8f8&query=Grown%20Ups%202
         * */



        return thisPoster;
    }
}