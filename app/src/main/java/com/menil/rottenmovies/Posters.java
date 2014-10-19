package com.menil.rottenmovies;

import com.google.gson.annotations.SerializedName;

/**
 * Created by menil on 19.10.2014.
 */
public class Posters {

    @SerializedName("thumbnail")
    public String thumbnail;
    @SerializedName("profile")
    public String profile;
    @SerializedName("detailed")
    public String detailed;
}
