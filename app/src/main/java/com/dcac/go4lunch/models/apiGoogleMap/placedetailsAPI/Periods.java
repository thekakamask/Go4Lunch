package com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Periods {
    @Expose
    @SerializedName("close")
    private Close close;
    @Expose
    @SerializedName("open")
    private Open open;

    public Close getClose() {
        return close;
    }

    public void setClose(Close close) {
        this.close = close;
    }

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }
}
