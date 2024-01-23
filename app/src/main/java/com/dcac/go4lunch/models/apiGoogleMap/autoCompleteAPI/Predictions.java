package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Predictions {
    @Expose
    @SerializedName("types")
    private List<String> types;
    @Expose
    @SerializedName("terms")
    private List<Terms> terms;
    @Expose
    @SerializedName("structured_formatting")
    private Structured_formatting structured_formatting;
    @Expose
    @SerializedName("reference")
    private String reference;
    @Expose
    @SerializedName("place_id")
    private String place_id;
    @Expose
    @SerializedName("matched_substrings")
    private List<Matched_substrings> matched_substrings;
    @Expose
    @SerializedName("description")
    private String description;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<Terms> getTerms() {
        return terms;
    }

    public void setTerms(List<Terms> terms) {
        this.terms = terms;
    }

    public Structured_formatting getStructured_formatting() {
        return structured_formatting;
    }

    public void setStructured_formatting(Structured_formatting structured_formatting) {
        this.structured_formatting = structured_formatting;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public List<Matched_substrings> getMatched_substrings() {
        return matched_substrings;
    }

    public void setMatched_substrings(List<Matched_substrings> matched_substrings) {
        this.matched_substrings = matched_substrings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
