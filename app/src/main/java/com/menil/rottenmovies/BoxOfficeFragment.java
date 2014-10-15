package com.menil.rottenmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/*
 * Created by menil on 08.10.2014.
 */
public class BoxOfficeFragment extends android.app.Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        URI requestURI = URI.create("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y");




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(view.getContext()));
        /*int x=0;
        while(x<20){
        Toast.makeText(getActivity(), "text", Toast.LENGTH_SHORT).show();
        x++;}*/

        CallAPI task = new CallAPI(getActivity());
        task.execute(requestURI);
        return view;
    }

    public class CallAPI extends AsyncTask<URI, String, String> {

        private Context context;

        public CallAPI(Context c) {
            context = c;
        }

        @Override
        protected String doInBackground(URI... urls) {
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            URI requestURI = urls[0];
            //HttpPost httppost = new HttpPost(String.valueOf(requestURI));
            HttpGet httppost = new HttpGet(String.valueOf(requestURI));
            // Depends on your web service
            httppost.setHeader("Content-type", "text/javascript;charset=ISO-8859-1");

            InputStream inputStream = null;
            String result = null;
            try {
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();
                // json is UTF-8 by default
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
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
                jsonObject = new JSONObject(result);
                String imageLinks = jsonObject.getString("synopsis");
                result = imageLinks;
            } catch (JSONException e) {
                e.printStackTrace();
            }


            publishProgress(result);
            return result;
        }



        @Override
        protected void onPostExecute(String result) {
            int x=20;
            while(x-->0)
            Toast.makeText(context, "Ovo je nesto" + result, Toast.LENGTH_LONG).show();

        }


    }


}


