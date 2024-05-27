package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("lng")
    private double lng;

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("lat")
    private double lat;

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }
}
