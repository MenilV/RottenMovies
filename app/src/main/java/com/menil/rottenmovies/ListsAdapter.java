package com.menil.rottenmovies;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by menil on 31.10.2014.
 */

//TODO: merge with gridAdapter
public class ListsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> listMovies = new ArrayList<Movie>();
    private int option = 0;

    public ListsAdapter(Context c, List<Movie> allMovies) {
        this.mContext = c;
        this.listMovies = allMovies;
    }

    public ListsAdapter(Context c, List<Movie> allMovies, int option) {
        this.mContext = c;
        this.listMovies = allMovies;
        this.option = option;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.fragment_list_item2, null);

        } else {
            view = convertView;
        }
        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_list_item_img);
        String picURL = listMovies.get(position).posters.detailed.replace("tmb", "det");
        imageView.setImageURL(picURL, false);

        TextView titleView = (TextView) view.findViewById(R.id.fragment_list_item_title);
        String title = listMovies.get(position).title;
        titleView.setText((position+1)+". ");
        titleView.append(title);

        //yearView = (TextView) view.findViewById(R.id.fragment_list_item_year);
        int year = listMovies.get(position).year;
        //yearView.setText(Integer.toString(year));
        titleView.append(" ("+year+")");

        TextView runtimeView = (TextView) view.findViewById(R.id.fragment_list_item_runtime);
        String runtime = listMovies.get(position).runtime;
        runtimeView.setText("Runtime: "+runtime+" min");

        TextView releaseView = (TextView) view.findViewById(R.id.fragment_list_item_date);
        String release_date = listMovies.get(position).release_dates.theater;
        releaseView.setText("Release date: "+release_date);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                Fragment fragment = new DetailsFragment();
                switchFragment(fragment);
            }

            private void switchFragment(Fragment fragment) {
                if (mContext == null)
                    return;
                if (mContext instanceof Main) {
                    Main main = (Main) mContext;
                    main.switchContent(fragment);
                }
            }
        });

        return view;
    }

}
