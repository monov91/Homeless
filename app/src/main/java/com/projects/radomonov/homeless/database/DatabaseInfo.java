package com.projects.radomonov.homeless.database;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class DatabaseInfo extends AppCompatActivity {

    public interface DatabaseChangedListener {
        void onDatabaseChanged();
    }

    private static DatabaseChangedListener listener;

    private static ArrayList<User> usersList;
    private static ArrayList<Offer> offersList;

    private static DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    private static DatabaseReference mDatabaseOffers = FirebaseDatabase.getInstance().getReference().child("Offers");

    public static void readUsers() {
        usersList = new ArrayList<>();
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
        offersList = new ArrayList<>();

        mDatabaseOffers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Offer offer = dataSnapshot.getValue(Offer.class);
                offersList.add(offer);
                if (SearchFragment.offerAdapter != null) {
                    Log.i("del", "vlezna v ifa 1");
//                    SearchFragment.offerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Offer changedOffer = dataSnapshot.getValue(Offer.class);
                String ID = changedOffer.getId();

                Log.i("child", "kolko puti ---> " + 1);

                for (Offer offer : offersList) {
                    if (offer.getId() != null) {
                        if (offer.getId().equals(ID)) {
                            offersList.remove(offer);
                            break;
                        }
                    }
                }
                offersList.add(changedOffer);
//                Log.i("child", "id --- > " + ID);
//                Log.i("child", "s-value --- > " + s);

//                Offer offer = dataSnapshot.getValue(Offer.class);
//                Log.i("ofertata", offer.toString());
//                offersList.remove(offer);
//                if (SearchFragment.offerAdapter != null) {
//                    Log.i("del", "vlezna v ifa 2");
//                    SearchFragment.offerAdapter.notifyDataSetChanged();
//                }
//                Log.i("del", " dell " + offersList.size());
//                offersList.add(offer);
//                if (SearchFragment.offerAdapter != null) {
//                    Log.i("del", "vlezna v ifa 3");
//                    SearchFragment.offerAdapter.notifyDataSetChanged();
//                }
//                Log.i("del", " addd " + offersList.size());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String delID = dataSnapshot.getValue(Offer.class).getId();
                Log.i("koli4estvo", "predi remove" + offersList.size());
                for (Offer offer : offersList) {
                    if (offer.getId() != null) {
                        if (offer.getId().equals(delID)) {
                            offersList.remove(offer);
                            break;
                        }
                    }
                }


                Log.i("koli4estvo", "sled remove ------> " + offersList.size());
                if (SearchFragment.offerAdapter != null) {
                    Log.i("del", "vlezna v ifa 4");
//                    SearchFragment.offerAdapter.notifyDataSetChanged();
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
        return Collections.unmodifiableList(usersList);
    }

    public static final List<Offer> getOffersList() {
        return Collections.unmodifiableList(offersList);
    }

}


