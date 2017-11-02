package com.projects.radomonov.homeless.database;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tom on 28.10.2017.
 */

public class DatabaseInfo extends AppCompatActivity {

    private static ArrayList<User> usersList = new ArrayList<>();
    private static ArrayList<Offer> offersList = new ArrayList<>();
    private static ArrayList<Offer> favouriteOffersList = new ArrayList<>();

    private static DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    private static DatabaseReference mDatabaseOffers = FirebaseDatabase.getInstance().getReference().child("Offers");
//    private static DatabaseReference mDatabaseFavouriteOffers = mDatabaseUsers.child("favouriteOffers");
//    private static DatabaseReference mDatabaseFavouriteOffers = FirebaseDatabase.getInstance().getReference().child("Users").child("favouriteOffers");

//    private OffersAdapter adapter = SearchFragment.getOfferAdapter();

    public static void readUsers() {
        mDatabaseUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                usersList.add(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                usersList.remove(s);
                usersList.add(user);
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

//    public static void readUsers() {
//        final ArrayList<String> allUsersByID = new ArrayList<>();
//
//        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot child : dataSnapshot.getChildren()) {
//                    allUsersByID.add(child.getKey());
//                }
//                for (final String userID : allUsersByID) {
//                    currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
//                    currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            User user = dataSnapshot.getValue(User.class);
//                            usersList.add(user);
//                        }
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            Log.i("users", "onCancelled");
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.i("users", "5");
//            }
//        });
//
//    }

    public static void readOffers() {
        mDatabaseOffers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                offersList.add(offer);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                offersList.remove(s);
                offersList.add(offer);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                offersList.remove(offer);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

//    public static void readFavouriteOffers() {
//        mDatabaseFavouriteOffers.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Offer offer = dataSnapshot.getValue(Offer.class);
//                favouriteOffersList.add(offer);
//                Log.i("of", "dobavi se v Database");
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////                Offer offer = dataSnapshot.getValue(Offer.class);
////                favouriteOffersList.remove(s);
////                favouriteOffersList.add(offer);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Offer offer = dataSnapshot.getValue(Offer.class);
//                favouriteOffersList.remove(offer);
//                Log.i("of", "iztri se ot Database");
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }



    public static final List<User> getUsersList() {
        return Collections.unmodifiableList(usersList);
    }

    public static final List<Offer> getOffersList() {
        return Collections.unmodifiableList(offersList);
    }

//    public static final List<Offer> getFavouriteOffersList() {
//        return Collections.unmodifiableList(favouriteOffersList);
//    }

}


