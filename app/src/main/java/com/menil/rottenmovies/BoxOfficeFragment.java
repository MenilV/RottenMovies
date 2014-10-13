package com.menil.rottenmovies;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by menil on 08.10.2014.
 */
public class BoxOfficeFragment extends android.app.Fragment {

/*
    private class PullBoxOffice extends AsyncTask <URI, Integer, Long>{

        @Override
        protected Long doInBackground(URI... urls) {
            DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpPost httppost = new HttpPost("http://api.rottentomatoes.com/api/public/v1.0/lists/movies/box_office.json?limit=16&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y");
            // Depends on your web service
            httppost.setHeader("Content-type", "text/javascript");
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
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                // Oops
            }
            finally {
                try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
            }

            String aJsonString=null;
            try {
                JSONObject jObject = new JSONObject(result);
                aJsonString = jObject.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RelativeLayout r = (RelativeLayout) getView().findViewById(R.id.full_card);
            TextView t = (TextView) getView().findViewById(R.id.full_card_title);
            t.setText(aJsonString);
            return null;
        }
    }*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boxoffice, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new ImageAdapter(view.getContext()));
        //return inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

}

