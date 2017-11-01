package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.MyOffersAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.model.User;
import com.projects.radomonov.homeless.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Tom on 21.10.2017.
 */

public class MyOffersFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference offers;
    private DatabaseReference currentOffer;
    private String currentUserID;
    private MyOffersAdapter adapter;
    private ArrayList<Offer> myOffers;

    HashMap<String, User> usersInMyOffers = new HashMap<>();

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);
        mAuth = FirebaseAuth.getInstance();
        myOffers = new ArrayList<>();

        Log.i("ofer", "koli4estvo ----> " + DatabaseInfo.getOffersList().size());

        Log.i("dima", "List size ---> " + DatabaseInfo.getUsersList().size());
        for(int i = 0; i < DatabaseInfo.getUsersList().size(); i++) {
            Log.i("dima", "name ----- > " + DatabaseInfo.getUsersList().get(i).getNickName());
            Log.i("dima", "ID ----- > " + DatabaseInfo.getUsersList().get(i).getID());
            Log.i("dima", "pic ----- > " + DatabaseInfo.getUsersList().get(i).getProfilePic());
        }

        Log.i("dima", "Offer List size ---> " + DatabaseInfo.getOffersList().size());
        for(int i = 0; i < DatabaseInfo.getOffersList().size(); i++) {
//            Log.i("dima", "id ----- > " + DatabaseInfo.getOffersList().get(i).getId());
//            Log.i("dima", "titla ----- > " + DatabaseInfo.getOffersList().get(i).getTitle());
//            Log.i("dima", "id ----- > " + DatabaseInfo.getOffersList().get(i).getId());
//            Log.i("dima", "owner ----- > " + DatabaseInfo.getOffersList().get(i).getOwner());
//            Log.i("dima", "rajon ----- > " + DatabaseInfo.getOffersList().get(i).getNeighbourhood());
        }

        Log.i("tagche","============= list size - " + myOffers.size() + " =============");
        for (Offer offer : myOffers) {
            Log.i("tagche","title" + offer.getTitle());
            Log.i("tagche","rooms" + offer.getRooms());
            Log.i("tagche","price " + offer.getPrice());
            Log.i("tagche","neighbourhood" + offer.getNeighbourhood());
            Log.i("tagche","phone" + offer.getPhoneNumber());
        }

        getMyOffers();
        setUpRecycleView(view);
        return view;
    }

    private void insertFakeOffers(){
        Offer offer1 = new Offer("titla1",1,100,"kvartal1", Offer.Currency.EU,"https://firebasestorage.googleapis.com/v0/b/homeless-ddf77.appspot.com/o/OfferImages%2Fimage%3A76?alt=media&token=1a107cb2-d2d6-4ecf-9d26-20a9f21dd49b");
        Offer offer2 = new Offer("titla2",2,200,"kvartal2", Offer.Currency.BGN,"https://firebasestorage.googleapis.com/v0/b/homeless-ddf77.appspot.com/o/OfferImages%2Fimage%3A76?alt=media&token=1a107cb2-d2d6-4ecf-9d26-20a9f21dd49b");
        Offer offer3 = new Offer("titla3",3,300,"kvartal3", Offer.Currency.BGN,"https://firebasestorage.googleapis.com/v0/b/homeless-ddf77.appspot.com/o/OfferImages%2Fimage%3A76?alt=media&token=1a107cb2-d2d6-4ecf-9d26-20a9f21dd49b");
        myOffers.add(offer1);
        myOffers.add(offer2);
        myOffers.add(offer3);
    }

    private void getMyOffers() {
        final ArrayList<String> allOffers = new ArrayList<>();

        currentUserID = mAuth.getCurrentUser().getUid().toString();
        offers = FirebaseDatabase.getInstance().getReference().child("Offers");

        offers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    allOffers.add(child.getKey());
                }
                for (final String offerID : allOffers) {
                    currentOffer = FirebaseDatabase.getInstance().getReference().child("Offers").child(offerID);
                    currentOffer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Offer offer = dataSnapshot.getValue(Offer.class);
                            if (offer.getOwner().equals(currentUserID)) {
                                myOffers.add(offer);
                               /* Utilities.getInstance().getAllOffers().add(offer);
                                Log.i("tagche", "OFFERS SIZE" + myOffers.size());
                                Log.i("tagche", offer.getImage());
                                Log.i("tagche", offer.getTitle());
                                Log.i("tagche", offer.getPhoneNumber());
                                Log.i("tagche", offer.getOwner());
                                Log.i("tagche", ""+offer.getRooms());
                                Log.i("tagche", ""+offer.getPrice());
                                Log.i("tagche",offer.getNeighbourhood());*/
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("log","Error on canceled");
                        }
                    });
                }
                Log.i("tagche","Offers SIZE deba- " + myOffers.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("log","Error on canceled");
            }
        });
    }

    private void setUpRecycleView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_offers);
        adapter = new MyOffersAdapter(getActivity(), myOffers,
                new MyOffersAdapter.onOfferClickListener() {

            @Override
            public void onOfferClick(Offer currentOffer) {
                FragmentManager fragmentManager = getFragmentManager();
                CreateOfferFragment createOfferFragment = new CreateOfferFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("offer",currentOffer);
                createOfferFragment.setArguments(bundle);

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container_main,createOfferFragment,"frag");
                ft.commit();
                //prati kam edit offer fragment
            }
        });
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }


}
