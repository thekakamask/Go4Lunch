package com.dcac.go4lunch.ui.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dcac.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.views.RestaurantsListAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestaurantsListFragment extends Fragment {

    private FragmentRestaurantsListBinding binding;
    private RestaurantsListAdapter adapter;
    private MainActivity mainActivity;

    private boolean hasObservers = false;

    private final List<String> chosenRestaurantIds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        assert mainActivity != null;
        mainActivity.getUserViewModel().getChosenRestaurantIds().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        chosenRestaurantIds.clear();
                        if (resource.data != null) {
                            chosenRestaurantIds.addAll(resource.data);
                        }
                        adapter.setChosenRestaurantIds(chosenRestaurantIds); // Pass the chosen IDs to the adapter
                        break;
                    case ERROR:
                        Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
                        break;
                    case LOADING:
                        // Optionally handle loading state
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        subscribeToUpdates();
    }

    private void setupAdapter() {
        adapter = new RestaurantsListAdapter(mainActivity);
        binding.restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.restaurantListRecyclerView.setAdapter(adapter);
    }

    private void subscribeToUpdates() {
        if (!hasObservers) {
            LiveData<Resource<List<PlaceNearbySearch>>> nearbyPlacesResource = mainActivity.getStreamGoogleMapViewModel().getStoredNearbyPlaces();
            LiveData<Resource<Map<String, List<User>>>> restaurantChoicesResource = mainActivity.getUserViewModel().getAllRestaurantChoices();

            nearbyPlacesResource.observe(getViewLifecycleOwner(), nearbyPlaces -> {
                if (nearbyPlaces != null && nearbyPlaces.status == Resource.Status.SUCCESS && nearbyPlaces.data != null) {
                    List<Results> resultsList = new ArrayList<>(nearbyPlaces.data.get(0).getResults());

                    //Retrieving the user's current location
                    Location userLocation = mainActivity.getLocationViewModel().getLocationLiveData().getValue();

                    // Sort results by distance if user location is available
                    if (userLocation != null) {
                        Collections.sort(resultsList, (result1, result2) -> {
                            Location location1 = new Location("");
                            location1.setLatitude(result1.getGeometry().getLocation().getLat());
                            location1.setLongitude(result1.getGeometry().getLocation().getLng());

                            Location location2 = new Location("");
                            location2.setLatitude(result2.getGeometry().getLocation().getLat());
                            location2.setLongitude(result2.getGeometry().getLocation().getLng());

                            return Float.compare(userLocation.distanceTo(location1), userLocation.distanceTo(location2));
                        });
                    }

                    //Recovery of opening hours
                    List<String> placeIds = new ArrayList<>();
                    for (Results result : resultsList) {
                        placeIds.add(result.getPlace_id());
                    }

                    mainActivity.getStreamGoogleMapViewModel().getOpeningHours(placeIds).observe(getViewLifecycleOwner(), openingHoursMap -> {
                        // Updating opening hours in the adapter
                        adapter.setOpeningHours(openingHoursMap);

                        // Observe restaurant choices
                        restaurantChoicesResource.observe(getViewLifecycleOwner(), restaurantChoices -> {
                            if (restaurantChoices != null && restaurantChoices.status == Resource.Status.SUCCESS && restaurantChoices.data != null) {
                                Map<String, Integer> restaurantUserCounts = new HashMap<>();
                                for (Map.Entry<String, List<User>> entry : restaurantChoices.data.entrySet()) {
                                    restaurantUserCounts.put(entry.getKey(), entry.getValue().size());
                                }

                                // Update adapter with sorted list, user location and number of users per restaurant
                                adapter.updateData(resultsList, userLocation, restaurantUserCounts);
                            }
                        });
                    });
                }
            });

            hasObservers = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData(); // Refresh data when fragment come back at first plan
    }

    @Override
    public void onPause() {
        super.onPause();
        hasObservers = false;
    }

    private void refreshData() {
        // Refresh choosen restaurant
        mainActivity.getUserViewModel().refreshChosenRestaurantIds();
        // Re-subscribe to updates to ensure data is refreshed
        subscribeToUpdates();
    }
}