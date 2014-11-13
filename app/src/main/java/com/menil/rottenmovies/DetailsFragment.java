package com.menil.rottenmovies;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
 * Created by menil on 08.10.2014.
 */
public class DetailsFragment extends android.app.Fragment {

    private Bundle bundle;
    private Drawable mActionBarBackgroundDrawable;
    private View view;

    //private static final String TAG = "DETAILS";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState == null)
            // Inflate the layout for this fragment
            //view = inflater.inflate(R.layout.fragment_details, container, false);
        /**
        /INFLATED TEST VIEW>>>>>> CHANGE THAT
        */
            view = inflater.inflate(R.layout.details_card_test, container, false);
        // Retrieve data from bundle with Parcelable object of type Movie
        bundle = getArguments();
        try {
            mActionBarBackgroundDrawable = getResources().getDrawable(R.drawable.actionbar_background);
            mActionBarBackgroundDrawable.setAlpha(0);

            getActivity().getActionBar().setDisplayShowTitleEnabled(false);
            getActivity().getActionBar().setTitle("");
            getActivity().getActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);
            getActivity().getActionBar().setSubtitle(null);
            getActivity().getActionBar().setIcon(new ColorDrawable(0x00000000));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        Movie movie = bundle.getParcelable("movie");

        TextView title = (TextView) view.findViewById(R.id.fragment_details_title);
        TextView synopsis = (TextView) view.findViewById(R.id.fragment_details_synopsis);

        TextView runtime = (TextView) view.findViewById(R.id.fragment_details_runtime);
        TextView cast = (TextView) view.findViewById(R.id.fragment_details_cast);
        TextView rating = (TextView) view.findViewById(R.id.fragment_details_rating);

        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_details_img);
        imageView.setImageURL(movie.posters.detailed.replace("tmb", "det"), false);

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
                if (floatingActionButton.getColorNormal() == getResources().getColor(R.color.green)) {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                    floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                } else {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                    floatingActionButton.setImageResource(R.drawable.ic_action_favorite);

                }
            }
        });

        final FloatingActionButton floatingActionButton2 = (FloatingActionButton) view.findViewById(R.id.fragment_details_fab2);
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            //View hider = view.findViewById(R.id.hider);


            @Override
            public void onClick(View v) {
                if (floatingActionButton2.getColorPressed() == getResources().getColor(R.color.light_gray)) {
                    floatingActionButton2.setColorPressed(R.color.pale_gray);
                    floatingActionButton2.setImageResource(R.drawable.ic_action_up);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imageViewTop.getLayoutParams());
                    lp.setMargins(0, 0, 0, 0);
                    imageViewTop.setLayoutParams(lp);

                } else {
                    floatingActionButton2.setImageResource(R.drawable.ic_action_expand);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(imageViewTop.getLayoutParams());
                    lp.setMargins(0, -400, 0, -400);
                    imageViewTop.setLayoutParams(lp);
                    floatingActionButton2.setColorPressed(R.color.light_gray);
                }
            }
        });
        synopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t = (TextView) v.findViewById(R.id.fragment_details_synopsis);

                //Toggle synopsis if synopsis is too long
                if (t.getEllipsize() == TextUtils.TruncateAt.END) {
                    t.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    t.setSingleLine(false);

                } else if (t.getEllipsize() == TextUtils.TruncateAt.MARQUEE) {
                    t.setMaxLines(10);
                    t.setEllipsize(TextUtils.TruncateAt.END);

                }
            }
        });

        NotifyingScrollView scrollView = (NotifyingScrollView) view.findViewById(R.id.fragment_details_scroll);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
        return view;

    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            final int headerHeight = view.findViewById(R.id.fragment_details_img_top).getHeight() - getActivity().getActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };
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