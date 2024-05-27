package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Structured_formatting {
    @Expose
    @SerializedName("secondary_text")
    private String secondary_text;
    @Expose
    @SerializedName("main_text")
    private String main_text;

    public String getSecondary_text() {
        return secondary_text;
    }

    public void setSecondary_text(String secondary_text) {
        this.secondary_text = secondary_text;
    }

    public String getMain_text() {
        return main_text;
    }

    public void setMain_text(String main_text) {
        this.main_text = main_text;
    }
}
