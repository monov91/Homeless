package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.adapters.OffersAdapter;
import com.projects.radomonov.homeless.adapters.NeighbourhoodsAdapter;
import com.projects.radomonov.homeless.database.DatabaseInfo;
import com.projects.radomonov.homeless.model.Offer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.projects.radomonov.homeless.model.Offer.Currency.EU;

/**
 * Created by admin on 28.10.2017.
 */

public class SearchFragment extends Fragment implements View.OnClickListener{

    private EditText etPriceMin, etPriceMax, etRooms;
    private List<String> neighbourhoodList;
    private ImageButton btnSortAscending, btnSortDescending;
    private ImageView btnSearch,btnSearchOptions;
    private RadioButton rdbtnEU;
    private LinearLayout searchOptions, sortOptions;
    private TextView tvSearchOptionsMessage;
    private List<Offer> allOffers;
    private ArrayList<Offer> searchedOffers;

    private Spinner spinnerNeighbourhoods;
    private NeighbourhoodsAdapter neighbourhoodsAdapter;
    private OffersAdapter offerAdapter;
    private RecyclerView offersRecycler;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        initialiseVariables(view);
        setOnClickListeners();
        setUpNeighbourhoodsChoosing(view);

        return view;
    }

    private void setUpNeighbourhoodsChoosing(View view) {
        /*
        String[] arr = getResources().getStringArray(R.array.neighbourhoods);
        ArrayAdapter adapter2 = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,arr) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setMinimumHeight(70);
                return v;
            }
        };
         */
        String[] arr = getResources().getStringArray(R.array.neighbourhoods);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,arr){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setMinimumHeight(50);
                return v;
            }
        };

        //spinnerNeighbourhoods.setAdapter(ArrayAdapter.createFromResource(getContext(), R.array.neighbourhoods, R.layout.support_simple_spinner_dropdown_item));
        spinnerNeighbourhoods.setAdapter(spinnerAdapter);
        setUpRecyclerNeighbourhoods(view);

        spinnerNeighbourhoods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String neighbourhood;
                neighbourhood = spinnerNeighbourhoods.getSelectedItem().toString();
                if(!neighbourhoodList.contains(neighbourhood)){
                    neighbourhoodList.add(neighbourhood);
                }
                neighbourhoodsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void initialiseVariables(View view) {
        tvSearchOptionsMessage = view.findViewById(R.id.tv_search_options_message_search);
        offersRecycler = view.findViewById(R.id.recycler_search_search);
        neighbourhoodList = new ArrayList<>();
        spinnerNeighbourhoods = view.findViewById(R.id.spinner_neigh);
        etPriceMin = view.findViewById(R.id.et_price_min);
        etPriceMax = view.findViewById(R.id.et_price_max);
        etRooms = view.findViewById(R.id.et_rooms_search);
        rdbtnEU = view.findViewById(R.id.rdbtn_EU_search);
        searchOptions = view.findViewById(R.id.layout_search_options);
        sortOptions = view.findViewById(R.id.layout_sort);
        btnSortDescending = view.findViewById(R.id.imgbtn_descending_search);
        btnSortAscending = view.findViewById(R.id.imgbtn_ascending_search);
        btnSearchOptions = view.findViewById(R.id.imgbtn_search_options);
        btnSearch = view.findViewById(R.id.imgbtn_search);
    }

    private void setUpRecyclerNeighbourhoods(View view) {
        RecyclerView recyclerView;
        recyclerView = view.findViewById(R.id.recycler_neighbourhoods_search);
        neighbourhoodsAdapter = new NeighbourhoodsAdapter(getContext(), neighbourhoodList);
        recyclerView.setAdapter(neighbourhoodsAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }

    private void setUpOfferRecycler(List<Offer> offers, View view) {

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
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        offersRecycler.setLayoutManager(manager);
        offersRecycler.setAdapter(offerAdapter);
    }

    private void setOnClickListeners(){
        btnSearch.setOnClickListener(this);
        btnSortAscending.setOnClickListener(this);
        btnSortDescending.setOnClickListener(this);
        btnSearchOptions.setOnClickListener(this);
    }

    private int getCalculatedMinPrice(){
        int minPrice = 0;
        if (!TextUtils.isEmpty(etPriceMin.getText())) {
            minPrice = Integer.parseInt(etPriceMin.getText().toString());
            if (rdbtnEU.isChecked()) {
                minPrice = (int) (minPrice * 1.94);
            }
        }
        return  minPrice;
    }
    private int getCalculatedMaxPrice() {
        int maxPrice = 20000;
        if (!TextUtils.isEmpty(etPriceMax.getText())) {
            maxPrice = Integer.parseInt(etPriceMax.getText().toString());
            if (rdbtnEU.isChecked()) {
                maxPrice = (int) (maxPrice * 1.94);
            }
        }
        return maxPrice;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_search :
                searchedOffers = new ArrayList<>();
                allOffers = DatabaseInfo.getOffersList();
                sortOptions.setVisibility(View.VISIBLE);
                boolean noRooms = false;
                int rooms = 0;
                int minPrice = getCalculatedMinPrice();
                int maxPrice = getCalculatedMaxPrice();

                if (TextUtils.isEmpty(etRooms.getText())) {
                    noRooms = true;
                } else {
                    rooms = Integer.parseInt(etRooms.getText().toString());
                }
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
                setUpOfferRecycler(searchedOffers, view);
                break;

            case R.id.imgbtn_search_options :
                if (searchOptions.getVisibility() == View.VISIBLE) {
                    searchOptions.setVisibility(View.GONE);
                    btnSearchOptions.setImageResource(R.drawable.arrow_show);
                    tvSearchOptionsMessage.setText("Show Search Options");

                } else {
                    searchOptions.setVisibility(View.VISIBLE);
                    btnSearchOptions.setImageResource(R.drawable.arrow_hide);
                    tvSearchOptionsMessage.setText("Hide Search Options");
                }
                break;

            case R.id.imgbtn_ascending_search :
                if (searchedOffers.size() > 1) {
                    Collections.sort(searchedOffers,new Comparator<Offer>() {
                        @Override
                        public int compare(Offer offer1, Offer offer2) {
                            int price1 = offer1.getPrice();
                            if (offer1.getCurrency() == Offer.Currency.EU) {
                                price1 = (int) (price1 * 1.94);
                            }
                            int price2 = offer2.getPrice();
                            if (offer2.getCurrency() == Offer.Currency.EU) {
                                price2 = (int) (price2 * 1.94);
                            }
                            return price1 - price2;
                        }
                    });
                    offerAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.imgbtn_descending_search :
                if (searchedOffers.size() > 1) {
                    Collections.sort(searchedOffers,new Comparator<Offer>() {
                        @Override
                        public int compare(Offer offer1, Offer offer2) {
                            int price1 = offer1.getPrice();
                            if (offer1.getCurrency() == Offer.Currency.EU) {
                                price1 = (int) (price1 * 1.94);
                            }
                            int price2 = offer2.getPrice();
                            if (offer2.getCurrency() == Offer.Currency.EU) {
                                price2 = (int) (price2 * 1.94);
                            }
                            return price2 - price1;
                        }
                    });
                    offerAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
