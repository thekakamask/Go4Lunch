package com.dcac.go4lunch.models.chat;

import java.util.Date;

public class Message {

    private String text;
    private String userId;
    private String userName;
    private String userProfilePicUrl;
    private String imageUrl;
    private Date dateCreated;

    public Message() {

    }

    public Message(String text, String userId, String userName, String userProfilePicUrl, String imageUrl, Date dateCreated) {
        this.text = text;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
