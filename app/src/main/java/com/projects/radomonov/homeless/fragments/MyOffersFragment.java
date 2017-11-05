package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.OffersAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tom on 21.10.2017.
 */

public class MyOffersFragment extends Fragment {
    private TextView title;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);
        initialiseVariables(view);
        List<Offer> myOffers = getMyOffers();
        setUpRecycleView(view,myOffers);

        return view;
    }

    private List<Offer> getMyOffers(){
        // Getting current user offer from collection with all offers
        List<Offer> allOffers = DatabaseInfo.getOffersList();
        List<Offer> myOfferss = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid().toString();
        for(Offer offer : allOffers){
            if(offer.getOwner().equals(currentUserID)){
                myOfferss.add(offer);
            }
        }
        return myOfferss;
    }

    private void setUpRecycleView(View view,List<Offer> offers) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_offers);
        OffersAdapter adapter = new OffersAdapter(getActivity(), offers,
                new OffersAdapter.onOfferClickListener() {

            @Override
            public void onOfferClick(Offer currentOffer) {
                FragmentManager fragmentManager = getFragmentManager();
                CreateOfferFragment createOfferFragment = new CreateOfferFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable(getResources().getString(R.string.offer_bundle_tag_create_offer),currentOffer);
                createOfferFragment.setArguments(bundle);

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container_main, createOfferFragment, getResources().getString(R.string.create_offer_frag_tag));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    private void initialiseVariables(View view){
        title = view.findViewById(R.id.tv_title_of_layout);
        title.setText(R.string.my_offers_title);
    }


}
