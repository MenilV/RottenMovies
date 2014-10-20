package com.menil.rottenmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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


    View view;
    GridView gridView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //API KEY =pj2z7eyve6mfdtcx4vynk26y
        Bundle args = getArguments();

        List<URI> requestURI = new ArrayList<URI>();
        int option = args.getInt("position", 0);
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?page_limit=16&page=1&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/opening.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/upcoming.json?page_limit=16&page=1&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        //Upcoming Movies URI (different JSON)

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);

        //gridView.setAdapter(new ImageAdapter(view.getContext(),allMovies));
        CallAPI task = new CallAPI(getActivity());
        task.execute(requestURI.get(option));
        //thread for getting data from the API


        return view;
    }

    public class CallAPI extends AsyncTask<URI, String, List<Movie>> {

        private Context context;

        public CallAPI(Context c) {
            this.context = c;

        }

        @Override
        protected List<Movie> doInBackground(URI... urls) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            URI requestURI = urls[0];
            HttpGet httppost = new HttpGet(String.valueOf(requestURI));

            httppost.setHeader("Content-type", "text/javascript;charset=ISO-8859-1");

            InputStream inputStream = null;
            String result = null;

            BufferedReader reader = null;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
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

            JSONObject jsonObject = null;
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                Movies filmovi = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into filmovi


                allMovies = filmovi.movies;
                result = allMovies.get(0).title;

                for (Movie m : allMovies) {
                    result = m.id;
                }

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {

            gridView.setAdapter(new ImageAdapter(view.getContext(), allMovies));
            // Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }

    }

}


