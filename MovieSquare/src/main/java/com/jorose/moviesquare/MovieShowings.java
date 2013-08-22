package com.jorose.moviesquare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jordanrose on 8/21/13.
 */
public class MovieShowings extends Activity {

    String selVenue;
    String jsonResult;
    GetMovieShowings movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message from the intent
        Intent intent = getIntent();
        selVenue = intent.getStringExtra(MainActivity.SELECTED_VENUE_ID);

        setContentView(R.layout.activity_movie_showings);

        TextView titleTV = (TextView) findViewById(R.id.theaterName);
        titleTV.setText(intent.getStringExtra(MainActivity.SELECTED_VENUE_NAME));
        movieList = new GetMovieShowings();
        movieList.execute();

    }

    private class GetMovieShowings extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            DefaultHttpClient httpclient = new DefaultHttpClient();
            final HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpConnectionParams.setSoTimeout(httpParams, 30000);

            HttpGet httppost = new HttpGet("https://api.foursquare.com/v2/venues/" + selVenue + "/events?client_id=" + MainActivity.CLIENT_ID +"&client_secret=" + MainActivity.CLIENT_SECRET); //

            try{

                HttpResponse response = httpclient.execute(httppost);  //response class to handle responses
                jsonResult = inputStreamToString(response.getEntity().getContent()).toString();

                /** The parsing of the xml data is done in a non-ui thread */
                ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

                /** Start parsing xml data */
                listViewLoaderTask.execute(jsonResult);
            }
            catch(ConnectTimeoutException e){
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return jsonResult;
        }

        protected void onPostExecute(String Result){
            try{

                //Toast.makeText(getApplicationContext(), "R E S U L T :"+jsonResult, Toast.LENGTH_LONG).show();
                System.out.println(jsonResult);
                //showing result

            }catch(Exception E){
                Toast.makeText(getApplicationContext(), "Error:"+E.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return answer;
        }

        private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter>{

            JSONObject jObject;
            /** Doing the parsing of xml data in a non-ui thread */
            @Override
            protected SimpleAdapter doInBackground(String... strJson) {
                try{
                    jObject = new JSONObject(strJson[0]);
                    MovieListJSONParser movieListJSONParser = new MovieListJSONParser();
                    movieListJSONParser.parse(jObject);
                }catch(Exception e){
                    Log.d("JSON Exception1", e.toString());
                }

                MovieListJSONParser movieListJSONParser = new MovieListJSONParser();

                List<HashMap<String, String>> movies = null;

                try{
                    /** Getting the parsed data as a List construct */
                    movies = movieListJSONParser.parse(jObject);
                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }

                /** Keys used in Hashmap */
                String[] from = {"name"};

                /** Ids of views in listview_layout */
                int[] to = { R.id.movie_name};

                /** Instantiating an adapter to store each items
                 *  R.layout.listview_layout defines the layout of each item
                 */
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), movies, R.layout.movie_lv_layout, from, to);

                return adapter;
            }

            /** Invoked by the Android system on "doInBackground" is executed completely */
            /** This will be executed in ui thread */
            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                /** Getting a reference to listview of main.xml layout file */
                final ListView listView = (ListView) findViewById(R.id.movieShowingsList);

                listView.setBackgroundColor(Color.rgb(240, 240, 240));

                /** Setting the adapter containing the country list to listview */
                listView.setAdapter(adapter);

                listView.setClickable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        HashMap hm = (HashMap) listView.getItemAtPosition(position);

                        /*String venID = hm.get("id").toString();
                        selVenueID = venID;
                        showMovies(listView);*/

                    }
                });

            }
        }

    }

}