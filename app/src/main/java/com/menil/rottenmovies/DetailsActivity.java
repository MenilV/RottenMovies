package com.menil.rottenmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
 * Created by menil on 13.11.2014.
 */
public class DetailsActivity extends PreferenceActivity {
    public SharedPreferences preferences;
    Context context;
    private Bundle bundle;
    private Drawable mActionBarBackgroundDrawable;
    private View view;

   /* public void DetailsActivity(Bundle args)
    {
        this.bundle=args;
       // bu
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(bundle);
        //setUserVisibleHint(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        view = getLayoutInflater().inflate(R.layout.fragment_details, null);
        bundle = getIntent().getExtras();
        try {
            mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background);
            mActionBarBackgroundDrawable.setAlpha(0);

            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setTitle("");
            getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
            getActionBar().setSubtitle(null);
            getActionBar().setIcon(new ColorDrawable(0x00000000));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //final Movie movie = bundle.getParcelable("movie");
        final Movie movie = (Movie) bundle.getSerializable("movie");
        TextView title = (TextView) view.findViewById(R.id.fragment_details_title);
        TextView synopsis = (TextView) view.findViewById(R.id.fragment_details_synopsis);

        TextView runtime = (TextView) view.findViewById(R.id.fragment_details_runtime);
        TextView cast = (TextView) view.findViewById(R.id.fragment_details_cast);
        TextView rating = (TextView) view.findViewById(R.id.fragment_details_rating);

        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_details_img);

        /**
         * OVDJE BACA EXCEPTION
         * java.lang.StackOverflowError
         at android.os.Parcel.writeValue(Parcel.java:1203)
         at com.menil.rottenmovies.Movie.writeToParcel(Movie.java:65)
         */
        imageView.setImageURL(movie.posters.detailed.replace("tmb", "det"), true);

        //getting a resized image from ThumbrIo service
        final RemoteImageView imageViewTop = (RemoteImageView) view.findViewById(R.id.fragment_details_img_top);
        String rescaledImage = null;
        try {
            rescaledImage = ThumbrIo.sign(movie.posters.detailed.replace("tmb", "ori"), "510x755c");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        imageViewTop.setImageURL(rescaledImage, false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is for image fullscreen
            }
        });

        title.setText(movie.title);
        title.append(" (" + String.valueOf(movie.year) + ")");
        synopsis.setText("Synopsis:\n\n" + movie.synopsis);

        runtime.setText("Runtime: " + String.valueOf(movie.runtime) + " min");
        rating.setText("Rating: " + movie.mpaa_rating);

        int x = 0;//just to ensure there are no commas after the last actor
        List<Cast> castList = movie.casts;
        String castText = "Cast: ";
        for (Cast c : castList) {
            //cast.append(c.name);
            castText += c.name;
            if (++x < castList.size())
                castText += ", ";
            //cast.append(", ");
        }
        cast.setText(castText);
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

        final FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fragment_details_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences = getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
                if (preferences.getString("id", null) == null)// && floatingActionButton.getColorNormal() == getResources().getColor(R.color.green) )
                {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                    floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                    ModifyPreferences("id", movie.id, 0);
                    Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_LONG).show();

                } else {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                    floatingActionButton.setImageResource(R.drawable.ic_action_favorite);
                    ModifyPreferences("id", movie.id, 1);
                    Toast.makeText(getApplicationContext(), "Removed from favourites", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void ModifyPreferences(String key, String value, int option) {
        preferences = getSharedPreferences("favsAreHere", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (option) {
            case 0://adds item to favs
                editor.putString(key, value);
                editor.apply();
                break;
            case 1://deletes item from favs
                editor.remove(key);
                editor.apply();
                break;
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
