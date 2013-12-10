package com.jorose.moviesquare;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by jrose on 12/10/13.
 */
public class Prefs extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
