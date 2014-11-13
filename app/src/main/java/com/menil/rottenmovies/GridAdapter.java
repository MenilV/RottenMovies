package com.menil.rottenmovies;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by menil on 13.10.2014.
 */
public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> listMovies = new ArrayList<Movie>();
    private int option = 0;

    public GridAdapter(Context c, List<Movie> allMovies) {
        this.mContext = c;
        this.listMovies = allMovies;
    }

    @Override
    public int getCount() {
        return listMovies.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.grid_full_card, null);
        } else {
            view = convertView;
        }


        TextView titleView = (TextView) view.findViewById(R.id.full_card_title);
        titleView.setText(listMovies.get(position).title);

        TextView subtitleView = (TextView) view.findViewById(R.id.full_card_subtitle);
        subtitleView.setText("(" + String.valueOf(listMovies.get(position).year)+")\n"+listMovies.get(position).runtime+" min");

        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.full_card_img);
        String picURL = listMovies.get(position).posters.detailed.replace("tmb", "det");
        imageView.setImageURL(picURL, false);

        ImageView favView = (ImageView) view.findViewById(R.id.fragment_grid_fav);
        favView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

}