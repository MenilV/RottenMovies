package com.menil.rottenmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

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
        titleView.setSelected(true);

        TextView subtitleView = (TextView) view.findViewById(R.id.full_card_subtitle);
        subtitleView.setText("(" + String.valueOf(listMovies.get(position).year) + ")");//+listMovies.get(position).runtime+" min");

        int x = 0;//just to ensure there are no commas after the last actor
        TextView actorsView = (TextView) view.findViewById(R.id.full_card_actors);
        List<Cast> castList = listMovies.get(position).casts;
        //String castText = "Cast: ";
        String castText = "";
        for (Cast c : castList) {
            castText += c.name;
            if (++x < castList.size())
                castText += ", ";
        }
        actorsView.setText(castText);
        actorsView.setSelected(true);

        ImageView imageView = (ImageView) view.findViewById(R.id.full_card_img);
        String picURL = listMovies.get(position).posters.detailed.replace("tmb", "det");
        Ion.with(imageView)
                .placeholder(R.drawable.empty_img)
                .error(R.drawable.empty_img_error)
                .load(picURL);




        return view;
    }

}