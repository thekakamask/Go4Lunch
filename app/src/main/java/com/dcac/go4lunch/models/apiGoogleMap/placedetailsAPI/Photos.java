package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Photos {

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("photo_reference")
    private String photo_reference;

    public String getPhoto_reference() {
        return photo_reference;
    }
}
