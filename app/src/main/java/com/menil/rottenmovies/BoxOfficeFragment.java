package com.menil.rottenmovies;


//import android.app.Fragment;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Created by menil on 29.10.2014.
 */
public class BoxOfficeFragment extends Fragment {//API KEY = pj2z7eyve6mfdtcx4vynk26y

    private List<Movie> allMovies = new ArrayList<Movie>();
    private Movies movies;
    private ProgressDialog progressDialog;
    private ListView listView;
    private Context mContext, mContext2;
    private String tag;
    private Boolean sort = false;
    private Boolean already_called[] = {false, false, false};
    private View view;
    //public SharedPreferences preferences;
    //public static String movie_id="id";



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_noshare, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
           // case R.id.action_settings:
           //     break;
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

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("allMovies", (Serializable) allMovies);
        super.onSaveInstanceState(outState);

    }

    private void makeActionbar() {
        String subtitle;
        try {
            switch (getArguments().getString("tag")) {
                case "BOXOFFICE":
                    subtitle = "Box Office";
                    break;
                case "OPENING":
                    subtitle = "Opening Movies";
                    break;
                case "UPCOMING":
                    subtitle = "Upcoming Movies";
                    break;
                default:
                    subtitle = "Box Office";
            }
            assert getActivity().getActionBar() != null;
            getActivity().getActionBar().show(); //this is a potential NullPointException
            getActivity().getActionBar().setDisplayShowTitleEnabled(true);
            getActivity().getActionBar().setTitle(R.string.app_name);
            getActivity().getActionBar().setSubtitle(subtitle);
            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent and also a potential NullPointException
            getActivity().getActionBar().setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //setRetainInstance(true);

        Bundle args = getArguments();

        String startURI = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/";
        String limit = "50";//max amount is 50
        String nrPages = "&page=1";
        String endURI = "&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";

        URI requestURI;
        String request;

        tag = args.getString("tag");
        int x;
        switch (tag) {
            case "BOXOFFICE":
                request = startURI + "box_office.json?limit=" + limit + endURI;
                //    getActivity().getActionBar().setSubtitle("Box Office");
                x = 0;
                break;
            case "OPENING":
                request = startURI + "opening.json?limit=" + limit + endURI;
                //   getActivity().getActionBar().setSubtitle("Opening Movies");
                x = 1;
                sort = !sort;
                break;
            case "UPCOMING":
                request = startURI + "upcoming.json?page_limit=" + limit + nrPages + endURI;
                //  getActivity().getActionBar().setSubtitle("Upcoming Movies");
                x = 2;
                sort = !sort;
                break;
            default:
                request = startURI + "box_office.json?limit=" + limit + endURI;
                //  getActivity().getActionBar().setSubtitle("Box Office");
                x = 0;
        }
        requestURI = URI.create(request);
        if (!already_called[x]) {
            CallAPI task = new CallAPI();
            task.execute(requestURI);
            already_called[x] = true;
            //thread for getting data from the API only if it's not done once
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
            view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
            listView = (ListView) view.findViewById(R.id.boxoffice_list);
        } catch (InflateException e) {
            e.printStackTrace();
        }


        mContext = getActivity().getApplicationContext();
        mContext2 = view.getContext();


        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.boxoffice_list_fab);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.smoothScrollToPosition(0);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //this only gives a small delay
                    }
                }, 1000);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
                detailsIntent.putExtra("movie", allMovies.get(position));
                startActivity(detailsIntent);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        makeActionbar();
    }

    public class CallAPI extends AsyncTask<URI, String, List<Movie>> {


        @Override
        protected void onPreExecute() {


            progressDialog = new ProgressDialog(getActivity());//, R.style.CustomDialog);
            progressDialog.setTitle("Loading...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Loading Movies, please wait...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //This dialog isn't indeterminate
            progressDialog.setIndeterminate(false);
            progressDialog.setIndeterminateDrawable(getResources()
                    .getDrawable(R.drawable.spinner_animation));
            //The maximum number of items is 100
            progressDialog.setMax(100);
            //Set the current progress to zero
            progressDialog.setProgress(0);
            //Display the progress dialog
            progressDialog.show();
        }

        @Override
        protected List<Movie> doInBackground(URI... urls) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            URI requestURI = urls[0];
            HttpGet httppost = new HttpGet(String.valueOf(requestURI));

            httppost.setHeader("Content-type", "text/javascript;charset=ISO-8859-1");

            InputStream inputStream = null;
            String result = null;

            BufferedReader reader;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception squish) {
                    squish.printStackTrace();
                }
            }

            JSONObject jsonObject;
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                Movies filmovi = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into filmovi
                movies = filmovi;
                allMovies = filmovi.getMovies();

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {
            if (sort) {
                //MovieComparator mc = null;
                Collections.sort(allMovies);
            }
            if (listView != null)
            listView.setAdapter(new ListsAdapter(mContext, allMovies, tag));
            progressDialog.dismiss();
        }
    }
}

