package com.grumpy.scavengerhunt.Models;

import androidx.annotation.NonNull;

import java.util.Date;

public class ImagePost extends PostId{

    private String image;
    private String user;
    private String caption;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getCaption() {
        return caption;
    }

    public Date getTime() {
        return time;
    }
}
