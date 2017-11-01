package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.MyOffersAdapter;
import com.projects.radomonov.homeless.adapters.NeighbourhoodsAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.onClick;
import static android.os.Build.VERSION_CODES.M;
import static java.security.AccessController.getContext;
import static java.security.AccessController.getContext;
import static java.security.AccessController.getContext;
import static java.security.AccessController.getContext;
import static java.security.AccessController.getContext;
import static java.security.AccessController.getContext;

/**
 * Created by admin on 28.10.2017.
 */

public class SearchFragment extends Fragment implements MyOffersAdapter.onOfferClickListener{

    private EditText etPriceMin,etPriceMax,etRooms;
    private NeighbourhoodsAdapter adapter;
    private Spinner spinnerNeighbourhoods;
    private List<Utilities.Neighbourhood> neighbourhoodList;
    private ImageButton btnSortAscending,btnSortDescending;

    // for recycle view
    private MyOffersAdapter offerAdapter;

    @Override
    public void onStart() {
        super.onStart();
        offerAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        neighbourhoodList = new ArrayList<>();
        spinnerNeighbourhoods = view.findViewById(R.id.spinner_neigh);
        spinnerNeighbourhoods.setAdapter(new ArrayAdapter<Utilities.Neighbourhood>(getContext(), android.R.layout.simple_spinner_item, Utilities.Neighbourhood.values()));
        etPriceMin = view.findViewById(R.id.et_price_min);
        etPriceMax = view.findViewById(R.id.et_price_max);
        etRooms = view.findViewById(R.id.et_rooms_search);

        btnSortDescending = view.findViewById(R.id.imgbtn_descending_search);
        btnSortAscending = view.findViewWithTag(R.id.imgbtn_ascending_search);

//        btnSortAscending.setOnClickListener(this);
//        btnSortDescending.setOnClickListener(this);

        setUpRecycler(view);
        setUpOfferRecycler(view);

        spinnerNeighbourhoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Utilities.Neighbourhood neighbourhood;
                neighbourhood = (Utilities.Neighbourhood) spinnerNeighbourhoods.getSelectedItem();
                neighbourhoodList.add(neighbourhood);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void setUpRecycler(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_neighbourhoods_search);
        adapter = new NeighbourhoodsAdapter(getContext(),neighbourhoodList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    private void setUpOfferRecycler(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_my_offers_search_frag);
        offerAdapter = new MyOffersAdapter(getActivity(),
                DatabaseInfo.getOffersList(), new MyOffersAdapter.onOfferClickListener() {

                    @Override
                    public void onOfferClick(Offer currentOffer) {
                        FragmentManager fragmentManager = getFragmentManager();

                        ViewOfferFragment viewOfferFragment = new ViewOfferFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("offer", currentOffer);
                        viewOfferFragment.setArguments(bundle);

                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.fragment_container_main, viewOfferFragment, "frag").commit();
//                        CreateOfferFragment createOfferFragment = new CreateOfferFragment();
//
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("offer", currentOffer);
//                        createOfferFragment.setArguments(bundle);
//
//                        FragmentTransaction ft = fragmentManager.beginTransaction();
//                        ft.replace(R.id.fragment_container_main, createOfferFragment, "frag");

                    }
                });

        recyclerView.setAdapter(offerAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);


    }


    @Override
    public void onOfferClick(Offer currentOffer) {

    }

    public MyOffersAdapter getOfferAdapter() {
        return this.offerAdapter;
    }

}
