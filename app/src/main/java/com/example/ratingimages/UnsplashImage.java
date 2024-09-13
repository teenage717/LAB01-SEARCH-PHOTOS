package com.example.ratingimages;

import com.google.gson.annotations.SerializedName;

class UnsplashImage {
    @SerializedName("urls")
    public Urls urls;

    public static class Urls {
        @SerializedName("regular")
        public String regular;
    }
}