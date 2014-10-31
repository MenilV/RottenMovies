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

/*
 * Created by menil on 13.10.2014.
 */
public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> listMovies = new ArrayList<Movie>();
    private int option=0;

    public GridAdapter(Context c, List<Movie> allMovies) {
        this.mContext = c;
        this.listMovies = allMovies;
    }

    public GridAdapter(Context c, List<Movie> allMovies, int option){
        this.mContext = c;
        this.listMovies = allMovies;
        this.option=option;
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
       // RemoteImageView imageView=null;
       // TextView titleView=null;
        //TextView yearView = null;
        //TextView synopsisView = null;
        //TextView runtimeView = null;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
/**PROBLEMS ARE HERE
 * BELOW
 */
            /*if (option==1) {
                view = layoutInflater.inflate(R.layout.fragment_list_item, null);
                imageView = (RemoteImageView) view.findViewById(R.id.fragment_list_item_img);
                titleView = (TextView) view.findViewById(R.id.fragment_list_item_title);
                yearView = (TextView) view.findViewById(R.id.fragment_list_item_year);
                runtimeView = (TextView) view.findViewById(R.id.fragment_list_item_runtime);
                int runtime = listMovies.get(position).runtime;
                int year = listMovies.get(position).year;
                runtimeView.setText(Integer.toString(runtime));
                yearView.setText(Integer.toString(year));
            }
            else {*/
                view = layoutInflater.inflate(R.layout.full_card_2, null);
                //imageView= (RemoteImageView) view.findViewById(R.id.full_card_img);
               // titleView = (TextView) view.findViewById(R.id.full_card_title);
           // }
            //convertView = view;
        }else{
            view = convertView;
        }

        RemoteImageView imageView= (RemoteImageView) view.findViewById(R.id.full_card_img);
        TextView  titleView = (TextView) view.findViewById(R.id.full_card_title);

            String title = listMovies.get(position).title;
            /*String synopsis = listMovies.get(position).synopsis;
            List<Cast> casts = listMovies.get(position).casts;
            ArrayList<String> actors = new ArrayList<String>();
            for (Cast cast:casts){
                actors.add(casts.get(position).name);
            }
*/
            //String releaseDate = listMovies.get(position).
            titleView.setText(title);

        String picURL = listMovies.get(position).posters.detailed.replace("tmb", "det");
        try {
            imageView.setImageURL(picURL, false);
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }


        assert imageView != null;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Kliknuto je! :D", Toast.LENGTH_LONG).show();
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