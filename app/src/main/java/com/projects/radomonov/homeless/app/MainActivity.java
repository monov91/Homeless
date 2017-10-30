package com.projects.radomonov.homeless.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

public class MainActivity extends AppCompatActivity implements SetupAccountFragment.OnFragmentUpdateListener{

    private Toolbar toolbar;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();
        setUpNavigationDrawer();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
//                    Toast.makeText(MainActivity.this, "Welcome...", Toast.LENGTH_SHORT).show();
                }
            }
        };

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        SearchFragment search = new SearchFragment();
        ft.add(R.id.fragment_container_main,search);
        ft.commit();



        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);


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
        toolbar.setTitle("Search Homes");

    }


    //for connection between fragments
    @Override
    public void updateFragment() {
        NavigationDrawerFragment fragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.nav_drawer_fragment);
        fragment.updateProfilePic();
    }

}
