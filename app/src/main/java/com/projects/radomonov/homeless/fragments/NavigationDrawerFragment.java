package com.projects.radomonov.homeless.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.app.LoginActivity;
import com.projects.radomonov.homeless.app.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.System.load;

/**
 * Created by admin on 17.10.2017.
 */

public class NavigationDrawerFragment extends android.app.Fragment implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout btnCreateOffer;
    private LinearLayout btnLogOut;
    private LinearLayout btnMyOffers;
    private LinearLayout btnMyFavouriteOffers;
    private ImageView imgEditProfile;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth mAuth;
    private RoundedBitmapDrawable round;
    private View view;
    private DatabaseReference currentUserPic;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);

        initialiseData();

        if(mAuth != null) {
            updateProfilePic();
        }

        setListeners();

        return view;
    }
    // Using this Stream in updateProfilePic method
    InputStream input = null;
    public void updateProfilePic(){
        // In this method we are taking current user profile picture,
        // cropping it with RoundedBitmapDrawableFactory by using Asynctask,
        // make it round and setting it to imgEditProfile to this fragment
        if (mAuth.getCurrentUser() != null) {
            currentUserPic = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.users_directory_DB))
                    .child(mAuth.getCurrentUser().getUid()).child(getResources().getString(R.string.profilePic_in_user_DB));

            currentUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        final String picURL = dataSnapshot.getValue().toString();
                        new AsyncTask<Void, Void, Bitmap>() {
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
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar){

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
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
                if(hasPermissions()) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    CreateOfferFragment fragment = new CreateOfferFragment();
                    fragmentTransaction.replace(R.id.fragment_container_main,fragment,getResources().getString(R.string.create_offer_frag_tag));
                    fragmentTransaction.commit();
                } else {
                    requestPermissionWithRationale();
                }
                break;


            case R.id.btn_my_offers :
                fragmentTransaction = fragmentManager.beginTransaction();
                MyOffersFragment fragMyOffers = new MyOffersFragment();
                fragmentTransaction.replace(R.id.fragment_container_main,fragMyOffers,getResources().getString(R.string.my_offers_frag_tag));
                fragmentTransaction.commit();
                break;

            case R.id.btn_my_favourite_offers :
                fragmentTransaction = fragmentManager.beginTransaction();
                MyFavouriteOffersFragment fragMyFavouriteOffers = new MyFavouriteOffersFragment();
                fragmentTransaction.replace(R.id.fragment_container_main,fragMyFavouriteOffers,getResources().getString(R.string.my_fav_offers_frag_tag));
                fragmentTransaction.commit();
                break;

            case R.id.img_edit_profile_drawer_frag :
                if(hasPermissions()) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    SetupAccountFragment setupFrag = new SetupAccountFragment();
                    fragmentTransaction.replace(R.id.fragment_container_main, setupFrag, getResources().getString(R.string.setup_acc_frag_tag));
                    fragmentTransaction.commit();
                } else {
                    requestPermissionWithRationale();
                }
                break;

            default:  break;
        }
        closeNavDrawer();
    }

    private void setListeners() {
        btnCreateOffer.setOnClickListener(this);
        btnLogOut.setOnClickListener(this);
        btnMyOffers.setOnClickListener(this);
        imgEditProfile.setOnClickListener(this);
        btnMyFavouriteOffers.setOnClickListener(this);
    }

    private void initialiseData() {
        mAuth = FirebaseAuth.getInstance();
        fragmentManager = getFragmentManager();
        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnCreateOffer = view.findViewById(R.id.btn_create_offer);
        btnMyOffers = view.findViewById(R.id.btn_my_offers);
        btnMyFavouriteOffers = view.findViewById(R.id.btn_my_favourite_offers);
        imgEditProfile = view.findViewById(R.id.img_edit_profile_drawer_frag);
    }


    //for requesting permissions
    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = getActivity().checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (!allowed){
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(getActivity(), "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }

    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(getActivity().findViewById(R.id.drawer_layout_main), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getActivity(),
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(getActivity().findViewById(R.id.drawer_layout_main), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

}
