package com.menil.rottenmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
    private ProgressDialog progressDialog;
    private ListView listView;
    private Context mContext, mContext2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_search, container, false);

        listView = (ListView) view.findViewById(R.id.search_listview);
        final TextView textSearch = (TextView)view.findViewById(R.id.search_textbox);

        FloatingActionButton floatingActionButton= (FloatingActionButton) view.findViewById(R.id.search_list_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String request = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?q="+textSearch.getText().toString()+"&page_limit=50&page=1&apikey=pj2z7eyve6mfdtcx4vynk26y";
                URI requestURI = URI.create(request);
                CallAPI task = new CallAPI();
                task.execute(requestURI);
            }
        });

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
                Movies filmovi = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into filmovi
                allMovies = filmovi.movies;

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {

            listView.setAdapter(new ListsAdapter(mContext, allMovies, "BOXOFFICE"));
            progressDialog.dismiss();
        }
    }
}
