package com.menil.rottenmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/*
 * Created by menil on 27.10.2014.
 */
public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Toast.makeText(this.getActivity().getApplicationContext(),"You are on the Home screen", Toast.LENGTH_LONG).show();
        Button homeButton = (Button) view.findViewById(R.id.home_button);
        //Button shareButton = (Button) view.findViewById(R.id.home_share_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crash=null;
                crash.length();
            }
        });
        /*shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        return view;
    }
}
