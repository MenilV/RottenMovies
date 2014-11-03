package com.menil.rottenmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by menil on 08.10.2014.
 */
public class DetailsFragment extends android.app.Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        Movie movie = bundle.getParcelable("movie");

        view = inflater.inflate(R.layout.fragment_details, container, false);
        TextView title = (TextView)view.findViewById(R.id.fragment_details_title);
        TextView synopsis = (TextView)view.findViewById(R.id.fragment_details_synopsis);
        TextView year = (TextView)view.findViewById(R.id.fragment_details_year);
        TextView runtime = (TextView)view.findViewById(R.id.fragment_details_runtime);
        TextView cast = (TextView)view.findViewById(R.id.fragment_details_cast);
        TextView rating = (TextView)view.findViewById(R.id.fragment_details_rating);
        TextView ID = (TextView)view.findViewById(R.id.fragment_details_id);
        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_details_img);

        imageView.setImageURL(movie.posters.detailed.replace("tmb", "det"), false);
        title.setText(movie.title);
        synopsis.setText(movie.synopsis);
        year.setText(" (");
        year.append(String.valueOf(movie.year));
        year.append(")");
        runtime.append(String.valueOf(movie.runtime));
        runtime.append(" min");
        rating.append(movie.mpaa_rating);
        ID.append(movie.id);

        List<Cast> castList = movie.casts;
        for (Cast c:castList) {
            cast.append(c.name);
            cast.append(", ");
        }
        return view;
        //return inflater.inflate(R.layout.fragment_list_item, container, false);


    }
}
