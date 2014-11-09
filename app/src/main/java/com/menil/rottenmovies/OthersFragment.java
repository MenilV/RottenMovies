package com.menil.rottenmovies;

import android.app.Activity;
import android.app.ProgressDialog;
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
public class OthersFragment extends android.app.Fragment {

    public List<Movie> allMovies = new ArrayList<Movie>();
    private ProgressDialog progressDialog;
    private static final String TAG = "OTHERS";
    private GridView gridView;
    private View view;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
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

        //API KEY =pj2z7eyve6mfdtcx4vynk26y
        //search json: http://api.rottentomatoes.com/api/public/v1.0/movies.json?q=STRING&page_limit=10&page=1&apikey=pj2z7eyve6mfdtcx4vynk26y
        Bundle args = getArguments();
        List<URI> requestURI = new ArrayList<URI>();

        int option = args.getInt("position");
        option -= 2;

        //INT: page_limit=50&page=1
        //OPE: limit=16
        //UPC: page_limit=16&page=1
        String startURI = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/";
        //String[] uriTopics = {"box_office.json?", "in_theaters.json?", "opening.json?", "upcoming.json?"};
        String[] uriTopics = {"in_theaters.json?", "opening.json?", "upcoming.json?"};
        String page = "page_";//for in theaters and upcoming movies
        String limit = "limit=50";//max amount is 50
        String nrPages = "&page=1";
        String endURI = "&country=us&apikey=pj2z7eyve6mfdtcx4vynk26y";


        for (String topic : uriTopics) {
            if (topic.equals("opening.json?"))
                requestURI.add(URI.create(startURI + topic + limit + endURI));
            else
                requestURI.add(URI.create(startURI + topic + page + limit + nrPages + endURI));
        }

        view=null;
        if(savedInstanceState==null)
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_others, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        CallAPI task = new CallAPI();

        task.execute(requestURI.get(option));
        //thread for getting data from the API

        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle args = new Bundle();
                args.putParcelable("movie", allMovies.get(position));
                Fragment fragment = new DetailsFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            }
            private void switchFragment(Fragment fragment) {
                if (view2.getContext() == null)
                    return;
                if (view2.getContext() instanceof Main) {
                    Main main = (Main) view2.getContext();
                    main.switchContent(fragment);
                }
            }

        });*/
        /*final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });


        mActionBarHeight = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        mActionBar = getActivity().getActionBar();

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            float y = (view.findViewById(R.id.parent)).getScrollY();
            if (y >= mActionBarHeight && mActionBar.isShowing()) {
                mActionBar.hide();
            } else if ( y==0 && !mActionBar.isShowing()) {
                mActionBar.show();
            }
        }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }
});
*/
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
            gridView.setAdapter(new GridAdapter(view.getContext(), allMovies));
            progressDialog.dismiss();
        }
    }
}