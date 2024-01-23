package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Terms {
    @Expose
    @SerializedName("value")
    private String value;
    @Expose
    @SerializedName("offset")
    private int offset;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
