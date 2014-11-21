package com.menil.rottenmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.List;

/**
 * Created by menil on 19.11.2014.
 */
public class ListFavouriteAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> favouritesList = new ArrayList<Movie>();
    private Movie current;
    private ProgressDialog progressDialog;

    public ListFavouriteAdapter(Context context, List<Movie> favouritesList, String tag) {
        this.mContext = context;
        this.favouritesList = favouritesList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        LayoutInflater layoutInflater;
        if (convertView == null) {

            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_full_card, null);
        } else {
            view = convertView;
        }
        URI requestURI = URI.create("http://api.rottentomatoes.com/api/public/v1.0/movies/"+favouritesList.get(position)+".json?apikey=pj2z7eyve6mfdtcx4vynk26y");
        /*ImageView imageView = (ImageView) view.findViewById(R.id.fragment_list_item_img);
        TextView titleView = (TextView) view.findViewById(R.id.fragment_list_item_title);
        TextView runtimeView = (TextView) view.findViewById(R.id.fragment_list_item_runtime);
        TextView releaseView = (TextView) view.findViewById(R.id.fragment_list_item_date);
        TextView rating = (TextView) view.findViewById(R.id.fragment_list_item_rating);*/
        return view;
    }

}


