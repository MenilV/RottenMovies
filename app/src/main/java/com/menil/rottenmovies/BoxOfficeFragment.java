package com.menil.rottenmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private ProgressDialog progressDialog;
    private ListView listView;
    //private View view;
    private Context mContext, mContext2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext=getActivity().getApplicationContext();

        //API KEY =pj2z7eyve6mfdtcx4vynk26y
        View view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        mContext2 = view.getContext();
        listView = (ListView) view.findViewById(R.id.boxoffice_list);
        //Bundle args = getArguments();
        CallAPI task = new CallAPI();
        List<URI> requestURI = new ArrayList<URI>();
        requestURI.add(URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=50&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y"));
        //limit can be changed to 10-20 for performance

        task.execute(requestURI.get(0));
        //thread for getting data from the API

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
                if (mContext2 == null){
                    Toast.makeText(getActivity().getApplicationContext(),"text",Toast.LENGTH_LONG).show();
                    return;}
                if (mContext2 instanceof Main) {
                    Toast.makeText(getActivity().getApplicationContext(),"text2",Toast.LENGTH_LONG).show();
                    Main main = (Main) mContext2;
                    main.switchContent(fragment);
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

            listView.setAdapter(new ListsAdapter(mContext, allMovies));
            progressDialog.dismiss();
        }
    }
}

