package com.dcac.go4lunch.ui.fragments;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.injection.StreamGoogleMapViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;


public class RestaurantsMapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentRestaurantsMapBinding binding;
    private GoogleMap mMap;
    private StreamGoogleMapViewModel viewModel;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 15f;

    /*private FusedLocationProviderClient fusedLocationProviderClient;

    private static final float DEFAULT_ZOOM = 15f;
    private PlacesClient placesClient;

    private ActivityResultLauncher<String> locationPermissionRequest;*/

    public static RestaurantsMapFragment newInstance() {
        return new RestaurantsMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Instanciation of the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        /*if (!Places.isInitialized()) {
            String apiKey = getApiKeyFromManifest();
            if (apiKey != null) {
                Places.initialize(requireContext().getApplicationContext(), apiKey);
            } else {
                Log.e("RestaurantsMapFragment", "API Key not found in Manifest.");
            }
        }
        placesClient = Places.createClient(requireActivity());

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission accordée
                //getDeviceLocation();
            } else {
                // Permission refusée
                // Vous pouvez afficher une explication à l'utilisateur, désactiver les fonctionnalités de localisation ou gérer un cas de refus
            }
        });*/
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
        //binding.mapView.onCreate(savedInstanceState);
        //binding.mapView.getMapAsync(this);

        viewModel = new ViewModelProvider(this, StreamGoogleMapViewModelFactory.getInstance(requireActivity()
                .getApplicationContext())).get(StreamGoogleMapViewModel.class);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap mMap) {
        this.mMap = mMap;
        updateLocationUI();
        getDeviceLocationAndNearbyRestaurants();
    }

    private void updateLocationUI() {
        // Need permission before call setMyLocationEnabled or setMyLocationButtonEnabled
        if (mMap == null) {
            return;
        }
        try {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getDeviceLocationAndNearbyRestaurants() {
        // Check permission and obtain last position of user
        // then call getNearbyRestaurants with the position of user
        try {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                        getNearbyRestaurants(location);
                    } else {
                        Log.d("RestaurantsMapFragment", "Current location is null. Using defaults.");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            } else {
                // Ask for the permission
                // You can use the ActivityResultContracts.RequestPermission contract or any other way you want to request the permission
                // locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getNearbyRestaurants(Location currentLocation) {
        String location = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        viewModel.getNearbyPlaces(location, 1500, "restaurant").observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                addRestaurantMarkers(resource.data);
            } else {
                // handle case where data are not accessible or an error
            }
        });
    }

    private void addRestaurantMarkers(PlaceNearbySearch nearbySearch) {
        if (nearbySearch.getResults() != null) {
            for (Results result : nearbySearch.getResults()) {
                // assure geometry is not null
                if (result.getGeometry() != null && result.getGeometry().getLocation() != null) {
                    LatLng restaurantLatLng = new LatLng(
                            result.getGeometry().getLocation().getLat(),
                            result.getGeometry().getLocation().getLng()
                    );

                    // Add a marker on the map for every restaurant
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(restaurantLatLng)
                            .title(result.getName())
                            .snippet(result.getVicinity()));

                    // Store id of the place in the tag of the marker for another usage
                    marker.setTag(result.getPlace_id());
                }
            }
        }

        // Configure click on the marker if needed
        mMap.setOnMarkerClickListener(marker -> {
            // get ID of place in the marker tag
            String placeId = (String) marker.getTag();
            // handle an action here like display dialog or go in another activity
            return true;
        });
    }


}

    /*private String getApiKeyFromManifest() {
        try {
            ApplicationInfo ai = requireContext().getPackageManager().getApplicationInfo(requireContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("RestaurantsMapFragment", "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e("RestaurantsMapFragment", "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return null;
    }*/

/*
    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        *//*LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));*//*
        mMap = googleMap;

        updateLocationUI();
        getDeviceLocation();
    }

    private void updateLocationUI(){
        if (mMap == null) {
            return;
        }
        try {
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(location -> {
                    if (location != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                        getNearbyRestaurants(location);
                    } else {
                    }
                });
            } else {

                locationPermissionRequest.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getNearbyRestaurants(Location currentLocation) {

        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Gérer le cas où la permission n'est pas accordée.
            return;
        }
        // Construisez une requête avec la localisation actuelle
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Effectuez la requête pour obtenir les lieux actuels
        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Log.i("RestaurantsMapFragment", "Place found: " + placeLikelihood.getPlace().getName() + ", address: " + placeLikelihood.getPlace().getAddress());
                    // Ici, vous pouvez choisir de mettre à jour l'UI avec les informations des restaurants
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("RestaurantsMapFragment", "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }*/