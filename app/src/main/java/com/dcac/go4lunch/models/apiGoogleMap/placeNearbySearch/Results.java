package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Results {

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("rating")
    private double rating;

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("place_id")
    private String place_id;
    @Expose
    @SerializedName("name")
    private String name;

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("geometry")
    private Geometry geometry;

    @Expose
    @SerializedName("vicinity")
    private String vicinity;

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("photos")
    private List<Photo> photos;

    public double getRating() {
        return rating;
    }

    public String getPlace_id() {
        return place_id;
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

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
