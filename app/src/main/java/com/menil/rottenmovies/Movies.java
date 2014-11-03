package com.menil.rottenmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by menil on 17.10.2014.
 */
public class Movies implements Parcelable{

    //@SerializedName("total")
    //private String total;

    public List<Movie> movies;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(movies);
    }
}
