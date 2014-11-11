package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/*
 * Created by menil on 27.10.2014.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            //actionBar.setDisplayOptions(actionBar.getDisplayOptions() ^ ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setSubtitle("Box Office");
            actionBar.setSubtitle(null);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Button homeButton = (Button) view.findViewById(R.id.home_button);
        //only for testing crash reports
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String crash = null;
                crash.length();
            }
        });
        return view;
    }
}
