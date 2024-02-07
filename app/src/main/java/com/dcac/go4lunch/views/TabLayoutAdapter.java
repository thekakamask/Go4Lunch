package com.dcac.go4lunch.views;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.ui.fragments.RestaurantsListFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.ui.fragments.WorkMatesListFragment;

public class TabLayoutAdapter {


    private final FragmentActivity activity;
    private RestaurantsMapFragment restaurantsMapFragment= new RestaurantsMapFragment();

    public TabLayoutAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void replaceFragment(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 0:
                if (restaurantsMapFragment == null) {
                    restaurantsMapFragment = new RestaurantsMapFragment();
                }
                selectedFragment = restaurantsMapFragment;
                break;
            case 1:
                selectedFragment = new RestaurantsListFragment();
                break;
            case 2:
                selectedFragment = new WorkMatesListFragment();
                break;
            default:
                throw new IllegalArgumentException("Position not handled");
        }

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();
    }

}
