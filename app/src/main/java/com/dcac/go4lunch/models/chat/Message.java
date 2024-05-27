package com.dcac.go4lunch.models.chat;

import java.util.Date;

public class Message {

    private String text;
    private String userName;
    private String userProfilePicUrl;
    private String imageUrl;
    private Date dateCreated;

    public Message() {

    }

    public Message(String text, String userName, String userProfilePicUrl, String imageUrl, Date dateCreated) {
        this.text = text;
        this.userName = userName;
        this.userProfilePicUrl = userProfilePicUrl;
        this.imageUrl = imageUrl;
        this.dateCreated = dateCreated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDateCreated() {
        return dateCreated;
    }
}
