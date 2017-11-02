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
    private String imageThumbnail;
    private String description;
    private HashMap<String, String> imageUrls;


    public Offer() {

    }

    @Override
    public String toString() {
        return "Offer{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", rooms=" + rooms +
                ", price=" + price +
                ", neighbourhood=" + neighbourhood +
                ", currency=" + currency +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", owner='" + owner + '\'' +
                ", imageThumbnail='" + imageThumbnail + '\'' +
                ", description='" + description + '\'' +
                ", imageUrls=" + imageUrls +
                '}';
    }

    public HashMap<String, String> getImageUrls() {
        return imageUrls;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
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

    public String getDescription() {
        return description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getId() {
        return id;
    }
}
