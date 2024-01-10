package com.dcac.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dcac.go4lunch.ui.fragments.RestaurantsListFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.ui.fragments.WorkMatesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int [] colors;

    public ViewPagerAdapter(FragmentActivity activity, int []colors) {
        super(activity);
        this.colors= colors;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return RestaurantsMapFragment.newInstance(position, colors[position]);
            case 1:
                return RestaurantsListFragment.newInstance(position, colors[position]);
            case 2:
                return WorkMatesFragment.newInstance(position, colors[position]);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }



}
