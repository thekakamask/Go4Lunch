package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Predictions {
    @Expose
    @SerializedName("types")
    private List<String> types;
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
    @SerializedName("description")
    private String description;

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
