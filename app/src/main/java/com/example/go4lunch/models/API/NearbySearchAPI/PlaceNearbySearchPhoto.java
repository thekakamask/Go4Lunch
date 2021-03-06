package com.example.go4lunch.models.API.NearbySearchAPI;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearbySearchPhoto {

    @SerializedName("html_attributions")
    private List<String> mHtmlAttributions;

    @SerializedName("height")
    private Long mHeight;

    @SerializedName("photo_reference")
    private String mPhotoReference;

    @SerializedName("width")
    private Long mWidth;

    public List<String> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public Long getHeight() {
        return mHeight;
    }

    public void setHeight(Long height) {
        mHeight = height;
    }

    public String getPhotoReference() {
        return mPhotoReference;
    }

    public void setPhotoReference(String photoReference) {
        mPhotoReference = photoReference;
    }

    public Long getWidth() {
        return mWidth;
    }

    public void setWidth(Long width) {
        mWidth = width;
    }
}
