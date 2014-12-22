package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
 * Created by menil on 02.12.2014.
 */
public class DetailsActivity extends Activity {

    public static final String TAG = "DETAILS";
    public static final String movie_id = "id";
    public static final String recent_id = "recent_id";
    public static final String FAVS_ARE_HERE = "favsAreHere";
    public static final String RECENT_ARE_HERE = "recentAreHere";
    public SharedPreferences preferences;
    private Movie detailMovie;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


        public boolean isConnectedToInternet() {
            ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
            }
            return false;
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        makeActionbar();
        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        detailMovie = (Movie) args.getSerializable("movie");

        addToRecent(recent_id, detailMovie);
        if(!isConnectedToInternet())
        {
            Intent mainIntent = new Intent(this, Main.class);
            startActivity(mainIntent);
            this.finish();
        }


        /**
         * TEXT STUFF COMES HERE
         */

        TextView title = (TextView) findViewById(R.id.activity_details_title);
        String movieYear = detailMovie.year;
        if (movieYear.length() < 4)
            movieYear = "noYear";
        title.setText(detailMovie.title + " (" + movieYear + ")");
        title.setSelected(true);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.activity_details_title);
                //Toggle title if title is too long
                if (t.getEllipsize() == TextUtils.TruncateAt.END) {
                    t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    t.setSingleLine(false);
                    t.setMaxLines(3);
                } else if (t.getEllipsize() == TextUtils.TruncateAt.MARQUEE) {
                    t.setSingleLine(true);
                    t.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });

        TextView synopsis = (TextView) findViewById(R.id.activity_details_synopsis);
        if (detailMovie.synopsis.length() < 5)
            synopsis.setText("No synopsis found");
        else
            synopsis.setText(detailMovie.synopsis);

        TextView runtimeView = (TextView) findViewById(R.id.activity_details_runtime);
        String runtime = detailMovie.runtime;
        if (runtime.equals(""))
            runtime = "NaN";
        runtimeView.setText("Runtime: " + runtime + " min");


        TextView rating = (TextView) findViewById(R.id.activity_details_rating);
        rating.setText("Rating: " + detailMovie.mpaa_rating);

        TextView cast = (TextView) findViewById(R.id.activity_details_cast);
        int x = 0;//just to ensure there are no commas after the last actor
        List<Cast> castList = detailMovie.casts;
        String castText = "Cast: ";
        for (Cast c : castList) {
            castText += c.name;
            if (++x < castList.size())
                castText += ", ";
        }

        if (castText.length() < 8)
            castText = "Cast: No cast found";
        cast.setText(castText);
        cast.setSelected(true);

        /**
         * IMAGE STUFF COMES HERE
         */

        RemoteImageView imageView = (RemoteImageView) findViewById(R.id.activity_details_img);
        String det_pic = detailMovie.posters.detailed.replace("tmb", "det");

        imageView.setImageURL(det_pic);
        //getting a resized image from ThumbrIo service
        final RemoteImageView imageViewTop = (RemoteImageView) findViewById(R.id.activity_details_img_top);
        //rescaledImage = null;

        try {//rescale and set picture TOP
            String rescaledImage = ThumbrIo.sign(detailMovie.posters.detailed.replace("tmb", "ori"), "510x755c");


            imageViewTop.setImageURL(rescaledImage);

        } catch (NoSuchAlgorithmException|InvalidKeyException|UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /**
         * API CALLS COME HERE
         */

        String criticsRequest = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + detailMovie.getId() + "/reviews.json?apikey=pj2z7eyve6mfdtcx4vynk26y";
        URI requestURIc = URI.create(criticsRequest);
        CallAPICritics taskC = new CallAPICritics();
        taskC.execute(requestURIc);
        //calling API to get critics reviews;

        String similarRequest = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + detailMovie.getId() + "/similar.json?limit=5&apikey=pj2z7eyve6mfdtcx4vynk26y";
        URI requestURIs = URI.create(similarRequest);
        CallAPISimilar taskS = new CallAPISimilar();
        taskS.execute(requestURIs);
        //calling API to get similar movies;

        String clipRequest = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + detailMovie.getId() + "/clips.json?limit=5&apikey=pj2z7eyve6mfdtcx4vynk26y";
        URI requestURIclip = URI.create(clipRequest);
        CallAPIClips taskClip = new CallAPIClips();
        taskClip.execute(requestURIclip);
        //calling API to get clips for a movies;

        String movieRequest = "http://api.rottentomatoes.com/api/public/v1.0/movies/" + detailMovie.getId() + ".json?apikey=pj2z7eyve6mfdtcx4vynk26y";
        URI requestURImovie = URI.create(movieRequest);
        CallAPIGenres taskMovie = new CallAPIGenres();
        taskMovie.execute(requestURImovie);
        //calling API to get clips for a movies;
        /**
         * BUTTONS STUFF COMES HERE
         */


        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.activity_details_fab);
        if (getSharedPreferences(FAVS_ARE_HERE, Context.MODE_PRIVATE).getString(movie_id, "").contains(detailMovie.id)) {
            floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
            floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
        } else {
            floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
            floatingActionButton.setImageResource(R.drawable.ic_action_favorite);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            int option;

            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences(FAVS_ARE_HERE, Context.MODE_PRIVATE);
                if (!preferences.getString(movie_id, "").contains(detailMovie.id)) {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                    floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                    option = 0;
                } else {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                    floatingActionButton.setImageResource(R.drawable.ic_action_favorite);
                    option = 1;
                }
                modifyPreferences(movie_id, option, detailMovie);
            }
        });

        FloatingActionButton floatingActionButtonShare = (FloatingActionButton) findViewById(R.id.activity_fab_share);
        floatingActionButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToFB(detailMovie);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    protected void shareToFB(Movie detailMovie) {
        int x = 0;//just to ensure there are no commas after the last actor
        List<Cast> castList = detailMovie.casts;
        String castText = "Cast: ";
        for (Cast c : castList) {
            castText += c.name;
            if (++x < castList.size())
                castText += ", ";
        }

        if (castText.length() < 8)
            castText = "Cast: No cast found";

        if (Session.getActiveSession().isOpened())
        //user is logged to facebook proceed
        {
            if (FacebookDialog.canPresentShareDialog(this)) {
                FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
                        .setLink(detailMovie.links.getAlternate())
                        .setName(detailMovie.title)
                        .setCaption(castText)
                        .setDescription(detailMovie.synopsis)
                        .setPicture(detailMovie.posters.thumbnail.replace("tmb", "det"))
                        .build();
                uiHelper.trackPendingDialogCall(shareDialog.present());
            } else {
                //using this if there is no Facebook application installed
                Bundle params = new Bundle();
                params.putString("name", detailMovie.title);
                params.putString("caption", castText);
                params.putString("description", detailMovie.synopsis);
                params.putString("link", detailMovie.links.getAlternate());
                params.putString("picture", detailMovie.posters.thumbnail.replace("tmb", "det"));

                WebDialog feedDialog = (
                        new WebDialog.FeedDialogBuilder(this,
                                Session.getActiveSession(),
                                params))
                        .setOnCompleteListener(new WebDialog.OnCompleteListener() {

                            @Override
                            public void onComplete(Bundle values,
                                                   FacebookException error) {
                                if (error == null) {
                                    // When the story is posted, echo the success
                                    // and the post Id.
                                    final String postId = values.getString("post_id");
                                    if (postId != null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Posted story, id: " + postId,
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // User clicked the Cancel button
                                        Toast.makeText(getApplicationContext(),
                                                "Publish cancelled",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else if (error instanceof FacebookOperationCanceledException) {
                                    // User clicked the "x" button
                                    Toast.makeText(getApplicationContext(),
                                            "Publish cancelled",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Generic, ex: network error
                                    Toast.makeText(getApplicationContext(),
                                            "Error posting story",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                        })
                        .build();
                feedDialog.show();
            }
        } else {
            //not logged in. try to login
            Toast.makeText(this, "Please go to the About page and login with your Facebook account", Toast.LENGTH_LONG).show();
        }

    }

    public void makeActionbar() {
        ActionBar actionBar = getActionBar();
        try {

            assert actionBar != null;
            if (actionBar.isShowing())
                actionBar.hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void addToRecent(String key, Movie movie) {
        if (key.equals("recent_id"))
            preferences = getSharedPreferences(RECENT_ARE_HERE, Context.MODE_PRIVATE);

        //Toast.makeText(getApplicationContext(), "Added to recent", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String movieJson = gson.toJson(movie);
        String idFav = preferences.getString(key, "{\"movies\":[");
        boolean contains = false;
        if (idFav.contains(movie.getId()))
            contains = true;
        if (!contains) {
            if (idFav.length() > 20) {
                idFav = idFav.substring(0, idFav.length() - 2);
                idFav += ",";
            }
            idFav += gson.toJson(movie);
            idFav += "]}";
            editor.putString(key, idFav);
            editor.apply();

        }
    }

    public void modifyPreferences(String key, int option, Movie movie) {
        preferences = getSharedPreferences(FAVS_ARE_HERE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String movieJson = gson.toJson(movie);
        String idFav = preferences.getString(key, "{\"movies\":[");
        boolean contains = false;
        if (idFav.contains(movie.getId()))
            contains = true;

        switch (option) {
            case 0://adds item to favourites if there isn't one already
                if (!contains) {
                    if (idFav.length() > 20) {
                        idFav = idFav.substring(0, idFav.length() - 2);
                        idFav += ",";
                    }
                    idFav += gson.toJson(movie);
                    idFav += "]}";
                    editor.putString(key, idFav);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Already in favourites", Toast.LENGTH_LONG).show();
                }

                break;
            case 1://deletes item from favourites if there is one already
                if (contains) {
                    String forReplace = movieJson + ",";
                    idFav = idFav.replace(forReplace, "");
                    editor.putString(key, idFav);
                    editor.apply();
                    //Toast.makeText(getActivity().getApplicationContext(), idFav, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Can't remove. It was not a favourite", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void createCritics(List<Review> reviewList) {
        TextView reviewText = (TextView) findViewById(R.id.activity_details_critics);
        reviewText.setText("There are no critics for this movie.");
        boolean reviewFinished = true;
        for (Review r : reviewList) {
            if (reviewFinished) {
                reviewText.setText("");
                reviewFinished = false;
            }
            reviewText.append(r.critic + " said: \"" + r.quote + "\" in: " + r.publication + "\n\n");
        }
    }

    public void createSimilar(List<Movie> similarMovies) {
        TextView similarText = (TextView) findViewById(R.id.activity_details_similar);
        similarText.setText("No similar movies found.");
        int count = 0;
        boolean similarFinished = true;
        for (Movie m : similarMovies) {
            if (similarFinished) {
                similarText.setText("");
                similarFinished = false;
            }
            count++;
            similarText.append(String.valueOf(count) + ". " + m.title + " (" + m.year + ")" + "\n");
        }
    }

    public void createClips(List<Clip> allClips) {
        RemoteImageView clipImg1 = (RemoteImageView) findViewById(R.id.activity_details_clips_img1);
        RemoteImageView clipImg2 = (RemoteImageView) findViewById(R.id.activity_details_clips_img2);
        RemoteImageView clipImg3 = (RemoteImageView) findViewById(R.id.activity_details_clips_img3);
        RemoteImageView clipImg4 = (RemoteImageView) findViewById(R.id.activity_details_clips_img4);
        RemoteImageView clipImg5 = (RemoteImageView) findViewById(R.id.activity_details_clips_img5);
        RemoteImageView clipImg6 = (RemoteImageView) findViewById(R.id.activity_details_clips_img6);
        RemoteImageView clipImg7 = (RemoteImageView) findViewById(R.id.activity_details_clips_img7);
        RemoteImageView[] clips = {clipImg1, clipImg2, clipImg3, clipImg4, clipImg5, clipImg6, clipImg7};
        int x = 0;
        for (Clip c : allClips) {
            String clipLink = c.getThumbnail();
            clips[x++].setImageURL(clipLink);
            if (x == clips.length)
                break;
        }
    }

    public void createGenres(Movie genreMovie) {
        List<String> genereList = new ArrayList<>();
        String[] genresStrings = {"Action & Adventure", "Animation", "Art House & International", "Classics", "Comedy", "Documentary", "Drama", "Horror", "Kids & Family", "Mistery & Suspense", "Musical & Performing Arts", "Romance", "Science Fiction & Fantasy", "Sports & Fitness", "Special Interest"};
        for (String s : genresStrings)
            genereList.add(s);
        /**
         * maybe change this so the array gets faster to the list
         */

        ImageView genresImg1 = (ImageView) findViewById(R.id.activity_details_genres_img1);
        ImageView genresImg2 = (ImageView) findViewById(R.id.activity_details_genres_img2);
        ImageView genresImg3 = (ImageView) findViewById(R.id.activity_details_genres_img3);
        ImageView genresImg4 = (ImageView) findViewById(R.id.activity_details_genres_img4);
        ImageView[] imageViews = {genresImg1, genresImg2, genresImg3, genresImg4};
        int x = 0;
        for (String s : genereList) {
            if (x == imageViews.length)
                break;
            if (genreMovie.genres.contains(s)) {
                switch (s) {
                    case "Action & Adventure": {
                        imageViews[x++].setImageResource(R.drawable.action);
                        break;
                    }
                    case "Animation": {
                        imageViews[x++].setImageResource(R.drawable.animation);
                        break;
                    }
                    case "Art House & International": {
                        imageViews[x++].setImageResource(R.drawable.art);
                        break;
                    }
                    case "Classics": {
                        imageViews[x++].setImageResource(R.drawable.classics);
                        break;
                    }
                    case "Comedy": {
                        imageViews[x++].setImageResource(R.drawable.comedy);
                        break;
                    }
                    case "Documentary": {
                        imageViews[x++].setImageResource(R.drawable.documentary);
                        break;
                    }
                    case "Drama": {
                        imageViews[x++].setImageResource(R.drawable.drama);
                        break;
                    }
                    case "Horror": {
                        imageViews[x++].setImageResource(R.drawable.horror);
                        break;
                    }
                    case "Kids & Family": {
                        imageViews[x++].setImageResource(R.drawable.kids);
                        break;
                    }
                    case "Mistery & Suspense": {
                        imageViews[x++].setImageResource(R.drawable.mistery);
                        break;
                    }
                    case "Musical & Performing Arts": {
                        imageViews[x++].setImageResource(R.drawable.musical);
                        break;
                    }
                    case "Romance": {
                        imageViews[x++].setImageResource(R.drawable.romance);
                        break;
                    }
                    case "Science Fiction & Fantasy": {
                        imageViews[x++].setImageResource(R.drawable.scifi);
                        break;
                    }
                    case "Sports & Fitness": {
                        imageViews[x++].setImageResource(R.drawable.sports);
                        break;
                    }
                    case "Special Interest": {
                        imageViews[x++].setImageResource(R.drawable.special);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }


    /**
     * ASYNCTASKS COME HERE
     */

    public class CallAPICritics extends AsyncTask<URI, String, List<Review>> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Review> doInBackground(URI... urls) {
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
            List<Review> reviewList = null;
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                Reviews reviews = gson.fromJson(jsonObject.toString(), Reviews.class);
                reviewList = reviews.getReviews();

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return reviewList;
        }

        @Override
        protected void onPostExecute(List<Review> reviewLists) {
            if (reviewLists != null)
            createCritics(reviewLists);
        }
    }

    public class CallAPISimilar extends AsyncTask<URI, String, List<Movie>> {


        @Override
        protected void onPreExecute() {
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
            List<Movie> allMovies = new ArrayList<>();
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                Movies movies = gson.fromJson(jsonObject.toString(), Movies.class); // deserializes json into movies
                allMovies = movies.getMovies();

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> allMovies) {
            if (allMovies != null)
            createSimilar(allMovies);
            //  progressDialog.dismiss();
        }
    }

    public class CallAPIClips extends AsyncTask<URI, String, List<Clip>> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Clip> doInBackground(URI... urls) {
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
            List<Clip> allClips = new ArrayList<>();
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                Clips clips = gson.fromJson(jsonObject.toString(), Clips.class); // deserializes json into movies
                allClips = clips.getClips();

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return allClips;
        }

        @Override
        protected void onPostExecute(List<Clip> allClips) {
            if (allClips != null)
            createClips(allClips);
            //  progressDialog.dismiss();
        }
    }

    public class CallAPIGenres extends AsyncTask<URI, String, Movie> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Movie doInBackground(URI... urls) {
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
            Movie movie = null;
            try {

                Gson gson = new Gson();
                jsonObject = new JSONObject(result);
                movie = gson.fromJson(jsonObject.toString(), Movie.class);


            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            publishProgress(result);
            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if (movie != null)
            createGenres(movie);
            //  progressDialog.dismiss();
        }
    }
}

class ThumbrIo {

    private static final String THUMBRIO_API_KEY = "t0AsaoQ1lG-nJaIvOavA";
    private static final String THUMBRIO_SECRET_KEY = "9YmFRL63IhMqhASdj1fq";
    private static final String[] THUMBRIO_BASE_URLS = {
            "http://api.thumbr.io/", "https://api.thumbr.io/"
    };

    private ThumbrIo() {
        throw new AssertionError();
    }

    private static String toHex(byte bytes[]) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);

        for (byte b : bytes)
            buf.append(Integer.toHexString(b + 0x800).substring(1));

        return buf.toString();
    }

    public static String sign(String url, String size)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return ThumbrIo.sign(url, size, "thumb.png", null, THUMBRIO_BASE_URLS[0]);
    }

    public static String sign(String url, String size, String thumbName)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return ThumbrIo.sign(url, size, thumbName, null, THUMBRIO_BASE_URLS[0]);
    }

    public static String sign(String url, String size, String thumbName, String queryArguments)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return ThumbrIo.sign(url, size, thumbName, queryArguments, THUMBRIO_BASE_URLS[0]);
    }

    public static String sign(String url, String size, String thumbName, String queryArguments, String baseUrl)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        //String format = "http://api.thumbr.io/HMAC TOKEN/API KEY/URL/DIMENSIONOPTIONS/SEO NAME.EXTENSION";
        if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        String encodedUrl = ThumbrIo.safe_url_escape(url);
        String encodedSize = ThumbrIo.safe_url_escape(size);
        String encodedThumbName = ThumbrIo.safe_url_escape(thumbName);
        String path = String.format("%s/%s/%s", encodedUrl, encodedSize, encodedThumbName);

        if (queryArguments != null && !queryArguments.isEmpty()) {
            if (queryArguments.startsWith("?")) {
                path = String.format("%s%s", path, queryArguments);
            } else {
                path = String.format("%s?%s", path, queryArguments);
            }
        }

        // We should add the API to the URL when we use the non customized
        // thumbr.io domains
        if (Arrays.asList(THUMBRIO_BASE_URLS).contains(baseUrl)) {
            path = String.format("%s/%s", THUMBRIO_API_KEY, path);
        }

        // some bots (msnbot-media) "fix" the url changing // by /, so even if
        // it's legal it's troublesome to use // in a URL.
        path = path.replace("//", "%2F%2F");

        SecretKeySpec keySpec = new SecretKeySpec(THUMBRIO_SECRET_KEY.getBytes(), "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(keySpec);
        byte[] hmacBytes = mac.doFinal((baseUrl + path).getBytes("UTF-8"));
        String token = ThumbrIo.toHex(hmacBytes);

        return String.format("%s%s/%s", baseUrl, token, path);
    }

    public static String safe_url_escape(String url) throws UnsupportedEncodingException {
        String res = URLEncoder.encode(url, "UTF-8");
        return res.replace("%2F", "/").replace("*", "%2A").replace("+", "%20");
    }

}
