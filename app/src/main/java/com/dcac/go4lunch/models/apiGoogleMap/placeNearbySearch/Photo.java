package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("photo_reference")
    private String photoReference;


    public String getPhotoReference() {
        return photoReference;
    }

}
