package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @Expose
    @SerializedName("photo_reference")
    private String photoReference;

    @Expose
    @SerializedName("height")
    private int height;

    @Expose
    @SerializedName("width")
    private int width;

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
