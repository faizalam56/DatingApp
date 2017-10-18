package com.example.senzec.datingapp.models;

import android.util.Log;

/**
 * Created by power hashing on 4/24/2017.
 */

public class User {

   /* // region Member Variables
    private String avatarUrl;
    private String displayName;
    private String username;
    // endregion

    // region Constructors
    public User(){

    }
    // endregion

    // region Getters
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }
    // endregion

    // region Setters
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    // endregion*/

    private String avatarUrl;
    private String displayName;
    private String displayAge;
    private String userDistance;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayAge() {
        return displayAge;
    }

    public void setDisplayAge(String displayAge) {
        this.displayAge = displayAge;
    }

    public String getUserDistance() {
        return userDistance;
    }

    public void setUserDistance(String userDistance) {
        this.userDistance = userDistance;
    }

    @Override
    public String toString() {
        Log.d("User...","Name="+displayName+" Age="+displayAge);
        return super.toString();
    }
}
