package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Photos {
    @Expose
    @SerializedName("authorAttributions")
    private List<AuthorAttributions> authorAttributions;
    @Expose
    @SerializedName("heightPx")
    private int heightPx;
    @Expose
    @SerializedName("widthPx")
    private int widthPx;
    @Expose
    @SerializedName("name")
    private String name;

    public List<AuthorAttributions> getAuthorAttributions() {
        return authorAttributions;
    }

    public void setAuthorAttributions(List<AuthorAttributions> authorAttributions) {
        this.authorAttributions = authorAttributions;
    }

    public int getHeightPx() {
        return heightPx;
    }

    public void setHeightPx(int heightPx) {
        this.heightPx = heightPx;
    }

    public int getWidthPx() {
        return widthPx;
    }

    public void setWidthPx(int widthPx) {
        this.widthPx = widthPx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
