package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @Expose
    @SerializedName("longitude")
    private double longitude;
    @Expose
    @SerializedName("latitude")
    private double latitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
