package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {
    @Expose
    @SerializedName("user_ratings_total")
    private int user_ratings_total;
    @Expose
    @SerializedName("types")
    private List<String> types;
    @Expose
    @SerializedName("reference")
    private String reference;
    @Expose
    @SerializedName("rating")
    private double rating;
    @Expose
    @SerializedName("plus_code")
    private Plus_code plus_code;
    @Expose
    @SerializedName("place_id")
    private String place_id;
    @Expose
    @SerializedName("opening_hours")
    private Opening_hours opening_hours;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("geometry")
    private Geometry geometry;
    @Expose
    @SerializedName("vicinity")
    private String vicinity;
    @Expose
    @SerializedName("photos")
    private List<Photo> photos;

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Plus_code getPlus_code() {
        return plus_code;
    }

    public void setPlus_code(Plus_code plus_code) {
        this.plus_code = plus_code;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Opening_hours getOpening_hours() {
        return opening_hours;
    }

    public void setOpening_hours(Opening_hours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
    public List<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
