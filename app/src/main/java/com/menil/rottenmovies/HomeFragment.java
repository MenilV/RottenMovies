package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/*
 * Created by menil on 27.10.2014.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";
    private View view;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        makeActionbar();
    }

    private void makeActionbar(){

        try{
            assert getActivity().getActionBar() != null;
            getActivity().getActionBar().show();
            getActivity().getActionBar().setDisplayShowTitleEnabled(true);
            getActivity().getActionBar().setTitle(R.string.app_name);
            getActivity().getActionBar().setSubtitle("Home");
            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            getActivity().getActionBar().setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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

        if (view == null)
            view = inflater.inflate(R.layout.fragment_home, container, false);

        assert view != null;
        ImageView AtlantImage = (ImageView) view.findViewById(R.id.fragment_home_abh_link);
        AtlantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://www.atlantbh.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });
        ImageView RottenImage = (ImageView) view.findViewById(R.id.fragment_home_rtn_link);
        RottenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://www.rottentomatoes.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        ImageView IMDBImage = (ImageView) view.findViewById(R.id.fragment_home_imdb_link);
        IMDBImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://www.imdb.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }

}
