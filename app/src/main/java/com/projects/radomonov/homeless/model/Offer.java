package com.projects.radomonov.homeless.model;

/**
 * Created by admin on 17.10.2017.
 */

public class Offer {

    private String title;
    private int rooms;
    private int price;
    private String neighbourhood;
    private int floor;
    private String currency;

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

    public int getFloor() {
        return floor;
    }

    public String getCurrency() {
        return currency;
    }

    public Offer(String title, int rooms, int price, int floor, String neighbourhood, String currency) {
        this.title = title;
        this.rooms = rooms;
        this.price = price;
        this.floor = floor;
        this.neighbourhood = neighbourhood;
        this.currency = currency;

    }


}
