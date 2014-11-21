package com.menil.rottenmovies;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.List;

/*
 * Created by menil on 13.11.2014.
 */
public class FavouritesFragment extends Fragment {
    public SharedPreferences preferences;
    public static final String movie_id = "id";
    View view;
    ProgressDialog progressDialog;
    List<Movie> favouritesList=new ArrayList<Movie>();
    ListView listView;
    Context mContext;
    SharedPreferences sh_Pref;
    ImageView selectedImage;
    private Integer[] mImageIds = {
            R.drawable.ic_abh_small,
            R.drawable.ic_fb_small,
            R.drawable.ic_imdb_small,
            R.drawable.ic_rtlogo_small,
            R.drawable.ic_launcher_small,
            R.drawable.ic_tw_small
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void makeActionbar() {
        ActionBar actionBar = getActivity().getActionBar();
        try {
            assert actionBar != null;
            actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle("Favourites");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            actionBar.setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_favourites_2, container, false);
        makeActionbar();

        preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = preferences.edit();

        TextView title = (TextView) view.findViewById(R.id.favourites_gallery_title);
        //if (preferences.contains(movie_id))
        String nesto = preferences.getString(movie_id,"noMovieID");
            title.setText(nesto);
        Toast.makeText(getActivity().getApplicationContext(),preferences.getString(movie_id,"noMovieID"),Toast.LENGTH_LONG).show();

        listView = (ListView) view.findViewById(R.id.fragment_favourites_list);
        nesto = nesto.replace(nesto.substring(0,1),"");//remove the first comma
        List<String> favouritesList = new ArrayList<String>(Arrays.asList(nesto.split(",")));
        ArrayAdapter<String> favouritesAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, favouritesList);//make seperate adapter
        //listView.setAdapter(favouritesAdapter);
        String tag = "FAVOURITES";
        //listView.setAdapter(new ListsAdapter(view.getContext(), favouritesList, tag, 0));List<String> favouritesList
        return view;

    }
  /*  private void ModifyPreferences(String key, String value, int option) {
        preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SharedPreferences settings2 = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        String idFav = settings2.getString("id", "0");
        switch (option) {
            case 0://adds item to favs
                editor.putString(key, value);
                editor.apply();

                Toast.makeText(getActivity().getApplicationContext(), idFav + " added", Toast.LENGTH_LONG).show();
                break;
            case 1://deletes item from favs
                editor.remove(key);
                editor.apply();
                Toast.makeText(getActivity().getApplicationContext(), idFav+" added", Toast.LENGTH_LONG).show();
                break;
        }
    }
*/

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
                Movie film = gson.fromJson(jsonObject.toString(), Movie.class); // deserializes json into filmovi
                favouritesList.add(film);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return favouritesList;
        }

        @Override
        protected void onPostExecute(List<Movie> favouritesList) {

            String tag="FAVOURITES";
            listView.setAdapter(new ListFavouriteAdapter(mContext, favouritesList, tag));
            progressDialog.dismiss();
        }
    }
}
