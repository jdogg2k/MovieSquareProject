package com.jorose.moviesquare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by jordanrose on 8/21/13.
 */
public class MovieShowings extends Activity {

    String selVenue;
    String jsonResult;
    GetMovieShowings movieList;
    LinearLayout checkSpan;
    LinearLayout confirmSpan;

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

                        Global global = ((Global)getApplicationContext());

                        String movieURL = hm.get("url").toString();
                        String movieName = hm.get("name").toString();
                        String movieInfo = "";
                        String eventID = hm.get("id").toString();

                        global.set_event(eventID);

                        int mPos = movieURL.indexOf("movie=") + 6;
                        int mEnd = movieURL.indexOf("&wired=");
                        String movieID = movieURL.substring(mPos, mEnd);

                        try{ //use fandango to get poster Image

                            LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popupLinearLayout);
                            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View layout = layoutInflater.inflate(R.layout.activity_movie_info, viewGroup);

                            int popupWidth = 610;
                            int popupHeight = 800;

                            Document doc = Jsoup.connect("http://www.fandango.com/movies/1/movieoverview.aspx?mid=" + movieID).get();

                            Element posterElement = doc.getElementById("POSTER_LINK");

                            if (posterElement != null){
                                String allSource = doc.html();
                                int topH = allSource.indexOf("<h1");
                                String subSource = allSource.substring(topH);
                                int topSpan = subSource.indexOf("<span") + 1;
                                String spanSource = subSource.substring(topSpan);
                                int markStart = spanSource.indexOf(">") + 1;
                                int markEnd = spanSource.indexOf("<");
                                movieInfo = spanSource.substring(markStart, markEnd);

                                Element posterImage = posterElement.children().first();
                                String imageURL = posterImage.attr("src");

                                //todo show image to user
                                URL url = new URL(imageURL);
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                ImageView image = (ImageView) layout.findViewById(R.id.posterImage);
                                image.setImageBitmap(bmp);

                            }

                            TextView mName = (TextView) layout.findViewById(R.id.selMovieName);
                            mName.setText(movieName);

                            TextView mInfo = (TextView) layout.findViewById(R.id.selMovieInfo);
                            mInfo.setText(movieInfo);

                            // Creating the PopupWindow
                            final PopupWindow popup = new PopupWindow();
                            popup.setContentView(layout);
                            popup.setWidth(popupWidth);
                            popup.setHeight(popupHeight);
                            popup.setFocusable(true);
                            popup.setAnimationStyle(R.style.PopupWindowAnimation);

                            // Displaying the popup at the specified location, + offsets.
                            popup.showAtLocation(layout, Gravity.NO_GRAVITY, 55, 200);

                            checkSpan = (LinearLayout) layout.findViewById(R.id.checkinSpan);
                            confirmSpan = (LinearLayout) layout.findViewById(R.id.completeSpan);

                            Button close = (Button) layout.findViewById(R.id.close);
                            close.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    popup.dismiss();

                                }
                            });

                            Button fsqCheckIn = (Button) layout.findViewById(R.id.checkInButton);
                            fsqCheckIn.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    String auth_token = ExampleTokenStore.get().getToken();
                                    // Create a new HttpClient and Post Header
                                    HttpClient httpclient = new DefaultHttpClient();
                                    HttpPost httppost = new HttpPost("https://api.foursquare.com/v2/checkins/add");

                                    try {

                                        Global global = ((Global)getApplicationContext());

                                        // Add your data
                                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                                        nameValuePairs.add(new BasicNameValuePair("oauth_token", auth_token));
                                        nameValuePairs.add(new BasicNameValuePair("venueId", global.get_venue()));
                                        nameValuePairs.add(new BasicNameValuePair("eventId", global.get_event()));
                                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                                        // Execute HTTP Post Request
                                        HttpResponse response = httpclient.execute(httppost);

                                        String responseBody = EntityUtils.toString(response.getEntity());

                                        if (responseBody != null && responseBody != ""){
                                            checkSpan.setVisibility(View.GONE);
                                            confirmSpan.setVisibility(View.VISIBLE);

                                            String respMessage = "test";

                                            JSONObject  checkResp = new JSONObject(responseBody);
                                            JSONArray notifications = checkResp.getJSONArray("notifications");

                                            LinearLayout score_view = (LinearLayout) confirmSpan.findViewById(R.id.scoreList);

                                            //todo make message work
                                            for(int i = 0 ; i < notifications.length(); i++){
                                               JSONObject jo = notifications.getJSONObject(i);
                                                String mType = jo.getString("type");
                                                if (mType.equals("message")){
                                                    respMessage = jo.getJSONObject("item").getString("message");
                                                }
                                                if (mType.equals("score")){
                                                    JSONArray scoreArray = jo.getJSONObject("item").getJSONArray("scores");
                                                    for(int j = 0 ; j < scoreArray.length(); j++){
                                                        JSONObject scoreObj = scoreArray.getJSONObject(j);
                                                        String scoreMsg = scoreObj.getString("message");
                                                        String scorePoints = scoreObj.getString("points");
                                                        String scoreIcon = scoreObj.getString("icon");

                                                        LinearLayout scoreRow = new LinearLayout(v.getContext());
                                                        scoreRow.setOrientation(LinearLayout.HORIZONTAL);

                                                        TextView scoreMessage = new TextView(v.getContext());
                                                        scoreMessage.setText(scoreMsg);

                                                        TextView scoreTally = new TextView(v.getContext());
                                                        scoreTally.setText(scorePoints);

                                                        scoreRow.addView(scoreMessage, 0);
                                                        scoreRow.addView(scoreTally, 1);

                                                        score_view.addView(scoreRow);

                                                    }
                                                }
                                            }

                                            TextView tv = (TextView) confirmSpan.findViewById(R.id.respMessage);
                                            tv.setText(respMessage);
                                        }

                                    } catch (ClientProtocolException e) {
                                        // TODO Auto-generated catch block
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                    } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    }
                                }
                            });

                        }catch(Exception e){
                            Log.d("Exception",e.toString());
                        }




                    }
                });

            }
        }

    }

}