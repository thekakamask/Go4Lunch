package com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearbySearch {


    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("results")
    private List<Results> results;

    @SuppressWarnings("unused")
    @Expose
    @SerializedName("next_page_token")
    private String nextPageToken;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
