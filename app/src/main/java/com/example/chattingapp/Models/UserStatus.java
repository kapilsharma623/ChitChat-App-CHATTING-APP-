package com.example.chattingapp.Models;

import java.util.ArrayList;

public class UserStatus {
    private String name,profileimage;
    private long lastUpdated;
    private ArrayList<Status> statuses;

    public UserStatus()
    {

    }

    public UserStatus(String name, String profileimage, long lastUpdated, ArrayList<Status> statuses) {
        this.name = name;
        this.profileimage = profileimage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
