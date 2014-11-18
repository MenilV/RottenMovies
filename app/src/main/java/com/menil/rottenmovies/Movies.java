package com.menil.rottenmovies;



        import android.os.Parcel;
        import android.os.Parcelable;
        import android.util.Log;

        import com.google.gson.annotations.SerializedName;

        import java.io.Serializable;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Comparator;
        import java.util.Date;
        import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by menil on 17.10.2014.
 */
public class Movies {

    public List<Movie> movies;
}
/*
 * Created by menil on 17.10.2014.
 */
class Movie implements Parcelable, Serializable, Comparable<Movie> {
    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("year")
    public int year;
    @SerializedName("mpaa_rating")
    public String mpaa_rating;
    @SerializedName("runtime")
    public String runtime;
    @SerializedName("release_dates")
    public ReleaseDates release_dates;
    @SerializedName("ratings")
    public Ratings ratings;
    @SerializedName("synopsis")
    public String synopsis;
    @SerializedName("posters")
    public Posters posters;
    @SerializedName("abridged_cast")
    public List<Cast> casts;
    @SerializedName("alternate_ids")
    public IMDB imdb;
    @SerializedName("links")
    public Link links;

    public String getId() {
        return this.id;
    }

    public Movie(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this);
    }


    @Override
    public int compareTo(Movie another) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date=null;
        Date date2 = null;
        try {
            date = formatter.parse(another.release_dates.theater);
            date2 = formatter.parse(this.release_dates.theater);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2.compareTo(date);
    }

}

