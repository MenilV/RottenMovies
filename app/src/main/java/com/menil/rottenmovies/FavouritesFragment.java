package com.menil.rottenmovies;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;

/*
 * Created by menil on 13.11.2014.
 */
public class FavouritesFragment extends Fragment {

    public static final String TAG = "FAVOURITES";
    public static final String movie_id = "id";
    public SharedPreferences preferences;
    private List<Movie> favouritesList = new ArrayList<Movie>();
    private View view;
    private UiLifecycleHelper uiHelper;
    private GridView gridview;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favourites, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
           // case R.id.action_settings:
           //     break;
            case R.id.action_share:
                AlertDialog dialog;
                final List<String> favouriteListTitles = new ArrayList<String>();
                for (Movie m : favouritesList)
                    favouriteListTitles.add(m.title);
                final CharSequence[] items = favouriteListTitles.toArray(new CharSequence[favouriteListTitles.size()]);
                final ArrayList selectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Share your favourite movies");
                builder.setIcon(R.drawable.ic_launcher_16);
                builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, adds it to the selected items
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                        // Set the action buttons
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                shareToFB(favouriteListTitles, selectedItems);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Select all", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int i = 0;
                                for (String s : favouriteListTitles)
                                    selectedItems.add(i++);
                                shareToFB(favouriteListTitles, selectedItems);
                            }

                        });

                dialog = builder.create();//AlertDialog dialog; create like this outside onClick
                dialog.show();
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


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        refreshView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActionbar();
        refreshView();
        uiHelper = new UiLifecycleHelper(getActivity(), null);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//API KEY = pj2z7eyve6mfdtcx4vynk26y
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    public void makeActionbar() {
        ActionBar actionBar = getActivity().getActionBar();
        try {
            assert actionBar != null;
            actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle("Favourites");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            actionBar.setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void refreshView() {
        preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String moviesJson = preferences.getString(movie_id, "");
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(moviesJson);
            Movies movies = gson.fromJson(jsonObject.toString(), Movies.class);
            favouritesList = movies.getMovies();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Fragment frg = null;

        //frg=this;
        frg = getFragmentManager().findFragmentByTag("FAVOURITES");
        getFragmentManager().beginTransaction().detach(frg);
        getFragmentManager().beginTransaction().attach(frg);
        getFragmentManager().beginTransaction().commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Boolean mAlreadyLoaded = false;
        if (savedInstanceState == null && !mAlreadyLoaded) {
            {
                mAlreadyLoaded = true;

                // refreshView();
            }
            // Do this code only first time, not after rotation or reuse fragment from backstack
        }
    }

    protected void shareToFB(List<String> selectedMovies, ArrayList<Integer> indexSelected) {

        String list = "";
        int count = 0;
        for (Integer i : indexSelected)
            list += ++count + ". " + selectedMovies.get(i) + "\n";

        if (Session.getActiveSession().isOpened())
        //logged in to facebook
        {
            if (FacebookDialog.canPresentShareDialog(getActivity().getApplicationContext())) {

                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(getActivity())
                        .setLink("https://imgur.com/OHxpbca")//this is 128x128, replace with link to app
                        .setName("My favourite movies:")
                        .setCaption("Shared with: Rotten Movies")
                        .setDescription(list)
                        .setPicture("https://i.imgur.com/xNzJGq1.png")//this is 512x512
                        .build();
                uiHelper.trackPendingDialogCall(shareDialog.present());
            } else {
                //using this if there is no Facebook application installed
                Bundle params = new Bundle();
                params.putString("name", "My favourite movies:");
                params.putString("caption", "Shared with: Rotten Movies");
                params.putString("description", list);
                params.putString("link", "https://imgur.com/OHxpbca");// this is 128x128, replace with link to app
                params.putString("picture", "https://i.imgur.com/xNzJGq1.png");//this is 512x512

                WebDialog feedDialog = (
                        new WebDialog.FeedDialogBuilder(getActivity(),
                                Session.getActiveSession(),
                                params))
                        .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                            @Override
                            public void onComplete(Bundle values,
                                                   FacebookException error) {
                                if (error == null) {
                                    // When the story is posted, echo the success
                                    // and the post Id.
                                    final String postId = values.getString("post_id");
                                    if (postId != null) {
                                        Toast.makeText(getActivity(),
                                                "Posted story, id: " + postId,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // User clicked the Cancel button
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Publish cancelled",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else if (error instanceof FacebookOperationCanceledException) {
                                    // User clicked the "x" button
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Publish cancelled",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Generic, ex: network error
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Error posting story",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        })
                        .build();
                feedDialog.show();

            }
        } else {
            //not logged in to facebook
            Toast.makeText(getActivity().getApplicationContext(), "Please go to the About page and login with your Facebook account", Toast.LENGTH_LONG).show();
            //UserSettingsFragment userSettingsFragment= new UserSettingsFragment();
            //TODO: make a login screen if the user wants to login right away
            //getActivity().getSupportFragmentManager().beginTransaction().add(userSettingsFragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        try {

            view = inflater.inflate(R.layout.fragment_favourites, container, false);
            gridview = (GridView) view.findViewById(R.id.fragment_favourites_list);
        } catch (InflateException e) {
            e.printStackTrace();
        }


        //   refreshView();

        gridview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), favouritesList));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra("movie", favouritesList.get(position));
                startActivity(detailsIntent);
            }
        });

        final FloatingActionButton floatingActionButtonShare = (FloatingActionButton) view.findViewById(R.id.favourites_list_fab);
        floatingActionButtonShare.attachToListView(gridview);
        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog;
                final List<String> favouriteListTitles = new ArrayList<String>();
                for (Movie m : favouritesList)
                    favouriteListTitles.add(m.title);
                final CharSequence[] items = favouriteListTitles.toArray(new CharSequence[favouriteListTitles.size()]);
                final ArrayList selectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Share your favourite movies");
                builder.setIcon(R.drawable.ic_launcher_16);
                builder.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, adds it to the selected items
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                        // Set the action buttons
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                shareToFB(favouriteListTitles, selectedItems);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Select all", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int i = 0;
                                for (String s : favouriteListTitles)
                                    selectedItems.add(i++);
                                shareToFB(favouriteListTitles, selectedItems);
                            }

                        });

                dialog = builder.create();//AlertDialog dialog; create like this outside onClick
                dialog.show();
            }
        });
        return view;
    }
}
