package com.menil.rottenmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by menil on 29.10.2014.
 */
public class BoxOfficeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        ListView listView = (ListView) view.findViewById(R.id.boxoffice_list);
        //listView.setAdapter();
        return view;
    }
}

