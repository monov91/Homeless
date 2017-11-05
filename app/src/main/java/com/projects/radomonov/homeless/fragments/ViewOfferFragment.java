package com.projects.radomonov.homeless.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.ViewPagerAdapter;
import com.projects.radomonov.homeless.app.MainActivity;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ViewOfferFragment extends android.app.Fragment implements View.OnClickListener {

    private TextView tvRoomsNeigborhood, tvPrice, tvDescription, tvTitle;
    private ImageView btnMakeCall, btnWriteEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUser;
    private DatabaseReference favouriteOfferID;
    private DatabaseReference favouriteOffers;
    private Offer currentOffer;
    private ViewPager viewPager;
    private View view;
    private ImageView profilePic;
    private TextView userNameEt;
    private RoundedBitmapDrawable round;
    private DatabaseReference currentUserPic;
    private DatabaseReference offerOwner;
    private ImageView btnLikeDislike;
    private boolean isLiked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offer_view, container, false);

        initialiseData();


        Bundle bundle = getArguments();
        if (bundle != null) {
            currentOffer = (Offer) getArguments().getSerializable("offer");
            fillFields();
        }
        getOfferOwner();
        setOwnerName();
        updateProfilePic();

        favouriteOfferID = currentUser.child("favouriteOffers").child(currentOffer.getId());
        favouriteOffers = currentUser.child("favouriteOffers");

        //for sliding images
        viewPager = view.findViewById(R.id.view_pager_offer_view);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), currentOffer);
        viewPager.setAdapter(viewPagerAdapter);

        btnMakeCall.setOnClickListener(this);
        btnWriteEmail.setOnClickListener(this);
        btnLikeDislike.setOnClickListener(this);

        if (MainActivity.getFavouriteOffersList().contains(currentOffer.getId())) {
            setStarChecked("true");
        }



        return view;
    }
    InputStream input = null;
    public void updateProfilePic() {
        // In this method we are taking current user profile picture,
        // cropping it with RoundedBitmapDrawableFactory by using Asynctask,
        // make it round and setting it to profilePic to this fragment
        currentUserPic = offerOwner.child("profilePic");

        if(currentUserPic != null) {
            currentUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
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

                                profilePic.setImageDrawable(round);
                                super.onPostExecute(object);
                            }
                        }.execute();
//                    setImage(getContext(), picURL);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void fillFields() {
        tvTitle.setText(currentOffer.getTitle());
        tvRoomsNeigborhood.setText("Rooms: " + currentOffer.getRooms() + " in " + currentOffer.getNeighbourhood());
        tvPrice.setText(currentOffer.getPrice() + " " + currentOffer.getCurrency() + "/мес.");
        tvDescription.setText(currentOffer.getDescription());
    }

    private void initialiseData() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        tvRoomsNeigborhood = view.findViewById(R.id.text_rooms_neigborhood_offer_info);
        tvPrice = view.findViewById(R.id.text_price_per_month_offer_info);
        tvTitle = view.findViewById(R.id.text_offer_title_view_offer);
        tvDescription = view.findViewById(R.id.text_description_offer_info);
        btnMakeCall = view.findViewById(R.id.btn_call_to_owner_offer_info);
        btnWriteEmail = view.findViewById(R.id.btn_write_an_email_offer_info);
        profilePic = view.findViewById(R.id.profile_pic_view_offer);
        userNameEt = view.findViewById(R.id.tv_user_name_view_offer);
        btnLikeDislike = view.findViewById(R.id.image_star_like_this_offer);
//        btnLikeDislike.setClickable(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_call_to_owner_offer_info:
                String phoneNumber = currentOffer.getPhoneNumber();
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                break;

            case R.id.image_star_like_this_offer :
                if(isLiked) {
                    String currentOfferID = currentOffer.getId();
                    favouriteOffers.child(currentOfferID).removeValue();
                    setStarChecked("false");
                } else {
                    favouriteOfferID.setValue(currentOffer.getId());
                    setStarChecked("true");
                }
                break;

            case R.id.btn_write_an_email_offer_info:
                String ownerID = currentOffer.getOwner();
                String ownerEmail = null;
                List<User> users = DatabaseInfo.getUsersList();
                for(User user : users){
                    Log.i("user",user.toString());
                    if(user.getID().equals(ownerID)){
                        ownerEmail = user.geteMail();
                    }
                }
                Log.i("user","email  = " + ownerEmail);
                if (ownerEmail != null) {
                    Intent eMailIntent = new Intent(Intent.ACTION_SENDTO);
                    eMailIntent.setData(Uri.parse("mailto:" + ownerEmail));
                    startActivity(eMailIntent);
                }
                break;
        }
    }

    private void setOwnerName() {
        String offerOwnerID = currentOffer.getOwner();
        String offerOwnerName = null;
        for(int i = 0; i < DatabaseInfo.getUsersList().size(); i++) {
            if(offerOwnerID.equals(DatabaseInfo.getUsersList().get(i).getID())) {
                offerOwnerName = DatabaseInfo.getUsersList().get(i).getNickName();
            }
        }
        userNameEt.setText(offerOwnerName);
    }

    private void getOfferOwner() {
        String offerOwnerID = currentOffer.getOwner();
        offerOwner = FirebaseDatabase.getInstance().getReference().child("Users").child(offerOwnerID);
    }

    private void setStarChecked(String trueOrFalse) {
        if(trueOrFalse.equals("true")){
            isLiked = true;
            btnLikeDislike.setImageResource(R.drawable.star_btn_full);
        }
        if(trueOrFalse.equals("false")) {
            isLiked = false;
            btnLikeDislike.setImageResource(R.drawable.star_btn_empty);
        }
    }


}
