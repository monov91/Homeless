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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.ViewPagerAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.List;


public class ViewOfferFragment extends android.app.Fragment {

    private ImageView image;
    private TextView tvRoomsNeigborhood, tvPrice, tvDescription;
    private Button btnMakeCall, btnWriteEmail;
    private CheckBox cbLikeThisOffer;

    private FirebaseAuth mAuth;
    private DatabaseReference currentUser;
    private DatabaseReference favouriteOffers;

    private Offer currentOffer;
    private List<Offer> allOffers;
    private ArrayList<Offer> favouriteOffersList;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_view, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        favouriteOffers = currentUser.child("favouriteOffers").push();
        favouriteOffersList = new ArrayList<>();

        image = view.findViewById(R.id.img_view_offer_info);
        tvRoomsNeigborhood = view.findViewById(R.id.text_rooms_neigborhood_offer_info);
        tvPrice = view.findViewById(R.id.text_price_per_month_offer_info);
        tvDescription = view.findViewById(R.id.text_description_offer_info);
        btnMakeCall = view.findViewById(R.id.btn_call_to_owner_offer_info);
        btnWriteEmail = view.findViewById(R.id.btn_write_an_email_offer_info);
        cbLikeThisOffer = view.findViewById(R.id.cb_like_this_offer);

        allOffers = DatabaseInfo.getOffersList();

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentOffer = (Offer) getArguments().getSerializable("offer");
            fillFields();
        }

        //for sliding images
        viewPager = view.findViewById(R.id.view_pager_offer_view);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), currentOffer);
        viewPager.setAdapter(viewPagerAdapter);

        btnMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = currentOffer.getPhoneNumber();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            }
        });

        btnWriteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ownerID = currentOffer.getOwner();
                String ownerEmail = null;
                for (int i = 0; i < DatabaseInfo.getUsersList().size(); i++) {

                    Log.i("userID", "userID " + DatabaseInfo.getUsersList().get(i).getID());

                    if (DatabaseInfo.getUsersList().get(i).getID().equals(ownerID)) {
                        ownerEmail = DatabaseInfo.getUsersList().get(i).geteMail();
                    }
                }
                if (ownerEmail != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + ownerEmail));
                    startActivity(intent);
                }
            }
        });


        cbLikeThisOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (Offer of : allOffers) {
                    Log.i("of", "vsi4ki --- > " + allOffers.size());
                    Log.i("of", "favourite --- > " + favouriteOffersList.size());
                    if(cbLikeThisOffer.isChecked()) {
                        favouriteOffersList.add(of);
                        favouriteOffers.setValue(currentOffer);
                        Log.i("of", "vlezna v ifa --- > " + favouriteOffersList.size());
                    } else {
                        for (Offer oferta : allOffers) {
                            if(oferta == of) {
                                favouriteOffersList.remove(of);
                                Log.i("of", "vlezna v else, iztri --- > " + favouriteOffersList.size());
                            }
                        }

                    }
                }

            }
        });


        return view;
    }

    public void fillFields() {
//        Glide.with(getContext()).load(currentOffer.getImageThumbnail()).into(image);
//        Glide.with(getContext()).load(offer.getImageThumbnail()).into(imgbtnChoose);
        tvRoomsNeigborhood.setText(currentOffer.getRooms() + "-стаен, кв. " + currentOffer.getNeighbourhood());
        tvPrice.setText(currentOffer.getPrice() + " " + currentOffer.getCurrency() + "/мес.");
        tvDescription.setText(currentOffer.getDescription());
    }

    public List<Offer> getFavouriteOffers() {
        return (List<Offer>) favouriteOffers ;
    }
}
