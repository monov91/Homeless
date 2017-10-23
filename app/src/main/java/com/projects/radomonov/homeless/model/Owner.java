package com.projects.radomonov.homeless.model;

/**
 * Created by Tom on 21.10.2017.
 */

public class Owner {

    private String userName;
    private String phoneNumber;
    private String eMal;

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
}
