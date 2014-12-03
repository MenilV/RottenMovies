package com.menil.rottenmovies;
/**
 * DEPRECATED CODE
 */

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
 * Created by menil on 18.11.2014.
 */
public class SearchFragment extends Fragment {//API KEY = pj2z7eyve6mfdtcx4vynk26y

    public List<Movie> allMovies = new ArrayList<Movie>();
    private View view;
    private ProgressDialog progressDialog;
    private ListView listView;
    private Context mContext;
    private TextView noMoviesTextView;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//API KEY = pj2z7eyve6mfdtcx4vynk26y
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        makeActionbar();
    }

    private void makeActionbar() {
        try {
            SearchView searchView = (SearchView) view.findViewById(R.id.search_textbox);
            assert getActivity().getActionBar() != null;
            getActivity().getActionBar().show();
            getActivity().getActionBar().setDisplayShowTitleEnabled(true);
            getActivity().getActionBar().setTitle(R.string.app_name);
            getActivity().getActionBar().setSubtitle("Search");
            getActivity().getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            getActivity().getActionBar().setIcon(R.drawable.actionbar_icon);
            getActivity().getActionBar().setCustomView(searchView);
            //getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //searchView.setQuery("test",true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
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
        setRetainInstance(true);
    }

    public void performSearch(String editText) {

        if (editText.length() > 0) {
            String request = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=" + editText.replace(" ", "+") + "&page_limit=50&page=1&apikey=pj2z7eyve6mfdtcx4vynk26y";
            URI requestURI = URI.create(request);
            CallAPI task = new CallAPI();
            task.execute(requestURI);
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Enter a search query", Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = getActivity().getApplicationContext();
        listView = (ListView) view.findViewById(R.id.search_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putSerializable("movie", allMovies.get(position));

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

        /**
         * HERE STARTS THE SEARCH
         */
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_textbox);

        noMoviesTextView = (TextView) view.findViewById(R.id.search_no_movies);
        //searchView.focus

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something when user his enter on keyboard
                performSearch(query);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                view.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Do something while user is entering text
                return false;
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.search_list_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(searchView.getQuery().toString());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        /**
         * HERE ENDS THE SEARCH
         */
        return view;
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
                noMoviesTextView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                noMoviesTextView.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(new ListsAdapter(mContext, allMovies, "SEARCH"));
            }
            progressDialog.dismiss();
        }
    }
}
