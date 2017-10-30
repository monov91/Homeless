package com.projects.radomonov.homeless.model;

import android.net.Uri;

/**
 * Created by Tom on 28.10.2017.
 */

public class User {

    private String nickName;
    private String eMail;
    private String phoneNumber;
    private String profilePic;
    private String ID;

    public User() {

    }

    public User(String nickName, String eMail, String phoneNumber, String profilePic, String ID) {
        this.nickName = nickName;
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
        this.ID = ID;
    }

    public String getNickName() {
        return nickName;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getID() {
        return ID;
    }
}
