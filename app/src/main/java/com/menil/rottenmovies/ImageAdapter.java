package com.menil.rottenmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by menil on 13.10.2014.
 */
public class ImageAdapter extends BaseAdapter {

    private int counter = 0;
    private Context mContext;
    private List<Movie> listMovies = new ArrayList<Movie>();

    public ImageAdapter(Context c, List<Movie> allMovies) {
        mContext = c;
        listMovies = allMovies;
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
        ImageView imageView;
        View view;
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (RelativeLayout) layoutInflater.inflate(R.layout.full_card, null);
            //convertView = view;
        }else{
            view = convertView;
        }

        imageView = (ImageView) view.findViewById(R.id.full_card_img);
        TextView textView = (TextView) view.findViewById(R.id.full_card_title);

            if (counter < 16) {
                //Toast.makeText(mContext, "counter je "+counter, Toast.LENGTH_LONG).show();
                String title = listMovies.get(counter).title;
            textView.setText(title);
            new DownloadImageTask(imageView).execute(listMovies.get(counter).posters.detailed.replace("tmb", "det"));
            counter++;
            }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Kliknuto je! :D", Toast.LENGTH_LONG).show();
                Bundle args = new Bundle();

                Fragment fragment = new FavouritesFragment();
                if(fragment!=null)
                    switchFragment(fragment);
                // Bundle args = new Bundle();
                //ovdje treba pokrenuti Favourites (details) fragment
                //fragmentActivity.getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                //Bundle args = new Bundle();
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}