package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Structured_formatting {
    @Expose
    @SerializedName("secondary_text")
    private String secondary_text;
    @Expose
    @SerializedName("main_text_matched_substrings")
    private List<Main_text_matched_substrings> main_text_matched_substrings;
    @Expose
    @SerializedName("main_text")
    private String main_text;

    public String getSecondary_text() {
        return secondary_text;
    }

    public void setSecondary_text(String secondary_text) {
        this.secondary_text = secondary_text;
    }

    public List<Main_text_matched_substrings> getMain_text_matched_substrings() {
        return main_text_matched_substrings;
    }

    public void setMain_text_matched_substrings(List<Main_text_matched_substrings> main_text_matched_substrings) {
        this.main_text_matched_substrings = main_text_matched_substrings;
    }

    public String getMain_text() {
        return main_text;
    }

    public void setMain_text(String main_text) {
        this.main_text = main_text;
    }
}
