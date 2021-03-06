package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.OffersAdapter;
import com.projects.radomonov.homeless.app.MainActivity;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;

/**
 * Created by Tom on 02.11.2017.
 */

public class MyFavouriteOffersFragment extends Fragment {

    private ArrayList<Offer> myFavouriteOffers;
    private OffersAdapter adapter;
    private TextView title;

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);
        initialiseVariables(view);
        getMyFavouriteOffers();
        setUpRecycleView(view);
        return view;
    }

    private void getMyFavouriteOffers() {
        // Getting all offers, that have Like button checked and adding them into myFavouriteOffers collection
        myFavouriteOffers = new ArrayList<>();
        for(int i = 0; i < MainActivity.getFavouriteOffersList().size(); i++) {
            for(Offer offer : DatabaseInfo.getOffersList()) {
                if(MainActivity.getFavouriteOffersList().get(i).equals(offer.getId())) {
                    myFavouriteOffers.add(offer);
                }
            }
        }
    }


    private void setUpRecycleView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_offers);
        adapter = new OffersAdapter(getActivity(), myFavouriteOffers,
                new OffersAdapter.onOfferClickListener() {
                    @Override
                    public void onOfferClick(Offer currentOffer) {
                        FragmentManager fragmentManager = getFragmentManager();
                        ViewOfferFragment viewOfferFragment = new ViewOfferFragment();

                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getResources().getString(R.string.offer_bundle_tag_view_offer),currentOffer);
                        viewOfferFragment.setArguments(bundle);

                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.fragment_container_main, viewOfferFragment, getResources().getString(R.string.view_offer_frag_tag));
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    private void initialiseVariables(View view) {
        title = view.findViewById(R.id.tv_title_of_layout);
        title.setText(R.string.my_favourite_offers_title);
    }

}
