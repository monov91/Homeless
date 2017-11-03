package com.projects.radomonov.homeless.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.List;


public class ViewOfferFragment extends android.app.Fragment implements View.OnClickListener {

    private TextView tvRoomsNeigborhood, tvPrice, tvDescription;
    private Button btnMakeCall, btnWriteEmail;
    private CheckBox cbLikeThisOffer;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUser;
    private DatabaseReference favouriteOfferID;
    private DatabaseReference favouriteOffers;
    private Offer currentOffer;
    private ViewPager viewPager;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_offer_view, container, false);

        initialiseData();

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentOffer = (Offer) getArguments().getSerializable("offer");
            fillFields();
        }

        btnMakeCall.setOnClickListener(this);
        btnWriteEmail.setOnClickListener(this);

        if (MainActivity.getFavouriteOffersList().contains(currentOffer.getId())) {
            cbLikeThisOffer.setChecked(true);
        }

        cbLikeThisOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbLikeThisOffer.isChecked()) {
                    favouriteOfferID.setValue(currentOffer.getId());
                }
                if (!cbLikeThisOffer.isChecked()) {
                    String currentOfferID = currentOffer.getId();
                    favouriteOffers.child(currentOfferID).removeValue();
                }
            }
        });

        return view;
    }

    private void fillFields() {
        tvRoomsNeigborhood.setText(currentOffer.getRooms() + "-стаен, кв. " + currentOffer.getNeighbourhood());
        tvPrice.setText(currentOffer.getPrice() + " " + currentOffer.getCurrency() + "/мес.");
        tvDescription.setText(currentOffer.getDescription());
    }

    private void initialiseData() {
        favouriteOfferID = currentUser.child("favouriteOffers").child(currentOffer.getId());
        favouriteOffers = currentUser.child("favouriteOffers");
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        tvRoomsNeigborhood = view.findViewById(R.id.text_rooms_neigborhood_offer_info);
        tvPrice = view.findViewById(R.id.text_price_per_month_offer_info);
        tvDescription = view.findViewById(R.id.text_description_offer_info);
        btnMakeCall = view.findViewById(R.id.btn_call_to_owner_offer_info);
        btnWriteEmail = view.findViewById(R.id.btn_write_an_email_offer_info);
        cbLikeThisOffer = view.findViewById(R.id.cb_like_this_offer);

        //for sliding images
        viewPager = view.findViewById(R.id.view_pager_offer_view);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), currentOffer);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_call_to_owner_offer_info:
                String phoneNumber = currentOffer.getPhoneNumber();
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
                return;

            case R.id.btn_write_an_email_offer_info:
                String ownerID = currentOffer.getOwner();
                String ownerEmail = null;
                for (int i = 0; i < DatabaseInfo.getUsersList().size(); i++) {
                    if (DatabaseInfo.getUsersList().get(i).getID().equals(ownerID)) {
                        ownerEmail = DatabaseInfo.getUsersList().get(i).geteMail();
                    }
                }
                if (ownerEmail != null) {
                    Intent eMailIntent = new Intent(Intent.ACTION_SENDTO);
                    eMailIntent.setData(Uri.parse("mailto:" + ownerEmail));
                    startActivity(eMailIntent);
                }
                return;
        }
    }
}
