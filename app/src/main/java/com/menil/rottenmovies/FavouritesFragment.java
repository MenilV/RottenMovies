package com.menil.rottenmovies;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Created by menil on 13.11.2014.
 */
public class FavouritesFragment extends PreferenceFragment{
    SharedPreferences sh_Pref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
       // bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_favourites, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", "771355766");
        editor.apply();
        return view;

    }
}
