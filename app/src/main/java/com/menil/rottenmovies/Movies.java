package com.menil.rottenmovies;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/*
 * Created by menil on 17.10.2014.
 */
public class Movies implements Serializable {

    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

}

class Movie implements Serializable, Comparable<Movie> {

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
    @SerializedName("genres")
    public List<String> genres;


    public String getId() {
        return this.id;
    }


    @Override
    public int compareTo(Movie m) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date date2 = null;
        try {
            date = formatter.parse(m.release_dates.theater);
            date2 = formatter.parse(this.release_dates.theater);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date2.compareTo(date);
    }

}

class IMDB implements Serializable {

    @SerializedName("imdb")
    private String imdb;

    public String getIMDB() {
        return imdb;
    }

}

class Link implements Serializable {

    @SerializedName("alternate")
    public String alternate;

}

class Posters implements Serializable {

    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("profile")
    public String profile;
    @SerializedName("detailed")
    public String detailed;

}

class Ratings implements Serializable {

    @SerializedName("critics_score")
    public String critics_score;

}

class ReleaseDates implements Serializable {

    @SerializedName("theater")
    public String theater;

}

class Cast implements Serializable {
    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public String id;

}

class Reviews {
    List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

}

class Review {
    @SerializedName("critic")
    public String critic;

    @SerializedName("quote")
    public String quote;

    @SerializedName("publication")
    public String publication;
}

class Clips {

    List<Clip> clips;

    public List<Clip> getClips() {
        return clips;
    }

}

class Clip {
    String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }
}
