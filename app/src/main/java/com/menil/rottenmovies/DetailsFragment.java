package com.menil.rottenmovies;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by menil on 08.10.2014.
 */
public class DetailsFragment extends android.app.Fragment {

    /*@Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Retrieve data from bundle with Parcelable object of type Movie
        Bundle bundle = getArguments();
        Movie movie = bundle.getParcelable("movie");
        TextView title = (TextView) view.findViewById(R.id.fragment_details_title);
        TextView synopsis = (TextView) view.findViewById(R.id.fragment_details_synopsis);
        //TextView year = (TextView) view.findViewById(R.id.fragment_details_year);
        TextView runtime = (TextView) view.findViewById(R.id.fragment_details_runtime);
        TextView cast = (TextView) view.findViewById(R.id.fragment_details_cast);
        TextView rating = (TextView) view.findViewById(R.id.fragment_details_rating);
        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_details_img);
        imageView.setImageURL(movie.posters.detailed.replace("tmb", "det"), false);


        title.setText(movie.title);
        title.append(" ("+String.valueOf(movie.year)+")");
        synopsis.setText(movie.synopsis);

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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //view.setBackground(imageView.getDrawable());
            }
        });


        return view;
    }
}
