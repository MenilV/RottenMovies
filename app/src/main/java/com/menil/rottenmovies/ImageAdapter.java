package com.menil.rottenmovies;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by menil on 13.10.2014.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c){
        mContext=c;
    }
    @Override
    public int getCount() {
        return mThumbIds.length;
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
        ImageView imageView=null;

        if (convertView==null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //ViewGroup parent2 = (ViewGroup) findViewById(R.id.full_card);
            //layoutInflater.inflate(R.id.full_card_img, parent);
            RelativeLayout view = (RelativeLayout) layoutInflater.inflate(R.layout.full_card, null);//wtf do you want here
            convertView = view;
        }

        imageView=(ImageView) convertView.findViewById(R.id.full_card_img);
        imageView.setImageResource(mThumbIds[position]);
        TextView textView = (TextView) convertView.findViewById(R.id.full_card_title);
        textView.setText("Nesto");
        return convertView;
    }

    private Integer[] mThumbIds={
            R.drawable.dracula_tmb, R.drawable.pic_tmb, R.drawable.buck, R.drawable.nesto_det,
            R.drawable.buck, R.drawable.nesto_det,
            R.drawable.gone_girl, R.drawable.pic_tmb,
            R.drawable.dracula_tmb, R.drawable.pic_tmb
    };
}
