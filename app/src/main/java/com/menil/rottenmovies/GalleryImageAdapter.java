package com.menil.rottenmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by menil on 16.11.2014.
 */public class GalleryImageAdapter extends BaseAdapter
{
    private Context mContext;

    private Integer[] mImageIds = {
            R.drawable.ic_abh_small,
            R.drawable.ic_fb_small,
            R.drawable.ic_imdb_small,
            R.drawable.ic_rtlogo_small,
            R.drawable.ic_launcher_small,
            R.drawable.ic_tw_small
    };

    public GalleryImageAdapter(Context context)
    {
        mContext = context;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }


    // Override this method according to your need
    public View getView(int index, View view, ViewGroup viewGroup)
    {
        // TODO Auto-generated method stub
        ImageView i = new ImageView(mContext);

        i.setImageResource(mImageIds[index]);
        i.setLayoutParams(new Gallery.LayoutParams(200, 200));

        i.setScaleType(ImageView.ScaleType.FIT_XY);

        return i;
    }
}

