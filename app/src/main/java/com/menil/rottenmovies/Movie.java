package com.menil.rottenmovies;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by menil on 17.10.2014.
 */
public class Movie {
    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("year")
    public int year;

    @SerializedName("mpaa_rating")
    public String mpaa_rating;

    @SerializedName("runtime")
    public int runtime;

    @SerializedName("critics_consensus")
    public String critics_consensus;

    //public ReleaseDates releaseDates;

    //public Ratings ratings;

    @SerializedName("synopsis")
    public String synopsis;

    @SerializedName("posters")
    public Posters posters;

    @SerializedName("abridged_cast")
    public List<Cast> casts;

    //public aternateIDs aids;

    //public Link links

}
