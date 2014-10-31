package com.menil.rottenmovies;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

/**
 * Created by menil on 29.10.2014.
 */
public class BoxOfficeFragment extends Fragment {
    public List<Movie> allMovies = new ArrayList<Movie>();
    private ListView listView;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //API KEY =pj2z7eyve6mfdtcx4vynk26y
        View view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        listView = (ListView) view.findViewById(R.id.boxoffice_list);
        Bundle args = getArguments();
        int option = args.getInt("position");
        CallAPI task = new CallAPI();
        List<URI> requestURI = new ArrayList<URI>();
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        task.execute(requestURI.get(0));
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
                //if (urls[0].toString().contains("upcoming"))
                //String total = gson.fromJson(jsonObject.toString(), Movies.class.);
                //{ }
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
            /**PROBLEMS ARE LOCATED HERE
             *
             */

            listView.setAdapter(new ListsAdapter(view.getContext(), allMovies));
            Toast.makeText(view.getContext(),"all is fine ... NOT!", Toast.LENGTH_LONG).show();
        }
    }
}

