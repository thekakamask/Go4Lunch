package com.dcac.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
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
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.fragments.ChatFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsListFragment;
import com.dcac.go4lunch.ui.fragments.RestaurantsMapFragment;
import com.dcac.go4lunch.ui.fragments.WorkMatesListFragment;
import com.dcac.go4lunch.utils.MyBroadcastReceiver;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener {


    private UserViewModel userViewModel;
    private LocationViewModel locationViewModel;
    private StreamGoogleMapViewModel streamGoogleMapViewModel;

    private LatLng lastKnownLocation = null;

    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate: start");

        initializeViewModels();
        //setupTabIcons();

        setSupportActionBar(binding.activityMainToolbar);
        getSupportActionBar().setTitle("");
        Log.d("MainActivity", "onCreate: toolbar set up");

        setUpNavigationDrawer();
        updateToolbarTitleBasedOnRestaurantChoice();

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
                    case 3 :
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new ChatFragment())
                                .commit();
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

        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(this, MyBroadcastReceiver.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // Check user preferences for notifications
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = preferences.getBoolean("NotificationsEnabled", true);

        if (notificationsEnabled) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    startActivity(intent);
                }
            }

            Intent intent = new Intent(this, MyBroadcastReceiver.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            // Cancel if alarm is not already programmed
            Intent intent = new Intent(this, MyBroadcastReceiver.class);
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }

        Log.d("MainActivity", "Alarme quotidienne à 12h planifiée.");


        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        Intent intent = new Intent(this, MyBroadcastReceiver.class);


        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, flags);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }*/


    }

    /*private void setupTabIcons() {
        TabLayout tabLayout = findViewById(R.id.activity_main_tabs_layout);
        tabLayout.getTabAt(0).setIcon(R.drawable.map_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.list_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.workmates_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.chat_icon);


        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getIcon() != null) {
                Drawable wrapDrawable = DrawableCompat.wrap(tab.getIcon()).mutate();
                DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(this, R.color.black));
                tab.setIcon(wrapDrawable);
            }
        }
    }*/

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }

    public LocationViewModel getLocationViewModel() {
        return locationViewModel;
    }

    public StreamGoogleMapViewModel getStreamGoogleMapViewModel() {
        return streamGoogleMapViewModel;
    }

    private void initializeViewModels() {
        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        locationViewModel = new ViewModelProvider(this, factory).get(LocationViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this, factory).get(StreamGoogleMapViewModel.class);

        setupLocationListenerAndFetchData();
    }

    private void setupLocationListenerAndFetchData() {
        locationViewModel.getLocationLiveData().observe(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (shouldFetchData(currentLocation)) {
                    lastKnownLocation = currentLocation;
                    fetchRestaurantsData(currentLocation);
                }
            }
        });
    }

    private boolean shouldFetchData(LatLng currentLocation) {
        if (lastKnownLocation == null) {
            return true; // NO DATA PREVIOUSLY CHARGE, NEED TO CHARGE AT THE LAUNCH
        }
        float[] results = new float[1];
        Location.distanceBetween(lastKnownLocation.latitude, lastKnownLocation.longitude,
                currentLocation.latitude, currentLocation.longitude, results);
        return results[0] > 500; // RETURN TRUE IF USER MOVE MORE THANT 500 METERS
    }

    private void fetchRestaurantsData(LatLng userLocation) {
        //Use streamGoogleMapViewModel for recover proximity restaurants
        // Use userViewModel for recover user choice restaurants
        // Note that call are asynchronous, maybe store temporarily results
        // and send them to fragments when data set are ready

        Toast.makeText(this, R.string.loading_restaurants, Toast.LENGTH_SHORT).show();
        String loc = userLocation.latitude + "," + userLocation.longitude;
        List<String> types = Collections.singletonList("restaurant");

        streamGoogleMapViewModel.fetchAndStoreNearbyPlaces(loc, 1000, types);
    }

    // This method is a placeholder. I need to implement the logic for notify the fragments effectively

    private void notifyFragmentsWithRestaurantsData(List<Results> results) {
        // You can use interface of callback or livedata that fragments will observe
    }

    public void navigateToRestaurantDetail(String restaurantId) {
        Intent intent = new Intent(this, RestaurantActivity.class);
        intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, restaurantId);
        startActivity(intent);
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

    private void updateToolbarTitleBasedOnRestaurantChoice() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            userViewModel.getUserData(uid).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    User user = resource.data.toObject(User.class);
                    TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
                    if (user != null && user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantName() != null) {
                        toolbarTextView.setText(R.string.selected_restaurant);
                    } else {
                        toolbarTextView.setText(R.string.you_are_hungry);
                    }
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        int size = binding.activityMainNavView.getMenu().size();
        for (int i = 0; i < size; i++) {
            binding.activityMainNavView.getMenu().getItem(i).setChecked(false);
        }

        updateToolbarTitleBasedOnRestaurantChoice();
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

