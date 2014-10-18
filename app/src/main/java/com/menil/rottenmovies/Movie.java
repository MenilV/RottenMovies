package com.menil.rottenmovies;

import com.google.gson.annotations.SerializedName;

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

    //public Posters posters;

    //public Cast cast;
    //I will ignore abridged cast for now

    //public aternateIDs aids;

    //public Link links

}
