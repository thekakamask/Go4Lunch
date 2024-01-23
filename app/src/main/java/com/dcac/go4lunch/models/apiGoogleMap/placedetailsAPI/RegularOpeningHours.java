package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegularOpeningHours {
    @Expose
    @SerializedName("weekdayDescriptions")
    private List<String> weekdayDescriptions;
    @Expose
    @SerializedName("periods")
    private List<Periods> periods;
    @Expose
    @SerializedName("openNow")
    private boolean openNow;

    public List<String> getWeekdayDescriptions() {
        return weekdayDescriptions;
    }

    public void setWeekdayDescriptions(List<String> weekdayDescriptions) {
        this.weekdayDescriptions = weekdayDescriptions;
    }

    public List<Periods> getPeriods() {
        return periods;
    }

    public void setPeriods(List<Periods> periods) {
        this.periods = periods;
    }

    public boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }
}
