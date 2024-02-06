package com.dcac.go4lunch.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.injection.LocationViewModelFactory;
import com.dcac.go4lunch.injection.StreamGoogleMapViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;


public class RestaurantsMapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentRestaurantsMapBinding binding;
    private GoogleMap mMap;
    private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private LocationViewModel locationViewModel;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public static RestaurantsMapFragment newInstance() {
        return new RestaurantsMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationViewModelFactory locationFactory = LocationViewModelFactory.getInstance(requireContext().getApplicationContext());
        locationViewModel = new ViewModelProvider(this, locationFactory).get(LocationViewModel.class);

        StreamGoogleMapViewModelFactory streamFactory = StreamGoogleMapViewModelFactory.getInstance(requireContext().getApplicationContext());
        streamGoogleMapViewModel = new ViewModelProvider(this, streamFactory).get(StreamGoogleMapViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantsMapBinding.inflate(inflater, container, false);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*locationViewModel = new ViewModelProvider(this, LocationViewModelFactory.getInstance(requireActivity().getApplicationContext())).get(LocationViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this, StreamGoogleMapViewModelFactory.getInstance(requireActivity().getApplicationContext())).get(StreamGoogleMapViewModel.class);
*/
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
    }

    private void setupMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            subscribeToLocationUpdates();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void subscribeToLocationUpdates() {
        locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));
                fetchNearbyRestaurants(userLocation);
            }
        });
    }

    private void fetchNearbyRestaurants(LatLng location) {
        // use the StreamGoogleMapViewModel for recover restaurants near user localisation
        String loc = location.latitude + "," + location.longitude;
        streamGoogleMapViewModel.getCombinedNearbyPlaces(loc, 1000, Arrays.asList("restaurant")).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                updateMapWithRestaurants(resource.data);
            } else {
                Log.e("MapFragment", "Error fetching restaurants: " + (resource.message != null ? resource.message : "unknown error"));
            }
        });
    }

    private void updateMapWithRestaurants(List<PlaceNearbySearch> placeSearches) {
        mMap.clear(); // Clean the map before adding new markers
        for (PlaceNearbySearch placeSearch : placeSearches) {
            for (Results result : placeSearch.getResults()) {
                LatLng placeLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                mMap.addMarker(new MarkerOptions().position(placeLocation).title(result.getName()));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap();
            } else {
                Toast.makeText(getContext(), "Localisation permission reject, some functionality are deactivate.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onPause() {
        binding.mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        binding.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }
}