package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.OffersAdapter;
import com.projects.radomonov.homeless.adapters.NeighbourhoodsAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;
import com.projects.radomonov.homeless.utilities.Utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.projects.radomonov.homeless.model.Offer.Currency.EU;

/**
 * Created by admin on 28.10.2017.
 */

public class SearchFragment extends Fragment {

    private EditText etPriceMin, etPriceMax, etRooms;
    private NeighbourhoodsAdapter adapter;
    private Spinner spinnerNeighbourhoods;
    private List<String> neighbourhoodList;
    private ImageButton btnSortAscending, btnSortDescending, btnSearchOptions, btnSearch;
    private RadioButton rdbtnBGN;
    private RadioButton rdbtnEU;
    private LinearLayout searchOptions, sortOptions;
    private List<Offer> allOffers;
    private List<Offer> searchedOffers;
    private List<Offer> primaryDisplayOffers;
    private int minPrice;
    private int maxPrice;
    private int rooms;
    private RecyclerView offersRecycler;



    // for recycle view
    public static OffersAdapter offerAdapter;

    @Override
    public void onStart() {
        super.onStart();
//        offerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        offersRecycler = view.findViewById(R.id.recycler_search_search);
        neighbourhoodList = new ArrayList<>();
        spinnerNeighbourhoods = view.findViewById(R.id.spinner_neigh);
        //spinnerNeighbourhoods.setAdapter(new ArrayAdapter<Utilities.Neighbourhood>(getContext(), android.R.layout.simple_spinner_item, Utilities.Neighbourhood.values()));
        etPriceMin = view.findViewById(R.id.et_price_min);
        spinnerNeighbourhoods.setAdapter(ArrayAdapter.createFromResource(getContext(),R.array.neighbourhoods,R.layout.support_simple_spinner_dropdown_item));
        etPriceMax = view.findViewById(R.id.et_price_max);
        etRooms = view.findViewById(R.id.et_rooms_search);
        rdbtnBGN = view.findViewById(R.id.rdbtn_BGN_search);
        rdbtnEU = view.findViewById(R.id.rdbtn_EU_search);

        searchOptions = view.findViewById(R.id.layout_search_options);
        sortOptions = view.findViewById(R.id.layout_sort);

        btnSortDescending = view.findViewById(R.id.imgbtn_descending_search);
        btnSortAscending = view.findViewById(R.id.imgbtn_ascending_search);
        btnSearchOptions = view.findViewById(R.id.imgbtn_search_options);
        btnSearch = view.findViewById(R.id.imgbtn_search);

        btnSortAscending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedOffers.sort(new Comparator<Offer>() {
                    @Override
                    public int compare(Offer offer1, Offer offer2) {
                        int price1 = offer1.getPrice();
                        if(offer1.getCurrency() == Offer.Currency.EU){
                            price1 = (int)(price1 * 1.94);
                        }
                        int price2 = offer2.getPrice();
                        if(offer2.getCurrency() == Offer.Currency.EU){
                            price2 = (int)(price2 * 1.94);
                        }
                        return price1 - price2;
                    }
                });
                offerAdapter.notifyDataSetChanged();
            }
        });

        btnSortDescending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedOffers.sort(new Comparator<Offer>() {
                    @Override
                    public int compare(Offer offer1, Offer offer2) {
                        int price1 = offer1.getPrice();
                        if(offer1.getCurrency() == Offer.Currency.EU){
                            price1 = (int)(price1 * 1.94);
                        }
                        int price2 = offer2.getPrice();
                        if(offer2.getCurrency() == Offer.Currency.EU){
                            price2 = (int)(price2 * 1.94);
                        }
                        return price2 - price1;
                    }
                });
                offerAdapter.notifyDataSetChanged();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchedOffers = new ArrayList<>();
                allOffers = DatabaseInfo.getOffersList();
                sortOptions.setVisibility(View.VISIBLE);
                boolean noRooms = false;

                if (TextUtils.isEmpty(etPriceMin.getText())) {
                    minPrice = 0;
                } else {
                    minPrice = Integer.parseInt(etPriceMin.getText().toString());
                    if (rdbtnEU.isChecked()) {
                        minPrice = (int) (minPrice * 1.94);
                    }
                }
                if (TextUtils.isEmpty(etPriceMax.getText())) {
                    maxPrice = 20000;
                } else {
                    maxPrice = Integer.parseInt(etPriceMax.getText().toString());
                    if (rdbtnEU.isChecked()) {
                        maxPrice = (int) (maxPrice * 1.94);
                    }
                }

                if (TextUtils.isEmpty(etRooms.getText())) {
                    noRooms = true;
                } else {
                    rooms = Integer.parseInt(etRooms.getText().toString());
                }
                Log.i("kriterii","rooms " + rooms);
                Log.i("kriterii","max " + maxPrice);
                Log.i("kriterii","min " + minPrice);
                Log.i("kriterii","kvartal " + neighbourhoodList.toString());
                Log.i("kriterii","oferti " + allOffers.size());
                for (Offer offer : allOffers) {
                    int offerPrice = offer.getPrice();
                    if (offer.getCurrency() == EU) {
                        offerPrice = (int) (offerPrice * 1.94);
                    }

                    if ((offerPrice >= minPrice) && (offerPrice <= maxPrice)) {
                        if (noRooms) {
                            if (neighbourhoodList.isEmpty()) {
                                searchedOffers.add(offer);
                            } else {
                                if (neighbourhoodList.contains(offer.getNeighbourhood())) {
                                    searchedOffers.add(offer);
                                }
                            }

                        } else {
                            if (rooms == offer.getRooms()) {
                                if (neighbourhoodList.isEmpty()) {
                                    searchedOffers.add(offer);
                                } else {
                                    if (neighbourhoodList.contains(offer.getNeighbourhood())) {
                                        searchedOffers.add(offer);
                                    }
                                }
                            }
                        }
                    }
                }
                for (Offer offer : searchedOffers) {
//                    Log.i("search", offer.getNeighbourhood().toString());
//                    Log.i("search", "price" + String.valueOf(offer.getPrice()));
//                    Log.i("search", "rooms" + String.valueOf(offer.getRooms()));
                }
                setUpOfferRecycler(searchedOffers,view);
                Log.i("search", "SIZE -> " + searchedOffers.size());
            }
        });

        btnSearchOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchOptions.getVisibility() == View.VISIBLE) {
                    searchOptions.setVisibility(View.GONE);
                    btnSearchOptions.setImageResource(R.drawable.down_icon);
                } else {
                    searchOptions.setVisibility(View.VISIBLE);
                    btnSearchOptions.setImageResource(R.drawable.up_icon);
                }
            }
        });

       // btnSortAscending.setOnClickListener(this);
       // btnSortDescending.setOnClickListener(this);

        setUpRecyclerNeighbourhoods(view);
        //setUpOfferRecycler(primaryDisplayOffers,view);

        spinnerNeighbourhoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String neighbourhood;
                neighbourhood =  spinnerNeighbourhoods.getSelectedItem().toString();
                neighbourhoodList.add(neighbourhood);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void setUpRecyclerNeighbourhoods(View view) {
        RecyclerView recyclerView ;
        recyclerView = view.findViewById(R.id.recycler_neighbourhoods_search);
        adapter = new NeighbourhoodsAdapter(getContext(), neighbourhoodList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    private void setUpOfferRecycler(List<Offer> offers,View view) {

        offerAdapter = new OffersAdapter(getActivity(),
                offers, new OffersAdapter.onOfferClickListener() {

            @Override
            public void onOfferClick(Offer currentOffer) {
                FragmentManager fragmentManager = getFragmentManager();

                ViewOfferFragment viewOfferFragment = new ViewOfferFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("offer", currentOffer);
                viewOfferFragment.setArguments(bundle);

                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_container_main, viewOfferFragment, "viewOfferFrag");
                ft.addToBackStack(null);
                ft.commit();
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

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        offersRecycler.setLayoutManager(manager);
        offersRecycler.setAdapter(offerAdapter);
    }


    public OffersAdapter getOfferAdapter() {
        return this.offerAdapter;
    }

}
