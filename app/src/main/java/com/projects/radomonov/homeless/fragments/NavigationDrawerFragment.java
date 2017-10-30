package com.projects.radomonov.homeless.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import java.io.IOException;
import java.io.InputStream;

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
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference currentUserPic;
    private DatabaseReference currentUser;


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

        String currentUserID = mAuth.getCurrentUser().getUid();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        if(currentUser.child("profilePic") != null) {
            Log.i("plamen", "1");
            updateProfilePic();
        }


        btnCreateOffer.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnMyOffers.setOnClickListener(this);
        imgEditProfile.setOnClickListener(this);

        return view;
    }

    private RoundedBitmapDrawable round;

    InputStream input = null;

    public void updateProfilePic(){
        currentUserPic = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("profilePic");

        currentUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    final String picURL = dataSnapshot.getValue().toString();
                    new AsyncTask<Void,Void,Bitmap>(){
                        @Override
                        protected Bitmap doInBackground(Void... voids) {
                            try {
                                input = new java.net.URL(picURL).openStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bitmap image = BitmapFactory.decodeStream(input);
                            return image;
                        }

                        @Override
                        protected void onPostExecute(Bitmap object) {
                            round = RoundedBitmapDrawableFactory.create(getResources(), object);
                            round.setCircular(true);

                            imgEditProfile.setImageDrawable(round);
                            super.onPostExecute(object);
                        }
                    }.execute();





                    //Bitmap image = StringToBitMap(picURL);

                    Log.e("vlado4",picURL);

//                    setImage(getContext(), picURL);
                }

                Log.e("vlado5",dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
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
