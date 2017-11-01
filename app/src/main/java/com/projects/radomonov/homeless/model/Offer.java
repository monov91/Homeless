package com.projects.radomonov.homeless.model;

import android.net.Uri;

import com.projects.radomonov.homeless.utilities.Utilities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 17.10.2017.
 */

public class Offer implements Serializable {
    public enum Currency {
        EU, BGN
    }

    private String id;
    private String title;
    private int rooms;
    private int price;
    private Utilities.Neighbourhood neighbourhood;
    private Currency currency;
    private String phoneNumber;
    private String owner;

    public HashMap<String,String> getImageUrls() {
        return imageUrls;
    }

    private HashMap<String,String> imageUrls;



    public String getImageThumbnail() {
        return imageThumbnail;
    }

    private String imageThumbnail;
    public Offer() {

    }


    public Offer(String title, int rooms, int price, Utilities.Neighbourhood neighbourhood, Currency currency, String imgDownloadUrl, String phone, String ownerID) {
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.neighbourhood = neighbourhood;
        this.currency = currency;
        this.phoneNumber = phone;
        this.owner = ownerID;


    }

    public Offer(String title, int rooms, int price, Utilities.Neighbourhood neighbourhood, Currency currency, String image) {
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.neighbourhood = neighbourhood;
        this.currency = currency;
    }

    public String getOwner() {
        return owner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public Utilities.Neighbourhood getNeighbourhood() {
        return neighbourhood;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getId() {
        return id;
    }
}
