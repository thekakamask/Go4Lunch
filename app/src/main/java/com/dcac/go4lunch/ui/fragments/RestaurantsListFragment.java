package com.dcac.go4lunch.ui.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dcac.go4lunch.databinding.FragmentRestaurantsListBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.views.RestaurantsListAdapter;
import com.google.android.gms.maps.model.LatLng;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantsListFragment extends Fragment {

    private FragmentRestaurantsListBinding binding;
    private RestaurantsListAdapter adapter;
    private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private LocationViewModel locationViewModel;

    public static RestaurantsListFragment newInstance() {return new RestaurantsListFragment();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory factory = ViewModelFactory.getInstance(requireContext().getApplicationContext());
        locationViewModel = new ViewModelProvider(this, factory).get(LocationViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this,factory).get(StreamGoogleMapViewModel.class);
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
        getLocationUpdates();
    }

    private void getLocationUpdates() {
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
    }

    private void fetchNearbyRestaurants(LatLng userLocation) {

        String location = userLocation.latitude + "," + userLocation.longitude;
        List<String> types = Arrays.asList("restaurant");
        streamGoogleMapViewModel.getCombinedNearbyPlaces(location, 1000, types).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                // Trier les restaurants par distance
                List<Results> sortedRestaurants = sortRestaurantsByDistance(resource.data.get(0).getResults(), userLocation);
                adapter.submitList(sortedRestaurants);
            } else {
                Log.e("ListFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });

        /*String location = userLocation.latitude + "," + userLocation.longitude;
        List<String> types = Arrays.asList("restaurant");
        streamGoogleMapViewModel.getCombinedNearbyPlaces(location, 1000, types).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.submitList(resource.data.get(0).getResults());
            } else {
                Log.e("ListFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });*/
    }

    private List<Results> sortRestaurantsByDistance(List<Results> restaurants, final LatLng userLatLng) {

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
    }



}