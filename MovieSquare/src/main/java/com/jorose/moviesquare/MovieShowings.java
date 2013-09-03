package com.jorose.moviesquare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    String selMovieName;
    String selMovieID;
    GetMovieShowings movieList;

    private Point p;

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

                        selMovieName = hm.get("name").toString();
                        selMovieID = hm.get("id").toString();
                        if (p !=null)
                            showPopup(MovieShowings.this, p);


                    }
                });

            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        int[] location = new int[2];
        ListView mList = (ListView) findViewById(R.id.movieShowingsList);

        mList.getLocationOnScreen(location);
        // Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];

    }

    private void showPopup(final Activity context, Point p) {

        Rect rectgle= new Rect();
        Window window= getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        int StatusBarHeight= rectgle.top;
        int contentViewTop=
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int TitleBarHeight= contentViewTop - StatusBarHeight;
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int popupWidth = display.getWidth();
        int popupHeight = (display.getHeight()-StatusBarHeight);

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context
                .findViewById(R.id.popupLinearLayout);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.activity_movie_info, viewGroup);

        BuildMovieInfo mInfo = new BuildMovieInfo();
        mInfo.GetMovie(layout,selMovieName, "test");

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        popup.setAnimationStyle(R.style.PopupWindowAnimation);

        // Some offset to align the popup a bit to the right, and a bit down,
        // relative to button's position.

        int OFFSET_X = 0;
        int OFFSET_Y = 0;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y
                + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when
        // clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

    }

}