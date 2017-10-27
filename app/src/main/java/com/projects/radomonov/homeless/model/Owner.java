package com.projects.radomonov.homeless.model;

import android.support.v4.graphics.drawable.RoundedBitmapDrawable;

import java.io.Serializable;

/**
 * Created by Tom on 21.10.2017.
 */

public class Owner implements Serializable{

    private String userName;
    private String phoneNumber;
    private String eMal;
    private RoundedBitmapDrawable userPic;

    public Owner () {

    }

    public Owner(String userName, String phoneNumber, String eMal) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.eMal = eMal;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String geteMal() {
        return eMal;
    }

    public RoundedBitmapDrawable getUserPic() {
        return userPic;
    }

    public void setUserPic(RoundedBitmapDrawable userPic) {
        this.userPic = userPic;
    }
}
