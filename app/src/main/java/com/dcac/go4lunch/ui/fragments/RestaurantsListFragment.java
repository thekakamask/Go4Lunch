package com.dcac.go4lunch.ui.fragments;

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
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.views.RestaurantsListAdapter;
import com.google.android.gms.maps.model.LatLng;
import java.util.Arrays;
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
                adapter.submitList(resource.data.get(0).getResults());
            } else {
                Log.e("ListFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });
    }

    /*private void transformResultsToPlaceDetails(List<Results> results) {
        List<PlaceDetails> placeDetailsList = new ArrayList<>();

        for (Results result : results) {
            streamGoogleMapViewModel.getPlaceDetails(result.getPlace_id()).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    placeDetailsList.add(resource.data);

                    if (placeDetailsList.size() == results.size()) {
                        adapter.submitList(results);
                    }
                } else {
                    Log.e("ListFragment", "Error fetching place details: " + (resource.message != null ? resource.message : "unknown error"));
                }
            });
        }
    }*/

}