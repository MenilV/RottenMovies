package com.menil.rottenmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by menil on 31.10.2014.
 */


public class ListsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> listMovies = new ArrayList<Movie>();
    private String tag;
    private ArrayList<String> bannedDates = new ArrayList<String>();
    private ArrayList<Integer> uniquePositionDates = new ArrayList<Integer>();


    public ListsAdapter(Context context, List<Movie> allMovies, String tag) {
        this.mContext = context;
        this.listMovies = allMovies;
        this.tag = tag;

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


    public void setHeader(int position, View view) {
        LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);
        TextView headerText = (TextView) view.findViewById(R.id.header_text);
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String prettyDate;
        String currentDate;

        String savedDate;
        if (position == 0) {
            headerLayout.setVisibility(View.VISIBLE);
            savedDate = listMovies.get(position).release_dates.theater;
            prettyDate = months[Integer.parseInt(savedDate.substring(5, 7)) - 1] + " " + savedDate.substring(8, 10);
            headerText.setText(prettyDate);
            bannedDates.add(savedDate);
            uniquePositionDates.add(position);

        } else {
            currentDate = listMovies.get(position).release_dates.theater;

            if (bannedDates.contains(currentDate) && !uniquePositionDates.contains(position))
                headerLayout.setVisibility(View.GONE);
            else {
                headerLayout.setVisibility(View.VISIBLE);
                prettyDate = months[Integer.parseInt(currentDate.substring(5, 7)) - 1] + " " + currentDate.substring(8, 10);
                headerText.setText(prettyDate);
                savedDate = currentDate;
                bannedDates.add(savedDate);
                uniquePositionDates.add(position);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        LayoutInflater layoutInflater;
        if (convertView == null) {

            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_full_card, null);
        } else {
            view = convertView;
        }

        if (tag.equals("BOXOFFICE") || tag.equals("SEARCH") || tag.equals("FAVOURITES")) {
            LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);
            headerLayout.setVisibility(View.GONE);
        } else {
            setHeader(position, view);//setting the header for upcoming and opening movies
        }
        String picURL;
        if (listMovies.get(position) != null)
            picURL = listMovies.get(position).posters.detailed.replace("tmb", "det");
        else
            picURL = "http://i.imgur.com/q62dq0z.png";
        RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.fragment_list_item_img);
        //ImageView imageView = (ImageView) view.findViewById(R.id.fragment_list_item_img);
        /*Ion.with(imageView)
                .placeholder(R.drawable.empty_img)
                .error(R.drawable.empty_img_error)
                .load(picURL);*/
        imageView.setImageURL(picURL);


        TextView titleView = (TextView) view.findViewById(R.id.fragment_list_item_title);
        String title;
        if (listMovies.get(position).title == null)
            title = "No Movie title";
        else
            title = listMovies.get(position).title;
        titleView.setText((position + 1) + ". ");
        titleView.append(title);

        String year = listMovies.get(position).year;
        if (year.length() < 4)
            year = "noYear";
        titleView.append(" (" + year + ")");
        titleView.setSelected(true);

        TextView runtimeView = (TextView) view.findViewById(R.id.fragment_list_item_runtime);
        String runtime = listMovies.get(position).runtime;
        if (runtime.equals(""))
            runtime = "NaN";
        runtimeView.setText("Runtime: " + runtime + " min");

        TextView releaseView = (TextView) view.findViewById(R.id.fragment_list_item_date);
        String release_date, new_date;
        if (listMovies.get(position).release_dates == null || listMovies.get(position).release_dates.theater == null)
            new_date = "No date";
        else {
            release_date = listMovies.get(position).release_dates.theater;
            new_date = release_date.substring(8, 10) + "/" + release_date.substring(5, 7) + "/" + release_date.substring(2, 4);
        }
        releaseView.setText("Release date: " + new_date);

        String criticsScore;
        TextView rating = (TextView) view.findViewById(R.id.fragment_list_item_rating);
        if (listMovies.get(position).ratings == null || listMovies.get(position).ratings.critics_score.equals("-1"))
            criticsScore = "Unrated";
        else
            criticsScore = listMovies.get(position).ratings.critics_score;
        rating.setText("Rating: " + criticsScore);

        ImageView IMDBImage = (ImageView) view.findViewById(R.id.fragment_list_item_imdb_link);
        final String IMDBiD;

        if (listMovies.get(position).imdb == null)
            IMDBiD = "0000000";
        else
            IMDBiD = listMovies.get(position).imdb.getIMDB();

        //IMDB direct link to Movie
        IMDBImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final View v2 = v;
                final Uri uri = Uri.parse("http://www.imdb.com/title/tt" + IMDBiD);
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

        //Rotten Movies direct link to Movie
        ImageView RottenImage = (ImageView) view.findViewById(R.id.fragment_list_item_rtn_link);

        final String RottenID = listMovies.get(position).links.alternate;
        RottenImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(RottenID);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });


        /*if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP)
        {
            CardView mainCardview = (CardView) view.findViewById(R.id.card_view_main);
            CardView rtCardview = (CardView) view.findViewById(R.id.list_full_card_rotten);
            CardView imdbCardview = (CardView) view.findViewById(R.id.list_full_card_imdb);
            LinearLayout linksLayout = (LinearLayout) view.findViewById(R.id.list_full_card_links);

            Resources r = Resources.getSystem();
            float twoDpTopx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());//converts 2dp to px
            float threeDpTopx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics());//converts 3dp to px

            rtCardview.setElevation(twoDpTopx);
            imdbCardview.setElevation(twoDpTopx);
            mainCardview.setElevation(threeDpTopx);
            linksLayout.setElevation(threeDpTopx);
        }*/
        return view;
    }
}