package com.dcac.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.views.TabLayoutAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener  {

    private TabLayoutAdapter tabLayoutAdapter;

protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = binding.activityMainToolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        setUpNavigationDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new RestaurantsMapFragment())
                    .commit();
        }

        tabLayoutAdapter = new TabLayoutAdapter(this);

        TabLayout tabLayout = binding.activityMainTabs;
        for (int i = 0; i < 3; i++) {
            tabLayout.addTab(tabLayout.newTab().setText("Tab " + (i + 1)));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayoutAdapter.replaceFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselect
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselect
            }
        });

        // Initialize the default selected tab
        tabLayout.selectTab(tabLayout.getTabAt(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void displayDefaultFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_frame_layout, new RestaurantsMapFragment())
                .commit();
    }*/

    private void setUpNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.activityMainDrawerLayout, binding.activityMainToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        binding.activityMainDrawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        binding.activityMainNavView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //int id = item.getItemId();

        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.activityMainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}