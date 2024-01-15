package com.dcac.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dcac.go4lunch.ui.fragments.RestaurantsListFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.ui.fragments.WorkMatesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {


    public ViewPagerAdapter(FragmentActivity fragment) {
        super(fragment);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return RestaurantsMapFragment.newInstance(position);
            case 1:
                return RestaurantsListFragment.newInstance(position);
            case 2:
                return WorkMatesFragment.newInstance(position);
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }





}
