package com.dcac.go4lunch.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.viewModels.UserManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantsListFragment extends Fragment {

    private FragmentRestaurantsListBinding binding;

    private UserManager userManager = UserManager.getInstance();

    private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";

    public static RestaurantsListFragment newInstance(int position, int color) {
        RestaurantsListFragment listFragment = new RestaurantsListFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
        listFragment.setArguments(args);

        return(listFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRestaurantsListBinding.inflate(inflater, container, false);

        // Retrieve and use data from Bundle
        if (getArguments() != null) {
            int position = getArguments().getInt(KEY_POSITION, -1);
            int color = getArguments().getInt(KEY_COLOR, -1);

            binding.restaurantsListLayout.setBackgroundColor(color);
            binding.restaurantsListTitle.setText("List of restaurants. Page number "+ position);
        }
        return binding.getRoot();

    }
}