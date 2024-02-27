package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {
    @Expose
    @SerializedName("wheelchair_accessible_entrance")
    private boolean wheelchair_accessible_entrance;
    @Expose
    @SerializedName("website")
    private String website;
    @Expose
    @SerializedName("vicinity")
    private String vicinity;
    @Expose
    @SerializedName("utc_offset")
    private int utc_offset;
    @Expose
    @SerializedName("user_ratings_total")
    private int user_ratings_total;
    @Expose
    @SerializedName("url")
    private String url;
    @Expose
    @SerializedName("types")
    private List<String> types;
    @Expose
    @SerializedName("reviews")
    private List<Reviews> reviews;
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
    @SerializedName("photos")
    private List<Photos> photos;
    @Expose
    @SerializedName("opening_hours")
    private Opening_hours opening_hours;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("international_phone_number")
    private String international_phone_number;
    @Expose
    @SerializedName("icon_mask_base_uri")
    private String icon_mask_base_uri;
    @Expose
    @SerializedName("icon_background_color")
    private String icon_background_color;
    @Expose
    @SerializedName("icon")
    private String icon;
    @Expose
    @SerializedName("geometry")
    private Geometry geometry;
    @Expose
    @SerializedName("formatted_phone_number")
    private String formatted_phone_number;
    @Expose
    @SerializedName("formatted_address")
    private String formatted_address;
    @Expose
    @SerializedName("editorial_summary")
    private Editorial_summary editorial_summary;
    @Expose
    @SerializedName("current_opening_hours")
    private Current_opening_hours current_opening_hours;
    @Expose
    @SerializedName("business_status")
    private String business_status;
    @Expose
    @SerializedName("adr_address")
    private String adr_address;
    @Expose
    @SerializedName("address_components")
    private List<Address_components> address_components;

    public boolean isWheelchair_accessible_entrance() {
        return wheelchair_accessible_entrance;
    }

    public void setWheelchair_accessible_entrance(boolean wheelchair_accessible_entrance) {
        this.wheelchair_accessible_entrance = wheelchair_accessible_entrance;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public int getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(int utc_offset) {
        this.utc_offset = utc_offset;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
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

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
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

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getIcon_mask_base_uri() {
        return icon_mask_base_uri;
    }

    public void setIcon_mask_base_uri(String icon_mask_base_uri) {
        this.icon_mask_base_uri = icon_mask_base_uri;
    }

    public String getIcon_background_color() {
        return icon_background_color;
    }

    public void setIcon_background_color(String icon_background_color) {
        this.icon_background_color = icon_background_color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Editorial_summary getEditorial_summary() {
        return editorial_summary;
    }

    public void setEditorial_summary(Editorial_summary editorial_summary) {
        this.editorial_summary = editorial_summary;
    }

    public Current_opening_hours getCurrent_opening_hours() {
        return current_opening_hours;
    }

    public void setCurrent_opening_hours(Current_opening_hours current_opening_hours) {
        this.current_opening_hours = current_opening_hours;
    }

    public String getBusiness_status() {
        return business_status;
    }

    public void setBusiness_status(String business_status) {
        this.business_status = business_status;
    }

    public String getAdr_address() {
        return adr_address;
    }

    public void setAdr_address(String adr_address) {
        this.adr_address = adr_address;
    }

    public List<Address_components> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<Address_components> address_components) {
        this.address_components = address_components;
    }
}
