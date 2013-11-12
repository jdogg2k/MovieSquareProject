package com.jorose.moviesquare;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

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
import java.util.Locale;
import java.util.Map;


public class MainActivity extends Activity {

    public final static String SELECTED_VENUE_ID = "com.jorose.moviesquare.VENUE_ID";
    public final static String SELECTED_VENUE_NAME = "com.jorose.moviesquare.VENUE_NAME";
    String jsonResult;
    GetChildList childList;
    String selVenueID;
    String selVenueName;

    private static final int REQUEST_CODE_FSQ_CONNECT = 200;
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;

    private String[] mMenuTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private FrameLayout frame;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private LayoutInflater inflater;

    private static final LatLng SYDNEY = new LatLng(-33.88,151.21);
    private static final LatLng MOUNTAIN_VIEW = new LatLng(37.4, -122.1);

    private GoogleMap mMap;
    private int menuPos = 0;


    /**
     * Obtain your client id and secret from:
     * https://foursquare.com/developers/apps
     */
    public static final String CLIENT_ID = "ZAJDKMJ13CTC25VCZFYTMQCV3YMMZXSQFUZIOEMUOVVRFQGG";
    public static final String CLIENT_SECRET = "43T3FFM5KWHRXUP1M0BJVUV3ZJNAIYC2WVECDUJJJRHAG2EZ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //todo - TAKE THIS OFF AND DO background processing properly
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mMenuTitles = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        frame = (FrameLayout) findViewById(R.id.content_frame);

        inflater = getLayoutInflater();

        //FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);
        //inflater.inflate(R.layout.venue_list, frame);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0, this.findViewById(R.id.content_frame));
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position, view);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position, View v) {

        frame.removeAllViews();
        int tView = R.layout.venue_list;

        if (position == 1){


        }
        if (position == 2){
            tView = R.layout.movie_map;

           /* MapFragment mMapFragment = MapFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.content_frame, mMapFragment);
            fragmentTransaction.commit();

           /* map = mMapFragment.getMap();

            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));*/

        }

        inflater.inflate(tView, frame);

        if (position == 2){
            setUpMapIfNeeded();
        }
        if (position == 0) {
            ensureUi();
            childList = new GetChildList();
            childList.execute();
        }

        menuPos = position;

        /*if (position == 2){
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, 15));
        }*/

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mMenuTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            FragmentManager fm = getFragmentManager();
            mMap = ((MapFragment) fm.findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                launchMap();
            } else {
                setUpMapIfNeeded();
            }
        }
    }

    private void launchMap() {
        MySQLiteHelper db = new MySQLiteHelper(frame.getContext());
        ListView lv = (ListView) frame.findViewById(R.id.myMovieView);
        List<Movie> movies = db.getAllMovies();
        for (int i=0; i<movies.size(); i++) {
            Movie m = (Movie) movies.get(i);
            String mName = m.getTitle();
        }
        mMap.setMyLocationEnabled(true);
        

    }

    @Override
    public void setTitle(CharSequence title) {
       mTitle = title;
       getActionBar().setTitle(mTitle);
    }





    private class GetChildList extends AsyncTask<String, Void, String>{

        private String currentDateandTime = "20130715";  //yyyymmdd

        @Override
        protected String doInBackground(String... params) {
            // TODO Check for location via GPS OR WIFI

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            String latLong = Double.toString(latitude) + "," + Double.toString(longitude);

            DefaultHttpClient httpclient = new DefaultHttpClient();
            final HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
            HttpConnectionParams.setSoTimeout(httpParams, 30000);

            HttpGet httppost = new HttpGet("https://api.foursquare.com/v2/venues/search?intent=checkin&ll="+latLong+"&client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v="+currentDateandTime+"&categoryId=4bf58dd8d48988d17f941735&limit=20"); //

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
                String[] from = {"name","location","checkIn"};

                /** Ids of views in listview_layout */
                int[] to = { R.id.venue_name,R.id.venue_location,R.id.checkInCount};

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
                final ListView listView = (ListView) findViewById(R.id.venueList);

                listView.setBackgroundColor(Color.rgb(223, 223, 224));

                /** Setting the adapter containing the country list to listview */
                listView.setAdapter(adapter);

                listView.setClickable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        HashMap hm = (HashMap) listView.getItemAtPosition(position);

                        selVenueID = hm.get("id").toString();
                        selVenueName = hm.get("name").toString();
                        showMovies(listView);

                    }
                });

            }
        }

    }

    /** Called when the user clicks the Send button */
    public void showMovies(View view) {
        Global global = ((Global)getApplicationContext());
        Intent intent = new Intent(this, MovieShowings.class);
        intent.putExtra(SELECTED_VENUE_ID, selVenueID);
        intent.putExtra(SELECTED_VENUE_NAME, selVenueName);
        global.set_venue(selVenueID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void ensureUi() {

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String tToken = settings.getString("4sqToken", "none");

        if (tToken != "none") {
            ExampleTokenStore.get().setToken(tToken);
        }

        boolean isAuthorized = !TextUtils.isEmpty(ExampleTokenStore.get().getToken());

        if (!isAuthorized){
            Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, CLIENT_ID);

            // If the device does not have the Foursquare app installed, we'd
            // get an intent back that would open the Play Store for download.
            // Otherwise we start the auth flow.
            if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                toastMessage(MainActivity.this, getString(R.string.app_not_installed_message));
                startActivity(intent);
            } else {
                startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
            }
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("4sqToken", ExampleTokenStore.get().getToken().toString());
            editor.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT:
                onCompleteConnect(resultCode, data);
                break;

            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
                onCompleteTokenExchange(resultCode, data);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCompleteConnect(int resultCode, Intent data) {
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();

        if (exception == null) {
            // Success.
            String code = codeResponse.getCode();
            performTokenExchange(code);

        } else {
            if (exception instanceof FoursquareCancelException) {
                // Cancel.
                toastMessage(this, "Canceled");

            } else if (exception instanceof FoursquareDenyException) {
                // Deny.
                toastMessage(this, "Denied");

            } else if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else if (exception instanceof FoursquareUnsupportedVersionException) {
                // Unsupported Fourquare app version on the device.
                toastError(this, exception);

            } else if (exception instanceof FoursquareInvalidRequestException) {
                // Invalid request.
                toastError(this, exception);

            } else {
                // Error.
                toastError(this, exception);
            }
        }
    }

    private void onCompleteTokenExchange(int resultCode, Intent data) {
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();

        if (exception == null) {
            String accessToken = tokenResponse.getAccessToken();
            // Success.
            //toastMessage(this, "Access token: " + accessToken);

            // Persist the token for later use. In this example, we save
            // it to shared prefs.
            ExampleTokenStore.get().setToken(accessToken);

            // Refresh UI.
            ensureUi();

        } else {
            if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = ((FoursquareOAuthException) exception).getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                toastMessage(this, errorMessage + " [" + errorCode + "]");

            } else {
                // Other exception type.
                toastError(this, exception);
            }
        }
    }

    private void performTokenExchange(String code) {
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID, CLIENT_SECRET, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

}
