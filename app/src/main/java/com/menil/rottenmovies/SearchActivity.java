package com.menil.rottenmovies;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by menil on 02.12.2014.
 */
public class SearchActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public List<Movie> allMovies = new ArrayList<Movie>();
    private View view;
    private ProgressDialog progressDialog;
    private ListView listView;
    private TextView noMoviesTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // if (!mNavigationDrawerFragment.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //restoreActionBar();
        //  return true;
        // }
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        //Fragment fragment;
        android.support.v4.app.Fragment fragment;

        String tag;
        switch (position) {
            case 0://home fragment
                fragment = new HomeFragment();
                tag = "HOME";
                break;
            case 1://box office fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "BOXOFFICE";
                }
                break;
            case 2://in theaters fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new InTheatersFragment();
                    tag = "INTHEATERS";
                }
                break;
            case 3://opening fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "OPENING";
                }
                break;
            case 4://upcoming fragment
                if (!isConnectedToInternet()) {
                    fragment = new HomeFragment();
                    tag = "HOME";
                    Toast.makeText(getApplicationContext(), "No Internet connection.\nReturning to Home...", Toast.LENGTH_LONG).show();
                } else {
                    fragment = new BoxOfficeFragment();
                    tag = "UPCOMING";
                }
                break;
            case 5://favourites fragment
                fragment = new FavouritesFragment();
                tag = "FAVOURITES";
                break;
            case 6://about fragment
                fragment = new AboutFragment();
                tag = "ABOUT";
                break;
            default://Home is the default view
                fragment = new HomeFragment();
                tag = "HOME";
                break;
        }
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putString("tag", tag);
        fragment.setArguments(args);
        switchContent(fragment, tag);

    }

    public void switchContent(android.support.v4.app.Fragment fragment, String TAG) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        Boolean found = false;
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            if (fm.findFragmentByTag(TAG) == fm.findFragmentByTag(fm.getBackStackEntryAt(i).getName()))
                found = true;
        }
        if (found)
            fm.popBackStack(TAG, 0);
        else
            ft.replace(R.id.container, fragment, TAG).addToBackStack(TAG).commit();
    }

    private void makeActionbar() {

        try {
            assert getActionBar() != null;
            getActionBar().show();
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setTitle(R.string.app_name);
            getActionBar().setSubtitle("Search");
            getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            getActionBar().setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActionbar();
        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        setContentView(R.layout.activity_search);

        //noMoviesTextView = (TextView) findViewById(R.id.search_textbox);
        listView = (ListView) findViewById(R.id.search_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(SearchActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("movie", allMovies.get(position));
                startActivity(detailsIntent);
            }
        });

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }


        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.search_list_fab);
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
        /**
         * HERE ENDS THE SEARCH
         */
    }

    public void performSearch(String editText) {

        if (editText.length() > 0) {
            String request = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=" + editText.replace(" ", "+") + "&page_limit=50&page=1&apikey=pj2z7eyve6mfdtcx4vynk26y";
            URI requestURI = URI.create(request);
            CallAPI task = new CallAPI();
            task.execute(requestURI);
        } else
            Toast.makeText(getApplicationContext(), "Enter a search query", Toast.LENGTH_LONG).show();
    }

    public class CallAPI extends AsyncTask<URI, String, List<Movie>> {

        @Override
        protected void onPreExecute() {

         /*   progressDialog = new ProgressDialog(getApplicationContext());//, R.style.CustomDialog);
            progressDialog.setTitle("Loading...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Searching Movies, please wait...");
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
            progressDialog.show();*/
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
                Movies movies = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into movies
                allMovies = movies.getMovies();

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {
            if (allMovies.isEmpty()) {
                //           noMoviesTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
//                noMoviesTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(new ListsAdapter(getApplicationContext(), allMovies, "SEARCH"));
            }
            //     progressDialog.dismiss();
        }
    }
}
