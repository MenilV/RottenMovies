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
    private String savedDate, tag;
    private ArrayList<String> bannedDates = new ArrayList<String>();
    private ArrayList<Integer> uniquePositionDates= new ArrayList<Integer>();

    public ListsAdapter(Context context, List<Movie> allMovies, String tag) {
        this.mContext = context;
        this.listMovies = allMovies;
        this.tag=tag;
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


    public void setHeader(int position, View view){
        LinearLayout headerLayout = (LinearLayout)view.findViewById(R.id.header_layout);
        TextView headerText = (TextView)view.findViewById(R.id.header_text);
        String months[]={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String prettyDate;
        String currentDate;

        if (position==0) {
            headerLayout.setVisibility(View.VISIBLE);
            savedDate = listMovies.get(position).release_dates.theater;
            prettyDate=months[Integer.parseInt(savedDate.substring(5,7))-1]+" "+ savedDate.substring(8,10);
            headerText.setText(prettyDate);
            bannedDates.add(savedDate);
            uniquePositionDates.add(position);

        } else {
            currentDate=listMovies.get(position).release_dates.theater;

            if (bannedDates.contains(currentDate) && !uniquePositionDates.contains(position))
                headerLayout.setVisibility(View.GONE);
            else
            {
                headerLayout.setVisibility(View.VISIBLE);
                prettyDate=months[Integer.parseInt(currentDate.substring(5,7))-1]+" "+currentDate.substring(8,10);
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

        if (tag.equals("BOXOFFICE")) {
            LinearLayout headerLayout = (LinearLayout) view.findViewById(R.id.header_layout);
            headerLayout.setVisibility(View.GONE);
        }else {
            setHeader(position, view);//setting the header for upcoming and opening movies
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
        titleView.setSelected(true);

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
        final String IMDBiD;

        if (listMovies.get(position).imdb==null)
            IMDBiD="0000000";
        else
            IMDBiD=listMovies.get(position).imdb.getIMDB();

        //IMDB direct link to Movie
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

        //Rotten Movies direct link to Movie
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

        //Add to favourites button
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

}
