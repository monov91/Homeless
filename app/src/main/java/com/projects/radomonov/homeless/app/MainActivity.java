package com.projects.radomonov.homeless.app;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.fragments.CreateOfferFragment;
import com.projects.radomonov.homeless.fragments.NavigationDrawerFragment;
import com.projects.radomonov.homeless.fragments.SearchFragment;
import com.projects.radomonov.homeless.fragments.SetupAccountFragment;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SetupAccountFragment.OnFragmentUpdateListener{

    private static SearchFragment searchFragInstance;
    public static SearchFragment getSearchFragInstance() {
        if (searchFragInstance == null) {
            return new SearchFragment();
        } else {
            return searchFragInstance;
        }
    }

    private Toolbar toolbar;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference currentUser;
    private static ArrayList<String> favouriteOffersList;
    private DatabaseReference mDatabaseFavouriteOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpNavigationDrawer();
        DatabaseInfo.readUsers();
        DatabaseInfo.readOffers();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.fragment_container_main, getSearchFragInstance(), "searchFrag");
        ft.commit();


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        if(mAuth.getCurrentUser() != null) {
            String currentUserID = mAuth.getCurrentUser().getUid();
            mDatabaseFavouriteOffers = mDatabaseUsers.child(currentUserID).child("favouriteOffers");
        }
        readFavouriteOffers();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void setUpNavigationDrawer(){
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawerFragment.setUpDrawer(R.id.nav_drawer_fragment,drawerLayout,toolbar);

    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Homeless");
    }


    //for connection between fragments
    @Override
    public void updateFragment() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        fragment.updateProfilePic();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment currFrag = fragmentManager.findFragmentById(R.id.fragment_container_main);

        if (currFrag == fragmentManager.findFragmentByTag("searchFrag")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Making custom alertDiialog
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_layout, null);
            TextView title = view.findViewById(R.id.tv_title_alert);
            ImageView imageView = view.findViewById(R.id.image_btn_alert);

            title.setText("Warning");
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ezhik_belii_png));

            builder.setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.this.finish();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.setView(view);
            builder.show();
//        } else
//        if(currFrag == fragmentManager.findFragmentByTag("viewOfferFrag")) {
//            getFragmentManager().popBackStack();
//        }
//        else
//        if (currFrag == fragmentManager.findFragmentByTag("createOfferFrag")) {
//            getFragmentManager().popBackStack();
//        }
        } else {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container_main, getSearchFragInstance(), "searchFrag");
            ft.commit();
        }
    }

    public void readFavouriteOffers() {
        // Getting current user favourite offers
        favouriteOffersList = new ArrayList<>();
        if(mDatabaseFavouriteOffers != null) {
            mDatabaseFavouriteOffers.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String offerID = (String) dataSnapshot.getValue();
                    favouriteOffersList.add(offerID);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String offerID = (String) dataSnapshot.getValue();
                    favouriteOffersList.remove(offerID);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    public static final List<String> getFavouriteOffersList() {
        return favouriteOffersList;
    }

}
