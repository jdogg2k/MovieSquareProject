package com.jorose.moviesquare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VenueJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject jObject){

        JSONArray jVenues = null;
        try {
            /** Retrieves all the elements in the 'countries' array */
            jVenues = jObject.getJSONObject("response").getJSONArray("venues");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getCountries with the array of json object
         * where each json object represent a country
         */
        return getVenues(jVenues);
    }

    private List<HashMap<String, String>> getVenues(JSONArray jVenues){
        int venueCount = jVenues.length();
        List<HashMap<String, String>> venueList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> venue = null;

        /** Taking each country, parses and adds to list object */
        for(int i=0; i<venueCount;i++){
            try {
                /** Call getCountry with country JSON object to parse the country */
                venue = getVenue((JSONObject)jVenues.get(i));
                venueList.add(venue);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return venueList;
    }

    /** Parsing the Country JSON object */
    private HashMap<String, String> getVenue(JSONObject jVenue){

        HashMap<String, String> venue = new HashMap<String, String>();
        String venueName = "";
        String venueLocation = "";
        String venueLat = "";
        String venueLng = "";
        String venueCheckIns = "";
        String venueID = "";

        try {
            venueID = jVenue.getString("id");
            venueName = jVenue.getString("name");
            venueLat = jVenue.getJSONObject("location").getString("lat");
            venueLng = jVenue.getJSONObject("location").getString("lng");
            venueLocation = jVenue.getJSONObject("location").getString("city") + ", " + jVenue.getJSONObject("location").getString("state");
            venueCheckIns = jVenue.getJSONObject("hereNow").getString("count");

            venue.put("name", venueName);
            venue.put("location", venueLocation);
            venue.put("lat", venueLat);
            venue.put("lng", venueLng);
            venue.put("checkIn", venueCheckIns);
            venue.put("id", venueID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return venue;
    }
}