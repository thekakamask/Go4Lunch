package com.dcac.go4lunch.models;

public class ParametersItem {

    private final String title;
    private final String description;
    private boolean isChecked;

    public ParametersItem(String title, String description, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
    }



    public String getDescription() {
        return description;
    }



    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }


}
