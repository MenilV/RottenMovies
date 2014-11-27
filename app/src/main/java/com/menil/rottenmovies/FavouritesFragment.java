package com.menil.rottenmovies;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by menil on 13.11.2014.
 */
public class FavouritesFragment extends Fragment {
    public SharedPreferences preferences;
    public static final String movie_id = "id";
    private List<Movie> favouritesList=new ArrayList<Movie>();
    private Boolean isDeleted=false;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//API KEY = pj2z7eyve6mfdtcx4vynk26y
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        makeActionbar();
        preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);

        if(isDeleted) {
            preferences.edit().clear().apply();
            isDeleted=false;
        }

        Gson gson = new Gson();
        final String moviesJson = preferences.getString(movie_id, "");
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(moviesJson);
            Movies movies = gson.fromJson(jsonObject.toString(), Movies.class);
            favouritesList = movies.getMovies();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (view == null)
            view = inflater.inflate(R.layout.fragment_favourites, container, false);


        GridView gridview = (GridView) view.findViewById(R.id.fragment_favourites_list);
        String tag = "FAVOURITES";

            gridview.setAdapter(new GridAdapter(getActivity().getApplicationContext(), favouritesList));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle args = new Bundle();
                    args.putParcelable("movie", favouritesList.get(position));

                    Fragment fragment = new DetailsFragment();
                    fragment.setArguments(args);
                    switchFragment(fragment);
                }

                private void switchFragment(Fragment fragment) {
                    if (view.getContext() == null) {
                        return;
                    }
                    if (view.getContext() instanceof Main) {
                        Main main = (Main) view.getContext();
                        main.switchContent(fragment, "DETAILS");
                    }
                }
            });

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.favourites_list_fab);
        floatingActionButton.attachToListView(gridview);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                // set title
                alertDialogBuilder.setTitle("Delete all from favourites?");

                // set dialog message
                AlertDialog.Builder builder = alertDialogBuilder
                        .setIcon(R.drawable.ic_action_warning)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, delete all favourites
                                isDeleted=true;
                                Toast.makeText(view.getContext(),"Upon next opening of the app, favourites will be cleared",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close the dialog box
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }

        });
        return view;

    }

}
