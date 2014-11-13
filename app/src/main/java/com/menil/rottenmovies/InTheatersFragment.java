package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
 * Created by menil on 08.10.2014.
 */
public class InTheatersFragment extends android.app.Fragment {

    public List<Movie> allMovies = new ArrayList<Movie>();
    private ProgressDialog progressDialog;
    private GridView gridView;
    private View view;
    private Context mContext, mContext2;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//API KEY =pj2z7eyve6mfdtcx4vynk26y
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
            view = inflater.inflate(R.layout.fragment_intheaters, container, false);

        mContext = getActivity().getApplicationContext();

        Bundle args = getArguments();
        mContext2 = view.getContext();

        String startURI = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/";
        String limit = "50";//max amount is 50
        String nrPages = "&page=1";
        String endURI = "&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";

        CallAPI task = new CallAPI();
        URI requestURI=URI.create(startURI + "in_theaters.json?page_limit=" + limit + nrPages + endURI);
        task.execute(requestURI);
        //thread for getting data from the API
        try {
            ActionBar actionbar = getActivity().getActionBar();
            actionbar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            actionbar.setSubtitle("In Theaters");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        gridView = (GridView) view.findViewById(R.id.others_gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putParcelable("movie", allMovies.get(position));
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
                    main.switchContent(fragment, "INTHEATERS");
                }
            }
        });

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.others_gridview_list_fab);
        floatingActionButton.attachToListView(gridView);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionButton.getColorNormal() == getResources().getColor(R.color.green)) {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                    floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                    gridView.smoothScrollToPosition(0);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //this only gives a small delay
                        }
                    }, 1000);
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                    floatingActionButton.setImageResource(R.drawable.ic_action_up);
                }
            }
        });

        return view;
    }

    public class CallAPI extends AsyncTask<URI, String, List<Movie>> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(getActivity(), R.style.CustomDialog);
            progressDialog.setTitle("Loading...");
            //Set the dialog message to 'Loading application View, please wait...'
            progressDialog.setMessage("Loading Movies, please wait...");
            //This dialog can't be canceled by pressing the back key
            progressDialog.setCancelable(false);
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
                Movies filmovi = new Movies();
                filmovi = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into filmovi
                allMovies = filmovi.movies;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {
            gridView.setAdapter(new GridAdapter(mContext, allMovies));
            progressDialog.dismiss();
        }
    }
}