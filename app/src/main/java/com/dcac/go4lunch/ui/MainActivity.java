package com.dcac.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.fragments.RestaurantsListFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.ui.fragments.WorkMatesListFragment;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener  {


    private UserViewModel userViewModel;

    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate: start");

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        setSupportActionBar(binding.activityMainToolbar);
        getSupportActionBar().setTitle("");
        Log.d("MainActivity", "onCreate: toolbar set up");

        setUpNavigationDrawer();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new RestaurantsMapFragment())
                    .commit();
        }

        // Configure TabLayout listener
        binding.activityMainTabsLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new RestaurantsMapFragment())
                                .commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new RestaurantsListFragment())
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new WorkMatesListFragment())
                                .commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Optional: Handle tab unselect
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Optional: Handle tab reselect
            }
        });

        Log.d("MainActivity", "onCreate: end");

        binding.toolbarSearchButton.setOnClickListener(v -> launchSearch());

    }

    private void launchSearch() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
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
        //binding.activityMainNavView.getHeaderView(0);

        // Access to the header view
        View headerView = binding.activityMainNavView.getHeaderView(0);

        // Access elements of the header view
        CircleImageView profileImageView = headerView.findViewById(R.id.header_profile_image);
        TextView userNameTextView = headerView.findViewById(R.id.header_user_name);
        TextView userEmailTextView = headerView.findViewById(R.id.header_user_email);

        setupUserProfile();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        binding.activityMainDrawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.activity_main_drawer_lunch) {
            openLunchActivity();
        } else if (id == R.id.activity_main_drawer_parameters) {
            openSettingsActivity();
        } else if (id == R.id.activity_main_drawer_log_out) {
            logOut();
        } else {
            return false;
        }

        int size = binding.activityMainNavView.getMenu().size();
        for (int i = 0; i < size; i++) {
            binding.activityMainNavView.getMenu().getItem(i).setChecked(false);
        }

        return true;
    }


    private void openLunchActivity() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            userViewModel.getUserData(uid).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    User user = resource.data.toObject(User.class);
                    if (user != null && user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantId() != null) {
                        // User choose a restaurant , open RestaurantActivity with ID of restaurant
                        String restaurantId = user.getRestaurantChoice().getRestaurantId();
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, restaurantId);
                        startActivity(intent);
                    } else {
                        // no restaurant choose, display message
                        Toast.makeText(MainActivity.this, R.string.choose_a_restaurant_first, Toast.LENGTH_SHORT).show();
                    }
                } else if (resource.status == Resource.Status.ERROR) {
                    Log.e("MainActivity", "Error fetching user data: " + resource.message);
                    Toast.makeText(MainActivity.this, R.string.error_fetching_user_data, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void openSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, ParametersActivity.class);
        startActivity(intent);
    }

    private void logOut() {
        userViewModel.signOut().observe(this, signOutResource -> {
            if (signOutResource != null) {
                if (signOutResource.status == Resource.Status.SUCCESS) {
                    redirectToWelcomeFragment("LoggedOut");
                } else if (signOutResource.status == Resource.Status.ERROR) {
                    Toast.makeText(this, R.string.disconnection_failed, Toast.LENGTH_SHORT).show();
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

    private void setupUserProfile() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            userViewModel.getUserData(uid).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    User user = resource.data.toObject(User.class);
                    if (user != null) {
                        updateHeaderView(user);
                    }
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(MainActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.d("MainActivity", "setupUserProfile: fetching user data");

        Log.d("MainActivity", "setupUserProfile: header updated");
    }

    private void updateHeaderView(User user) {
        View headerView = binding.activityMainNavView.getHeaderView(0);
        CircleImageView profileImageView = headerView.findViewById(R.id.header_profile_image);
        TextView userNameTextView = headerView.findViewById(R.id.header_user_name);
        TextView userEmailTextView = headerView.findViewById(R.id.header_user_email);

        userNameTextView.setText(user.getUserName());
        userEmailTextView.setText(user.getEmail());
        // Assume you have a default or a placeholder image for users without a profile image
        Glide.with(this)
                .load(user.getUrlPicture())
                .placeholder(R.drawable.account_icon)
                .into(profileImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int size = binding.activityMainNavView.getMenu().size();
        for (int i = 0; i < size; i++) {
            binding.activityMainNavView.getMenu().getItem(i).setChecked(false);
        }
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