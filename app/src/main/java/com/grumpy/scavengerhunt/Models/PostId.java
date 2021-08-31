package com.grumpy.scavengerhunt.Models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostId {

    @Exclude
    public String PostId;
    //generic constructor for class
    public <T extends PostId> T withId (@NonNull final String id){
        this.PostId = id;
        return (T) this;
    }
}
