package com.menil.rottenmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * Created by menil on 17.10.2014.
 */
public class Movie implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this);
    }

    public Movie(String id, String name, int type) {

        //this.id = Objects.requireNonNull(id);
       // this.title = Objects.requireNonNull(name);
        this.year = type;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in.readString(), in.readString(), in.readInt());
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
