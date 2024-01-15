package com.dcac.go4lunch.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.viewModels.UserViewModel;

public class RestaurantsMapFragment extends Fragment {

    private FragmentRestaurantsMapBinding binding;


    private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";


    public static RestaurantsMapFragment newInstance(int position) {
        RestaurantsMapFragment mapFragment = new RestaurantsMapFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        mapFragment.setArguments(args);


        return(mapFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRestaurantsMapBinding.inflate(inflater, container, false);
        // Retrieve and use data from Bundle
        if (getArguments() != null) {
            int position = getArguments().getInt(KEY_POSITION, -1);
            int color = getArguments().getInt(KEY_COLOR, -1);

            binding.restaurantsMapLayout.setBackgroundColor(color);
            binding.restaurantsMapTitle.setText("Map of restaurants. Page number : " + position);
        }



        return binding.getRoot();

    }
}