package com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Matched_substrings {
    @Expose
    @SerializedName("offset")
    private int offset;
    @Expose
    @SerializedName("length")
    private int length;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
