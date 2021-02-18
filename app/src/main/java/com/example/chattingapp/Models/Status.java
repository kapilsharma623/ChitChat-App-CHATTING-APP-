package com.example.chattingapp.Models;

public class Status {
    private  String imageurl;
    private long timestamp;

    public Status()
    {

    }
    public Status(String imageurl, long timestamp) {
        this.imageurl = imageurl;
        this.timestamp = timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
