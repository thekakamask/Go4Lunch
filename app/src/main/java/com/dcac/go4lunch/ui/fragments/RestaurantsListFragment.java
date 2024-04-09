package com.dcac.go4lunch.ui.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dcac.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.dcac.go4lunch.views.RestaurantsListAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantsListFragment extends Fragment {

    private FragmentRestaurantsListBinding binding;
    private RestaurantsListAdapter adapter;
    /*private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private LocationViewModel locationViewModel;*/
    private MainActivity mainActivity;

    private boolean hasObservers = false;

    public static RestaurantsListFragment newInstance() {return new RestaurantsListFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        /*if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();
        //subscribeToLocationUpdates();
        subscribeToUpdates();
        /*if (mainActivity != null) {
            locationViewModel = mainActivity.getLocationViewModel();
            streamGoogleMapViewModel = mainActivity.getStreamGoogleMapViewModel();
            getLocationUpdates();
        }*/
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

                    // Recuperation of actual localisation of user
                    Location userLocation = mainActivity.getLocationViewModel().getLocationLiveData().getValue();

                    // Sort result by distance if the loc of user is disponible
                    if (userLocation != null) {
                        Collections.sort(resultsList, new Comparator<Results>() {
                            @Override
                            public int compare(Results result1, Results result2) {
                                Location location1 = new Location("");
                                location1.setLatitude(result1.getGeometry().getLocation().getLat());
                                location1.setLongitude(result1.getGeometry().getLocation().getLng());

                                Location location2 = new Location("");
                                location2.setLatitude(result2.getGeometry().getLocation().getLat());
                                location2.setLongitude(result2.getGeometry().getLocation().getLng());

                                return Float.compare(userLocation.distanceTo(location1), userLocation.distanceTo(location2));
                            }
                        });
                    }

                    // Opening Hours recuperation
                    List<String> placeIds = new ArrayList<>();
                    for (Results result : resultsList) {
                        placeIds.add(result.getPlace_id());
                    }

                    mainActivity.getStreamGoogleMapViewModel().getOpeningHours(placeIds).observe(getViewLifecycleOwner(), openingHoursMap -> {
                        // Update opening hours in the adapter (COMMENT EST FABRIQUER OPENINGHOURS)
                        adapter.setOpeningHours(openingHoursMap);

                        // Observe choice restaurants
                        restaurantChoicesResource.observe(getViewLifecycleOwner(), restaurantChoices -> {
                            if (restaurantChoices != null && restaurantChoices.status == Resource.Status.SUCCESS && restaurantChoices.data != null) {
                                Map<String, Integer> restaurantUserCounts = new HashMap<>();
                                for (Map.Entry<String, List<User>> entry : restaurantChoices.data.entrySet()) {
                                    restaurantUserCounts.put(entry.getKey(), entry.getValue().size());
                                }

                                // Update adapter with sort list , loc of user and number of user by restaurants
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
        subscribeToUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        hasObservers = false;
    }



    /*private void subscribeToLocationUpdates() {
        if (mainActivity != null) {
            mainActivity.getLocationViewModel().getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    fetchNearbyRestaurants(userLocation);
                }
            });
        }
    }*/

    /*private void getLocationUpdates() {
        // Observe location updates
        locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                // Adapter init with location
                if (adapter == null) {
                    adapter = new RestaurantsListAdapter(requireContext(), location);
                    binding.restaurantListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.restaurantListRecyclerView.setAdapter(adapter);
                    fetchNearbyRestaurants(userLocation);
                } else {
                    // update location if adapter is already init
                    adapter.setUserLocation(location);
                }
            }
        });
    }*/

    /*private void fetchNearbyRestaurants(LatLng userLocation) {
        String loc = userLocation.latitude + "," + userLocation.longitude;
        List<String> types = Collections.singletonList("restaurant");
        mainActivity.getStreamGoogleMapViewModel().getCombinedNearbyPlaces(loc, 1000, types).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                List<Results> sortedRestaurants = sortRestaurantsByDistance(resource.data.get(0).getResults(), userLocation);
                Location location = new Location("");
                location.setLatitude(userLocation.latitude);
                location.setLongitude(userLocation.longitude);
                adapter.updateData(sortedRestaurants, location);
            }
        });
    }*/

    /*private void fetchNearbyRestaurants(LatLng userLocation) {

        String location = userLocation.latitude + "," + userLocation.longitude;
        //List<String> types = Arrays.asList("food", "restaurant");
        streamGoogleMapViewModel.getCombinedNearbyPlaces(location, 1000, Arrays.asList("food", "restaurant")).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                // Trier les restaurants par distance
                List<Results> sortedRestaurants = sortRestaurantsByDistance(resource.data.get(0).getResults(), userLocation);
                adapter.submitList(sortedRestaurants);
            } else {
                Log.e("ListFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });

        *//*String location = userLocation.latitude + "," + userLocation.longitude;
        List<String> types = Arrays.asList("restaurant");
        streamGoogleMapViewModel.getCombinedNearbyPlaces(location, 1000, types).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.submitList(resource.data.get(0).getResults());
            } else {
                Log.e("ListFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });*//*
    }*/

    /*private List<Results> sortRestaurantsByDistance(List<Results> restaurants, final LatLng userLatLng) {
        final Location userLocation = new Location("");
        userLocation.setLatitude(userLatLng.latitude);
        userLocation.setLongitude(userLatLng.longitude);

        Collections.sort(restaurants, (r1, r2) -> {
            Location location1 = new Location("");
            location1.setLatitude(r1.getGeometry().getLocation().getLat());
            location1.setLongitude(r1.getGeometry().getLocation().getLng());

            Location location2 = new Location("");
            location2.setLatitude(r2.getGeometry().getLocation().getLat());
            location2.setLongitude(r2.getGeometry().getLocation().getLng());

            return Float.compare(userLocation.distanceTo(location1), userLocation.distanceTo(location2));
        });

        return restaurants;
    }*/

    /*private List<Results> sortRestaurantsByDistance(List<Results> restaurants, final LatLng userLatLng) {
        final Location userLocation = new Location("");
        userLocation.setLatitude(userLatLng.latitude);
        userLocation.setLongitude(userLatLng.longitude);

        Collections.sort(restaurants, (r1, r2) -> {
            Location location1 = new Location("");
            location1.setLatitude(r1.getGeometry().getLocation().getLat());
            location1.setLongitude(r1.getGeometry().getLocation().getLng());

            Location location2 = new Location("");
            location2.setLatitude(r2.getGeometry().getLocation().getLat());
            location2.setLongitude(r2.getGeometry().getLocation().getLng());

            return Float.compare(userLocation.distanceTo(location1), userLocation.distanceTo(location2));
        });

        return restaurants;
    }*/


    /*private List<Results> sortRestaurantsByDistance(List<Results> restaurants, final LatLng userLatLng) {

        final Location userLocation = new Location("");
        userLocation.setLatitude(userLatLng.latitude);
        userLocation.setLongitude(userLatLng.longitude);

        Collections.sort(restaurants, new Comparator<Results>() {
            @Override
            public int compare(Results r1, Results r2) {
                Location location1 = new Location("");
                location1.setLatitude(r1.getGeometry().getLocation().getLat());
                location1.setLongitude(r1.getGeometry().getLocation().getLng());

                Location location2 = new Location("");
                location2.setLatitude(r2.getGeometry().getLocation().getLat());
                location2.setLongitude(r2.getGeometry().getLocation().getLng());

                Float distance1 = userLocation.distanceTo(location1);
                Float distance2 = userLocation.distanceTo(location2);

                return distance1.compareTo(distance2);
            }
        });

        return restaurants;
    }*/

}