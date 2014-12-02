package com.menil.rottenmovies;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
public class SearchActivity extends Activity {

    public List<Movie> allMovies = new ArrayList<Movie>();
    private View view;
    private ProgressDialog progressDialog;
    private ListView listView;
    private TextView noMoviesTextView;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        //super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        noMoviesTextView = (TextView) findViewById(R.id.search_textbox);
        listView = (ListView) findViewById(R.id.search_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putParcelable("movie", allMovies.get(position));

                Fragment fragment = new DetailsFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            }

            private void switchFragment(Fragment fragment) {
                if (getApplicationContext() == null) {
                    return;
                }
                if (getApplicationContext() instanceof Main) {
                    Main main = (Main) view.getContext();
                    main.switchContent(fragment, "DETAILS");
                }
            }
        });
        // Get the intent, verify the action and get the query
        //Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }


        /*FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.search_list_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });*/
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
