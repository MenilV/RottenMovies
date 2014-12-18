package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import android.app.Fragment;


/*
 * Created by menil on 27.10.2014.
 */
public class HomeFragment extends Fragment {

    public static final String RECENT_ARE_HERE = "recentAreHere";
    public static final String FAVS_ARE_HERE = "favsAreHere";
    public static final String movie_id = "id";
    public static final String recent_id = "recent_id";
    private static final String TAG = "HOME";
    private View view;
    private List<Movie> movieFavs = new ArrayList<>();
    private List<Movie> movieRecents = new ArrayList<>();
    private TwoWayView listview_favourite;
    private TwoWayView listview_recent;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        makeActionbar();
        getAndDraw(recent_id, listview_recent);
        getAndDraw(movie_id, listview_favourite);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_about:
                AboutFragment fragment = new AboutFragment();
                if (view.getContext() instanceof Main) {
                    Main main = (Main) view.getContext();
                    main.switchContent(fragment, "ABOUT");
                }
                break;
            case R.id.action_exit:
                getActivity().finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void makeActionbar() {
        ActionBar actionBar = getActivity().getActionBar();
        try {
            assert actionBar != null;
            if (!actionBar.isShowing())
                actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle("Home");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            actionBar.setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAndDraw(recent_id, listview_recent);
        getAndDraw(movie_id, listview_favourite);
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    public void getAndDraw(String preferenceID, TwoWayView listview) {

        SharedPreferences preferences;
        if (preferenceID.equals("recent_id"))
            preferences = getActivity().getSharedPreferences(RECENT_ARE_HERE, Context.MODE_PRIVATE);
        else
            preferences = getActivity().getSharedPreferences(FAVS_ARE_HERE, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String moviesJson = preferences.getString(preferenceID, "");
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(moviesJson);
            Movies movies = gson.fromJson(jsonObject.toString(), Movies.class);
            if (preferenceID.equals("recent_id"))
                movieRecents = movies.getMovies();
            else
                movieFavs = movies.getMovies();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (preferenceID.equals("recent_id")) {
            Collections.reverse(movieRecents);
            listview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), movieRecents, true));
        } else {
            Collections.reverse(movieFavs);
            listview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), movieFavs, true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {
            view = inflater.inflate(R.layout.fragment_home, container, false);

            listview_recent = (TwoWayView) view.findViewById(R.id.listview_recent);
            listview_favourite = (TwoWayView) view.findViewById(R.id.listview_favourite);


            getAndDraw(movie_id, listview_favourite);
            getAndDraw(recent_id, listview_recent);
        } catch (InflateException e) {
            e.printStackTrace();
        }


        listview_favourite.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra("movie", movieFavs.get(position));
                startActivity(detailsIntent);
            }
        });

        listview_recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra("movie", movieRecents.get(position));
                startActivity(detailsIntent);
            }
        });

        return view;
    }

}
