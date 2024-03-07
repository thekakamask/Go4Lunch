package com.dcac.go4lunch.models;

public class ParametersItem {

    private String title;
    private String description;
    private boolean isChecked;

    public ParametersItem(String title, String description, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setTitle(String title) {
        this.title = title;
    }
}
