package com.menil.rottenmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by menil on 08.10.2014.
 */
public class FavouritesFragment extends android.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        //bundle.get
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }
}
