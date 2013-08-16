package com.jorose.moviesquare;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import com.jorose.moviesquare.Foursquare.DialogListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.os.AsyncTask;
import android.view.Menu;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.InputStream;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.http.client.methods.HttpGet;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    Foursquare foursquare;
    String jsonResult;
    GetChildList childList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //todo - TAKE THIS OFF AND DO background processing properly
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        childList = new GetChildList();
        foursquare = new Foursquare(
                "ZAJDKMJ13CTC25VCZFYTMQCV3YMMZXSQFUZIOEMUOVVRFQGG",
                "43T3FFM5KWHRXUP1M0BJVUV3ZJNAIYC2WVECDUJJJRHAG2EZ",
                "http://www.thejordanblog.com");

        foursquare.authorize(this, new FoursquareAuthenDialogListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class FoursquareAuthenDialogListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            try {
                String aa = null;
                aa = foursquare.request("users/self");
                Log.d("Foursquare-Main", aa);
                childList.doInBackground();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onFoursquareError(FoursquareError e) {
            // TODO Auto-generated method stub
            String errorMe = e.toString();
        }

        @Override
        public void onError(DialogError e) {
            // TODO Auto-generated method stub
            String errorMe = e.toString();
        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
        }

    }

    private class GetChildList extends AsyncTask<String, Void, String>{

        private String strm = "38.87,-77.23";
        private String client_id = "ZAJDKMJ13CTC25VCZFYTMQCV3YMMZXSQFUZIOEMUOVVRFQGG";
        private String client_secret = "43T3FFM5KWHRXUP1M0BJVUV3ZJNAIYC2WVECDUJJJRHAG2EZ";
        private String currentDateandTime = "20130715";  //yyyymmdd

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            DefaultHttpClient httpclient = new DefaultHttpClient();
            final HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpConnectionParams.setSoTimeout(httpParams, 30000);

            HttpGet httppost = new HttpGet("https://api.foursquare.com/v2/venues/search?intent=checkin&ll="+strm+"&client_id="+client_id+"&client_secret="+client_secret+"&v="+currentDateandTime+"&categoryId=4bf58dd8d48988d17f941735&limit=20"); //

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

                Toast.makeText(getApplicationContext(), "R E S U L T :"+jsonResult, Toast.LENGTH_LONG).show();
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
                    VenueJSONParser venueJsonParser = new VenueJSONParser();
                    venueJsonParser.parse(jObject);
                }catch(Exception e){
                    Log.d("JSON Exception1",e.toString());
                }

                VenueJSONParser venueJsonParser = new VenueJSONParser();

                List<HashMap<String, String>> venues = null;

                try{
                    /** Getting the parsed data as a List construct */
                    venues = venueJsonParser.parse(jObject);
                }catch(Exception e){
                    Log.d("Exception",e.toString());
                }

                /** Keys used in Hashmap */
                String[] from = {"name","location"};

                /** Ids of views in listview_layout */
                int[] to = { R.id.venue_name,R.id.venue_location};

                /** Instantiating an adapter to store each items
                 *  R.layout.listview_layout defines the layout of each item
                 */
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), venues, R.layout.venue_lv_layout, from, to);

                return adapter;
            }

            /** Invoked by the Android system on "doInBackground" is executed completely */
            /** This will be executed in ui thread */
            @Override
            protected void onPostExecute(SimpleAdapter adapter) {

                /** Getting a reference to listview of main.xml layout file */
                ListView listView = (ListView) findViewById(R.id.venueList);

                listView.setBackgroundColor(Color.rgb(223, 223, 224));

                /** Setting the adapter containing the country list to listview */
                listView.setAdapter(adapter);
            }
        }

    }
}
