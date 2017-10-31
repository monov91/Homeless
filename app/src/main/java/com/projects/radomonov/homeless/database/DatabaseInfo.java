package com.projects.radomonov.homeless.database;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tom on 28.10.2017.
 */

public class DatabaseInfo extends AppCompatActivity {

    private static DatabaseReference currentUser;
    private static DatabaseReference currentOffer;


    private static ArrayList<User> usersList = new ArrayList<>();
    private static ArrayList<Offer> offersList = new ArrayList<>();

    private static DatabaseReference mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
    private static DatabaseReference mDatabaseOffers = FirebaseDatabase.getInstance().getReference().child("Offers");



    public static void readUsers() {
        final ArrayList<String> allUsersByID = new ArrayList<>();

        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    allUsersByID.add(child.getKey());
                }
                for (final String userID : allUsersByID) {
                    currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                    currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            usersList.add(user);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("users", "onCancelled");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("users", "5");
            }
        });

    }




    public static void readOffers() {
        final ArrayList<String> allOffers = new ArrayList<>();

        mDatabaseOffers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    allOffers.add(child.getKey());
                }
                for (final String userID : allOffers) {
                    currentOffer = FirebaseDatabase.getInstance().getReference().child("Offers").child(userID);
                    currentOffer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Offer offer = dataSnapshot.getValue(Offer.class);
                            offersList.add(offer);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("users", "onCancelled");
                        }
                    });
                }
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

//   public static HashMap<String, User> getUsers() {
////    public static ArrayList<User> getUsers() {
//       Log.i("users", "1");
//       mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(DataSnapshot dataSnapshot) {
//               Log.i("users", "2");
//
//               Log.i("users", "3");
//               Log.e("users " ," ===== childrens ========" + dataSnapshot.getChildrenCount());
//               for (DataSnapshot user : dataSnapshot.getChildren()) {
//                   Log.i("users", "41");
//                   String userName = user.child("nickName").toString();
//                   Log.i("users", "42");
//                   String eMail = user.child("eMail").toString();
//                   Log.i("users", "43");
//                   String phoneNumber = user.child("phoneNumber").toString();
//                   Log.i("users", "44");
//                   String ID = user.child("ID").toString();
//                   Log.i("users", "45");
//                   Uri profilePic = Uri.parse(user.child("profilePic").toString());
//                   Log.i("users", "46");
//                   users.put(ID, new User(userName, eMail,phoneNumber, profilePic, ID));
////                   users.add(new User(userName, eMail,phoneNumber, profilePic, ID));
//               }
//           }
//           @Override
//           public void onCancelled(DatabaseError databaseError) {
//               Log.i("users", "5");
//           }
//       });
//
//       Log.i("users", "6");
//       return users;
//   }

//    public static ArrayList<User> users2 = getUsers();
//    public static HashMap<String, User> users2 = getUsers();
}


