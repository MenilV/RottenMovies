package com.menil.rottenmovies;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.melnykov.fab.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
 * Created by menil on 08.10.2014.
 */
public class DetailsFragment extends android.app.Fragment {

    public SharedPreferences preferences;
    private Bundle bundle;
    private View view;
    public static final String movie_id = "id";

    //TODO:Details fragment crashes on KEYCODE_HOME pressed

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(bundle);
        //setUserVisibleHint(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void makeActionbar(){
        ActionBar actionBar = getActivity().getActionBar();
        try {
            Drawable mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background);
            mActionBarBackgroundDrawable.setAlpha(0);
            assert actionBar != null;
            actionBar.hide();
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
            actionBar.setSubtitle(null);
            actionBar.setIcon(new ColorDrawable(0x00000000));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState == null)
        // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_details, container, false);
        makeActionbar();
        // Retrieve data from bundle with Parcelable object of type Movie
        bundle = getArguments();


        final Movie movie = bundle.getParcelable("movie");

        /**
         * TEXT STUFF COMES HERE
         */

        TextView title = (TextView) view.findViewById(R.id.fragment_details_title);
        title.setText(movie.title + " (" + String.valueOf(movie.year) + ")");
        title.setSelected(true);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.fragment_details_title);
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

        TextView synopsis = (TextView) view.findViewById(R.id.fragment_details_synopsis);
        synopsis.setText(movie.synopsis);

        TextView runtime = (TextView) view.findViewById(R.id.fragment_details_runtime);
        runtime.setText("Runtime: " + String.valueOf(movie.runtime) + " min");

        TextView rating = (TextView) view.findViewById(R.id.fragment_details_rating);
        rating.setText("Rating: " + movie.mpaa_rating);

        TextView cast = (TextView) view.findViewById(R.id.fragment_details_cast);
        int x = 0;//just to ensure there are no commas after the last actor
        List<Cast> castList = movie.casts;
        String castText = "Cast: ";
        for (Cast c : castList) {
            //cast.append(c.name);
            castText += c.name;
            if (++x < castList.size())
                castText += ", ";
        }
        cast.setText(castText);
        cast.setSelected(true);

        /**
         * IMAGE STUFF COMES HERE
         */

        ImageView imageView = (ImageView) view.findViewById(R.id.fragment_details_img);

        Ion.with(imageView)
                .placeholder(R.drawable.empty_img)
                .error(R.drawable.empty_img_error)
                .load(movie.posters.detailed.replace("tmb", "det"));
        //getting a resized image from ThumbrIo service
        final ImageView imageViewTop = (ImageView) view.findViewById(R.id.fragment_details_img_top);
        String rescaledImage = null;

        try {//rescale and set picture TOP
            rescaledImage = ThumbrIo1.sign(movie.posters.detailed.replace("tmb", "ori"), "510x755c");
            Ion.with(imageViewTop)
                    .placeholder(R.drawable.empty_img)
                    .error(R.drawable.empty_img_error)
                    .load(rescaledImage);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        /**
         * BUTTONS STUFF COMES HERE
         */
        Gson gson = new Gson();
        String fav = gson.toJson(movie);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fragment_details_fab);

        //TODO: need to change button if it's already favourited
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            int option;
            @Override
            public void onClick(View v) {
                preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
                    if(!preferences.getString(movie_id,"").contains(movie.id))
                    {
                        floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                        floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                        option = 0;
                    }
                    else{
                        floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                        floatingActionButton.setImageResource(R.drawable.ic_action_favorite);
                        option = 1;
                    }
                modifyPreferences(movie_id, movie.id, option);
            }
        });
        return view;
    }

    private void modifyPreferences(String key, String value, int option) {
        preferences = getActivity().getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String idFav = preferences.getString(key, "");
        boolean contains = false;
        if (idFav.contains(value))
            contains = true;

        switch (option) {
            case 0://adds item to favourites if there isn't one already
                if (!contains)
                {
                    idFav += (","+value);
                    editor.putString(key, idFav);
                    editor.apply();
                    Toast.makeText(getActivity().getApplicationContext(),"Added to favourites", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Already in favourites", Toast.LENGTH_LONG).show();
                }

                break;
            case 1://deletes item from favourites if there is one already
                if (contains)
                {
                    editor.remove(key);
                    editor.apply();
                    idFav=idFav.replace(","+value,"");
                    editor.putString(key, idFav);
                    editor.apply();
                    Toast.makeText(getActivity().getApplicationContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), "Can't remove. It was not a favourite", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}

class ThumbrIo1 {

    private static final String THUMBRIO_API_KEY = "t0AsaoQ1lG-nJaIvOavA";
    private static final String THUMBRIO_SECRET_KEY = "9YmFRL63IhMqhASdj1fq";
    private static final String[] THUMBRIO_BASE_URLS = {
            "http://api.thumbr.io/", "https://api.thumbr.io/"
    };

    private ThumbrIo1() {
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
        return ThumbrIo1.sign(url, size, "thumb.png", null, THUMBRIO_BASE_URLS[0]);
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
        String token = ThumbrIo1.toHex(hmacBytes);

        return String.format("%s%s/%s", baseUrl, token, path);
    }

    public static String safe_url_escape(String url) throws UnsupportedEncodingException {
        String res = URLEncoder.encode(url, "UTF-8");
        return res.replace("%2F", "/").replace("*", "%2A").replace("+", "%20");
    }

}