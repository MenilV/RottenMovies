package com.menil.rottenmovies;

import android.content.Context;
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

    public ListsAdapter(List<Movie> allMovies) {
        this.listMovies = allMovies;
    }

    public ListsAdapter(Context c, List<Movie> allMovies) {
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
        titleView.setText((position + 1) + ". ");
        titleView.append(title);

        int year = listMovies.get(position).year;
        titleView.append(" (" + year + ")");

        TextView runtimeView = (TextView) view.findViewById(R.id.fragment_list_item_runtime);
        String runtime = listMovies.get(position).runtime;
        runtimeView.setText("Runtime: " + runtime + " min");

        TextView releaseView = (TextView) view.findViewById(R.id.fragment_list_item_date);
        String release_date = listMovies.get(position).release_dates.theater;
        releaseView.setText("Release date: " + release_date);

        TextView rating = (TextView)view.findViewById(R.id.fragment_list_item_rating);
        int criticsScore = (int)listMovies.get(position).ratings.critics_score;
        rating.append(String.valueOf(criticsScore));

        /*View progress_top = (View)view.findViewById(R.id.pr_bar_top);
        progress_top.setLayoutParams(new ViewGroup.LayoutParams(40,20));*/



        /*TextView castView = (TextView) view.findViewById(R.id.fragment_list_item_cast);
        List<Cast> castList=listMovies.get(position).casts;
        castView.setText("Cast: ");
        int x=0;
        for (Cast c : castList) {
            castView.append(c.name);
            if (++x < castList.size())
                castView.append(", ");
        }*/
        return view;
    }

}
