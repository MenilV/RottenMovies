package com.menil.rottenmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

    public ListsAdapter(Context context, List<Movie> allMovies) {
        this.mContext = context;
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
        String new_date=release_date.substring(8,10)+"/"+release_date.substring(5,7)+"/"+release_date.substring(0,4);
        releaseView.setText("Release date: " + new_date);

        TextView rating = (TextView) view.findViewById(R.id.fragment_list_item_rating);
        int criticsScore = (int) listMovies.get(position).ratings.critics_score;
        rating.setText("Rating: " + String.valueOf(criticsScore));



        ImageView IMDBImage = (ImageView) view.findViewById(R.id.fragment_list_item_imdb_link);
        final String IMDBiD=listMovies.get(position).imdb.imdb;


        IMDBImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final View v2=v;
                final Uri uri = Uri.parse("http://www.imdb.com/title/tt"+IMDBiD);
                int SPLASH_TIME_OUT = 250;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v2.getContext().startActivity(intent);
                    }
                }, SPLASH_TIME_OUT);


            }
        });

        ImageView RottenImage = (ImageView) view.findViewById(R.id.fragment_list_item_rtn_link);
        final String RottenID=listMovies.get(position).links.alternate;
        RottenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(RottenID);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        final Button addToFav = (Button)view.findViewById(R.id.fragment_list_add_to_fav);
        addToFav.setVisibility(View.INVISIBLE);

        ImageView more = (ImageView)view.findViewById(R.id.fragment_list_more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToFav.getVisibility()==View.INVISIBLE)
                    addToFav.setVisibility(View.VISIBLE);
                else
                    addToFav.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    /*public View getView(){
        View v=(ImageView)v.findViewById(R.id.fragment_list_item_imdb_link);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.imdb.com/title/tt");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);
            }
        });
    }*/

}
