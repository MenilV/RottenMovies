package com.menil.rottenmovies;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.List;

/**
 * Created by menil on 08.10.2014.
 */
public class DetailsFragment extends android.app.Fragment {

    Bundle bundle;
    private static final String TAG = "DETAILS";

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
    }
    @Override
    public void onResume()
    {
        super.onResume();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=null;
        if(savedInstanceState==null)
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_details, container, false);

        // Retrieve data from bundle with Parcelable object of type Movie
        bundle = getArguments();
        Movie movie = bundle.getParcelable("movie");
        assert view != null;
        TextView title = (TextView) view.findViewById(R.id.fragment_details_title);
        TextView synopsis = (TextView) view.findViewById(R.id.fragment_details_synopsis);

        TextView runtime = (TextView) view.findViewById(R.id.fragment_details_runtime);
        TextView cast = (TextView) view.findViewById(R.id.fragment_details_cast);
        TextView rating = (TextView) view.findViewById(R.id.fragment_details_rating);

        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_details_img);
        imageView.setImageURL(movie.posters.detailed.replace("tmb", "det"), false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.fragment_details_linear);
                //LayoutInflater layoutInflater = LayoutInflater.from(this);

                View child = getActivity().getLayoutInflater().inflate(R.layout.full_screen_img, null);
                ImageView fullImage=(ImageView)child.findViewById(R.id.full_screen_imgview);

                //linearLayout.addView(child);
                //ImageView fullImage=(ImageView)view.findViewById(R.id.fragment_details_img);
                //fullImage.setImageDrawable(imageView.getDrawable());
                //View child = inflater.inflate(R.layout.full_screen_img, null);


            }
        });


        title.setText(movie.title);
        title.append(" (" + String.valueOf(movie.year) + ")");
        synopsis.setText("Synopsis:\n"+movie.synopsis);

        runtime.append(String.valueOf(movie.runtime));
        runtime.append(" min");
        rating.append(movie.mpaa_rating);

        int x = 0;//just to ensure there are no commas after the last actor
        List<Cast> castList = movie.casts;
        for (Cast c : castList) {
            cast.append(c.name);
            if (++x < castList.size())
                cast.append(", ");
        }
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
                if(floatingActionButton.getColorNormal()==getResources().getColor(R.color.green)) {
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.white));
                    floatingActionButton.setImageResource(R.drawable.ic_navigation_check);
                }
                else{
                    floatingActionButton.setColorNormal(getResources().getColor(R.color.green));
                    floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp);

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

        return view;
    }
}
