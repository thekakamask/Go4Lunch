package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class PlaceDetails {


    @Expose
    @SerializedName("formattedAddress")
    private String formattedAddress;
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("photos")
    private List<Photos> photos;
    @Expose
    @SerializedName("displayName")
    private DisplayName displayName;
    @Expose
    @SerializedName("businessStatus")
    private String businessStatus;
    @Expose
    @SerializedName("regularOpeningHours")
    private RegularOpeningHours regularOpeningHours;
    @Expose
    @SerializedName("websiteUri")
    private String websiteUri;
    @Expose
    @SerializedName("rating")
    private double rating;
    @Expose
    @SerializedName("location")
    private Location location;
    @Expose
    @SerializedName("addressComponents")
    private List<AddressComponents> addressComponents;
    @Expose
    @SerializedName("internationalPhoneNumber")
    private String internationalPhoneNumber;

    @Expose
    @SerializedName("status")
    private String status;

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public void setDisplayName(DisplayName displayName) {
        this.displayName = displayName;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public RegularOpeningHours getRegularOpeningHours() {
        return regularOpeningHours;
    }

    public void setRegularOpeningHours(RegularOpeningHours regularOpeningHours) {
        this.regularOpeningHours = regularOpeningHours;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<AddressComponents> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponents> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceDetails that = (PlaceDetails) o;
        return Double.compare(that.getRating(), getRating()) == 0 &&
                Objects.equals(getFormattedAddress(), that.getFormattedAddress()) &&
                Objects.equals(getId(), that.getId()) &&
                Objects.equals(getDisplayName(), that.getDisplayName()) &&
                Objects.equals(getRegularOpeningHours(), that.getRegularOpeningHours()) &&
                Objects.equals(getWebsiteUri(), that.getWebsiteUri()) &&
                Objects.equals(getLocation(), that.getLocation()) &&
                Objects.equals(getInternationalPhoneNumber(), that.getInternationalPhoneNumber());
    }

}
