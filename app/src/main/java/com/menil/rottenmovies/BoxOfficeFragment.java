package com.menil.rottenmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
 * Created by menil on 08.10.2014.
 */
public class BoxOfficeFragment extends android.app.Fragment {

    public List<Movie> allMovies = new ArrayList<Movie>();

    int option = 0;
    View view;
    GridView gridView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //API KEY =pj2z7eyve6mfdtcx4vynk26y
        Bundle args = getArguments();

        List<URI> requestURI = new ArrayList<URI>();
        option = args.getInt("position");
        if (option > 4)//prevent coming to favourites fragment
            option = 4;
        /*
        Box Office
        In Theaters
        Opening movies
        Upcoming movies
         */
        String[] uriTopics = {"box_office", "in_theaters", "opening", "upcoming"};
        String startURI = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/";
        String endURI = ".json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";
        for (String topic : uriTopics)
            requestURI.add(URI.create(startURI + topic + endURI));
        //Upcoming Movies URI (different JSON)

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);

        CallAPI task = new CallAPI();
        task.execute(requestURI.get(option));
        //thread for getting data from the API

        return view;
    }

    public class CallAPI extends AsyncTask<URI, String, List<Movie>> {

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
                // Oops
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
            gridView.setAdapter(new ImageAdapter(view.getContext(), allMovies));
        }

    }

}


