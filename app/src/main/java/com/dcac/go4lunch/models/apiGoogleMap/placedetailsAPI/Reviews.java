package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reviews {

    @Expose
    @SerializedName("translated")
    private boolean translated;
    @Expose
    @SerializedName("time")
    private int time;
    @Expose
    @SerializedName("text")
    private String text;
    @Expose
    @SerializedName("relative_time_description")
    private String relative_time_description;
    @Expose
    @SerializedName("rating")
    private int rating;
    @Expose
    @SerializedName("profile_photo_url")
    private String profile_photo_url;
    @Expose
    @SerializedName("original_language")
    private String original_language;
    @Expose
    @SerializedName("language")
    private String language;
    @Expose
    @SerializedName("author_url")
    private String author_url;
    @Expose
    @SerializedName("author_name")
    private String author_name;

    public boolean isTranslated() {
        return translated;
    }

    public void setTranslated(boolean translated) {
        this.translated = translated;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRelative_time_description() {
        return relative_time_description;
    }

    public void setRelative_time_description(String relative_time_description) {
        this.relative_time_description = relative_time_description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getProfile_photo_url() {
        return profile_photo_url;
    }

    public void setProfile_photo_url(String profile_photo_url) {
        this.profile_photo_url = profile_photo_url;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
}
