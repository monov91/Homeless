package com.projects.radomonov.homeless.database;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.fragments.SearchFragment;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.id;
import static android.os.Build.ID;

/**
 * Created by Tom on 28.10.2017.
 */

public class DatabaseInfo {

    private static ArrayList<User> usersList;
    private static ArrayList<Offer> offersList;

    private static DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    private static DatabaseReference mDatabaseOffers = FirebaseDatabase.getInstance().getReference().child("Offers");

    public static void readUsers() {
        // Reading all users from database and fill, edit or delete them in collection
        usersList = new ArrayList<>();
        mDatabaseUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                usersList.add(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User changedUser = dataSnapshot.getValue(User.class);
                String ID = changedUser.getID();
                for (User user: usersList) {
                    if (user.getID() != null) {
                        if (user.getID().equals(ID)) {
                            usersList.remove(user);
                            break;
                        }
                    }
                }
                usersList.add(changedUser);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usersList.remove(user);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void readOffers() {
        // Reading all offers from database and fill, edit or delete them in collection
        offersList = new ArrayList<>();

        mDatabaseOffers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                offersList.add(offer);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Offer changedOffer = dataSnapshot.getValue(Offer.class);
                String ID = changedOffer.getId();
                for (Offer offer : offersList) {
                    if (offer.getId() != null) {
                        if (offer.getId().equals(ID)) {
                            offersList.remove(offer);
                            break;
                        }
                    }
                }
                offersList.add(changedOffer);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String delID = dataSnapshot.getValue(Offer.class).getId();
                for (Offer offer : offersList) {
                    if (offer.getId() != null) {
                        if (offer.getId().equals(delID)) {
                            offersList.remove(offer);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public static final List<User> getUsersList() {
        // Getting collection filled with all users in other classes
        // Made it Unmodifiable
        return Collections.unmodifiableList(usersList);
    }

    public static final List<Offer> getOffersList() {
        // Getting collection filled with all users in other classes
        // Made it Unmodifiable
        return Collections.unmodifiableList(offersList);
    }

}


