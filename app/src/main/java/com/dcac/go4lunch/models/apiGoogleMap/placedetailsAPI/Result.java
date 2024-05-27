package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @Expose
    @SerializedName("website")
    private String website;


    @SuppressWarnings("unused")
    @Expose
    @SerializedName("place_id")
    private String place_id;
    @SuppressWarnings("unused")
    @Expose
    @SerializedName("photos")
    private List<Photos> photos;
    @Expose
    @SerializedName("opening_hours")
    private Opening_hours opening_hours;
    @Expose
    @SerializedName("name")
    private String name;
    @SuppressWarnings("unused")
    @Expose
    @SerializedName("international_phone_number")
    private String international_phone_number;

    @Expose
    @SerializedName("icon")
    private String icon;


    @SuppressWarnings("unused")
    @Expose
    @SerializedName("adr_address")
    private String adr_address;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPlace_id() {
        return place_id;
    }

    public List<Photos> getPhotos() {
        return photos;
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

    public String getInternational_phone_number() {
        return international_phone_number;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAdr_address() {
        return adr_address;
    }

}
