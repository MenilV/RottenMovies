package com.menil.rottenmovies;


import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Created by menil on 13.11.2014.
 */
public class FavouritesFragment extends Fragment{
    SharedPreferences sh_Pref;
    ImageView selectedImage;
    public static final String movie_id = "idKey";
    private Integer[] mImageIds = {
            R.drawable.ic_abh_small,
            R.drawable.ic_fb_small,
            R.drawable.ic_imdb_small,
            R.drawable.ic_rtlogo_small,
            R.drawable.ic_launcher_small,
            R.drawable.ic_tw_small
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
       // bindPreferenceSummaryToValue(findPreference("sync_frequency"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        if (savedInstanceState == null)
            view = inflater.inflate(R.layout.fragment_favourites_2, container, false);

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        TextView title = (TextView)view.findViewById(R.id.gallery_title);
        if(sharedpreferences.contains(movie_id))
        {
            title.setText(sharedpreferences.getString(movie_id, "noMovieID"));
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(movie_id, "spaseno");
        editor.apply();
        ActionBar actionBar = getActivity().getActionBar();
        try {
            assert actionBar != null;
            actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle("Favourites");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        //editor.commit();
        /*SharedPreferences preferences = this.getActivity().getSharedPreferences("id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", "771355766");
        editor.apply();*/
        /*Gallery gallery = (Gallery)view.findViewById(R.id.gallery1);
        selectedImage=(ImageView)view.findViewById(R.id.imageView1);
        gallery.setSpacing(1);
        gallery.setAdapter(new GalleryImageAdapter(getActivity().getApplicationContext()));

        // clicklistener for Gallery
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), "Your selected position = " + position, Toast.LENGTH_SHORT).show();
                // show the selected Image
                selectedImage.setImageResource(mImageIds[position]);
            }
        });*/
        return view;

    }
}
