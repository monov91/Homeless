package com.projects.radomonov.homeless.model;

import android.net.Uri;

/**
 * Created by admin on 17.10.2017.
 */

public class Offer {

    private String title;
    private int rooms;
    private int price;
    private String neighbourhood;
    private String currency;
    private String phoneNumber;
    private String owner;
    private String image;

    public String getOwner() {
        return owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public int getRooms() {
        return rooms;
    }

    public int getPrice() {
        return price;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public String getCurrency() {
        return currency;
    }


    public Offer(){

    }

    public Offer(String title, int rooms, int price,  String neighbourhood, String currency,String imgDownloadUrl,String phone, String ownerID) {
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.neighbourhood = neighbourhood;
        this.currency = currency;
        this.image = imgDownloadUrl;
        this.phoneNumber = phone;
        this.owner = ownerID;
    }


}
