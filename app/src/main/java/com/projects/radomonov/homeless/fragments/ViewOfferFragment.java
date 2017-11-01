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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.ViewPagerAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;


public class ViewOfferFragment extends android.app.Fragment {

    private ImageView image;
    private TextView tvRoomsNeigborhood, tvPrice, tvDescription;
    private Button btnMakeCall, btnWriteEmail;

    private Offer currentOffer;

    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_view, container, false);

        image = view.findViewById(R.id.img_view_offer_info);
        tvRoomsNeigborhood = view.findViewById(R.id.text_rooms_neigborhood_offer_info);
        tvPrice = view.findViewById(R.id.text_price_per_month_offer_info);
        tvDescription = view.findViewById(R.id.text_description_offer_info);
        btnMakeCall = view.findViewById(R.id.btn_call_to_owner_offer_info);
        btnWriteEmail = view.findViewById(R.id.btn_write_an_email_offer_info);

        //for sliding images
        viewPager = view.findViewById(R.id.view_pager_offer_view);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(viewPagerAdapter);


        Bundle bundle = getArguments();
        if (bundle != null) {
            currentOffer = (Offer) getArguments().getSerializable("offer");
            fillFields();
        }

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


        return view;
    }

    public void fillFields() {
//        Glide.with(getContext()).load(currentOffer.getImageThumbnail()).into(image);
//        Glide.with(getContext()).load(offer.getImageThumbnail()).into(imgbtnChoose);
        tvRoomsNeigborhood.setText(currentOffer.getRooms() + "-стаен, кв. " + currentOffer.getNeighbourhood());
        tvPrice.setText(currentOffer.getPrice() + " " + currentOffer.getCurrency() + "/мес.");
        tvDescription.setText("tuk shte bude opisanieto");
    }

}
