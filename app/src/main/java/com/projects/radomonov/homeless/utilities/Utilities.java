package com.projects.radomonov.homeless.utilities;

import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 24.10.2017.
 */

public class Utilities {
    private static Utilities instance;

    private List<Offer> allOffers;
    private List<Offer> myOffers;

    private Utilities(){
        this.allOffers = new ArrayList<>();
        this.myOffers = new ArrayList<>();
    }


    public List<Offer> getAllOffers(){
        return this.allOffers;
    }

    public static Utilities getInstance(){
        if(instance == null){
            instance = new Utilities();
        }
        return instance;
    }

}
