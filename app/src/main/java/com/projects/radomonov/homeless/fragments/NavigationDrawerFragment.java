package com.projects.radomonov.homeless.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.app.CreateOfferActivity;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.System.load;

/**
 * Created by admin on 17.10.2017.
 */

public class NavigationDrawerFragment extends android.app.Fragment implements View.OnClickListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private LinearLayout btnCreateOffer;
    private LinearLayout btnLogOut;
    private LinearLayout btnMyOffers;
    private ImageView imgEditProfile;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;

    private DatabaseReference currentUserPic;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);

        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getFragmentManager();
        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnCreateOffer = view.findViewById(R.id.btn_create_offer);
        btnMyOffers = view.findViewById(R.id.btn_my_offers);
        imgEditProfile = view.findViewById(R.id.img_edit_profile_drawer_frag);

        updateProfilePic();

        btnCreateOffer.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnMyOffers.setOnClickListener(this);
        imgEditProfile.setOnClickListener(this);

        return view;
    }

    public void updateProfilePic(){
        currentUserPic = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("profilePic");

        currentUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String picURL = dataSnapshot.getValue().toString();
                setImage(getContext(), picURL);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar){

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    private void closeNavDrawer(){
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_log_out :
                mAuth.signOut();
                break;

            case R.id.btn_create_offer :
                fragmentTransaction = fragmentManager.beginTransaction();
                CreateOfferFragment fragment = new CreateOfferFragment();
                fragmentTransaction.replace(R.id.fragment_container_main,fragment,"createOfferFrag");
                fragmentTransaction.commit();
                break;

            case R.id.btn_my_offers :
                fragmentTransaction = fragmentManager.beginTransaction();
                MyOffersFragment fragMyOffers = new MyOffersFragment();
                fragmentTransaction.replace(R.id.fragment_container_main,fragMyOffers,"myOffersFrag");
                fragmentTransaction.commit();
                break;

            case R.id.img_edit_profile_drawer_frag :
                fragmentTransaction = fragmentManager.beginTransaction();
                SetupAccountFragment setupFrag = new SetupAccountFragment();
                fragmentTransaction.replace(R.id.fragment_container_main, setupFrag, "setupAccFrag");
                fragmentTransaction.commit();
                break;

            default:  break;
        }
        mDrawerLayout.closeDrawers();
    }

    private void setImage(Context context, String imgURL) {
//        Picasso.with(context).load(imgURL).into(imgEditProfile);
        Glide.with(context).load(imgURL).override(60, 60).into(imgEditProfile);
    }



}
