package com.projects.radomonov.homeless.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.projects.radomonov.homeless.R;
import com.projects.radomonov.homeless.utilities.Utilities;

/**
 * Created by admin on 28.10.2017.
 */

public class SearchFragment extends Fragment {

    private Spinner spinnerNeighbourhoods;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        spinnerNeighbourhoods = view.findViewById(R.id.spinner_neigh);
        spinnerNeighbourhoods.setAdapter(new ArrayAdapter<Utilities.Neighbourhood>(getContext(), android.R.layout.simple_spinner_item, Utilities.Neighbourhood.values()));


        return view;
    }
}
