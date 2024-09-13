package com.example.ratingimages;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnsplashResponse {
    @SerializedName("results")
    public List<UnsplashImage> results;
}