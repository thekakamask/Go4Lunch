package com.dcac.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.dcac.go4lunch.views.TabLayoutAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener  {

    private TabLayoutAdapter tabLayoutAdapter;

    private UserViewModel userViewModel;

    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

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

        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.activity_main_drawer_lunch) {
            openLunchActivity();
            return true;
        } else if (id == R.id.activity_main_drawer_parameters) {
            openSettingsActivity();
            return true;
        } else if (id == R.id.activity_main_drawer_log_out) {
            logOut();
            return true;
        }

        return false;
    }


    private void openLunchActivity() {

    }

    private void openSettingsActivity() {

    }

    private void logOut() {
        userViewModel.signOut().observe(this, signOutResource -> {
            if (signOutResource != null) {
                if (signOutResource.status == Resource.Status.SUCCESS) {
                    redirectToWelcomeFragment("LoggedOut");
                } else if (signOutResource.status == Resource.Status.ERROR) {
                    Toast.makeText(this, R.string.deconnection_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectToWelcomeFragment(String action){
        SharedPreferences prefs = getSharedPreferences("AppSettingsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("LastAction", action);
        editor.apply();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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