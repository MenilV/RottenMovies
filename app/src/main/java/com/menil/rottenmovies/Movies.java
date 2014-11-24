package com.menil.rottenmovies;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/*
 * Created by menil on 17.10.2014.
 */
public class Movies {

    private List<Movie> movies;

    public List<Movie> getMovies(){
        return movies;
    }


}

/*
 * Created by menil on 17.10.2014.
 */
class Movie implements Parcelable, Serializable, Comparable<Movie> {
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @SerializedName("id")
    public String id;
    @SerializedName("title")
    public String title;
    @SerializedName("year")
    public String year;
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

    public Movie(Parcel in) {
        readFromParcel(in);
    }

    public String getId() {
        return this.id;
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this);
    }


    @Override
    public int compareTo(Movie another) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
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


class IMDB {
    @SerializedName("imdb")
    private String imdb;

    public String getIMDB() {
        return imdb;
    }
}

class Link {
    @SerializedName("alternate")
    public String alternate;
}

class Posters {

    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("profile")
    public String profile;
    @SerializedName("detailed")
    public String detailed;

}

class Ratings {

    @SerializedName("critics_score")
    public String critics_score;
}

class ReleaseDates {

    @SerializedName("theater")
    public String theater;
}

class Cast {
    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public String id;
}
