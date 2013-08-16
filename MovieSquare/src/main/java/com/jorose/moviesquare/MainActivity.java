package com.jorose.moviesquare;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import com.jorose.moviesquare.Foursquare.DialogListener;

import android.app.Activity;
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
import android.widget.Toast;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.http.client.methods.HttpGet;
import java.io.BufferedReader;

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

                JSONObject fourResults = new JSONObject(jsonResult);
                String test = "test";
                //fourResults.
            }
            catch(ConnectTimeoutException e){
                Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show();
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
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



    }
}
