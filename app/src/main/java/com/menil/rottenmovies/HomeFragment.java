package com.menil.rottenmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/*
 * Created by menil on 27.10.2014.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";
    private View view;
    private List<Movie> movieFavs = new ArrayList<Movie>();
    private List<Movie> movieRecents = new ArrayList<Movie>();
    private Context mContext2;
    private String movie_id = "id";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        makeActionbar();
    }

    private void makeActionbar() {

        try {
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

    public void getAndDraw(String preferenceID, HorizontialListView listview) {

        SharedPreferences preferences = getActivity().getSharedPreferences(preferenceID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String moviesJson = preferences.getString(movie_id, "");
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(moviesJson);
            Movies movies = gson.fromJson(jsonObject.toString(), Movies.class);
            if (preferenceID.equals("recentAreHere"))
                movieRecents = movies.getMovies();
            else
                movieFavs = movies.getMovies();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (preferenceID.equals("recentAreHere")) {
            Collections.reverse(movieRecents);
            listview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), movieRecents, true));
        } else
            listview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), movieFavs, true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.fragment_home, container, false);

        mContext2 = view.getContext();
        HorizontialListView listview_favourite = (HorizontialListView) view.findViewById(R.id.listview_favourite);
        HorizontialListView listview_recent = (HorizontialListView) view.findViewById(R.id.listview_recent);
        getAndDraw("recentAreHere", listview_recent);
        getAndDraw("favsAreHere", listview_favourite);

        listview_favourite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putParcelable("movie", movieFavs.get(position));
                Fragment fragment = new DetailsFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            }

            private void switchFragment(Fragment fragment) {
                if (mContext2 == null) {
                    return;
                }
                if (mContext2 instanceof Main) {
                    Main main = (Main) mContext2;
                    main.switchContent(fragment, "DETAILS");
                }
            }
        });
        listview_recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putParcelable("movie", movieRecents.get(position));
                Fragment fragment = new DetailsFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            }

            private void switchFragment(Fragment fragment) {
                if (mContext2 == null) {
                    return;
                }
                if (mContext2 instanceof Main) {
                    Main main = (Main) mContext2;
                    main.switchContent(fragment, "DETAILS");
                }
            }
        });
        return view;
    }

}
