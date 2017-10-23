package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.MyOffersAdapter;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 21.10.2017.
 */

public class MyOffersFragment extends Fragment {


    private FirebaseAuth mAuth;
    private DatabaseReference offers;
    private DatabaseReference currentOffer;
    private String currentUserID;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);

        mAuth = FirebaseAuth.getInstance();

        setUpRecycleView(view);

        return view;

    }

//    private String readData(String child, DatabaseReference offerDB) {
//        DatabaseReference childRef = offerDB.child(child);
//        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                data = dataSnapshot.getValue().toString();
//                Log.i("tag",data);
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.i("tag"," cancel ");
//            }
//        });
//
//        return data;
//    }

    private List<Offer> getMyOffers() {
        final ArrayList<String> allOffers = new ArrayList<>();
        final ArrayList<Offer> myOffers = new ArrayList<>();


        currentUserID = mAuth.getCurrentUser().getUid().toString();
        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        offers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    allOffers.add(child.getKey());
                }
                for (String offerID : allOffers) {
                    currentOffer = FirebaseDatabase.getInstance().getReference().child("Offers").child(offerID);
                    currentOffer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Offer offer = dataSnapshot.getValue(Offer.class);
                            if (offer.getOwner().equals(currentUserID)) {
                                myOffers.add(offer);
                                Log.i("log", offer.getImage());
                                Log.i("log", offer.getTitle());
                                Log.i("log", offer.getPhoneNumber());
                                Log.i("log", offer.getOwner());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myOffers;
    }

    private void setUpRecycleView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_offers);
        MyOffersAdapter adapter = new MyOffersAdapter(getActivity(), getMyOffers());
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }
}
