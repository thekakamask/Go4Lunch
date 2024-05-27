package com.dcac.go4lunch.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.ui.RestaurantActivity;
import com.dcac.go4lunch.utils.Resource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


public class RestaurantsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FragmentRestaurantsMapBinding binding;
    private GoogleMap mMap;
    MainActivity mainActivity;
    private static final float DEFAULT_ZOOM = 15f;
    private final List<String> chosenRestaurantIds = new ArrayList<>();

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        setupMap();
                    } else {
                        Toast.makeText(getContext(), "Localisation permission reject, some functionality are deactivate.", Toast.LENGTH_LONG).show();
                    }
                });

        mainActivity.getUserViewModel().getChosenRestaurantIds().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        chosenRestaurantIds.clear();
                        if (resource.data != null) {
                            chosenRestaurantIds.addAll(resource.data);
                        }
                        updateMapMarkers();
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
        binding = FragmentRestaurantsMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        subscribeToUpdates();
        centerMapOnUserLocation();
    }

    private void centerMapOnUserLocation() {
        mainActivity.getLocationViewModel().getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if (location != null && mMap != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));
            }
        });
    }

    private void setupMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void subscribeToUpdates() {
        mainActivity.getStreamGoogleMapViewModel().getStoredNearbyPlaces().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                updateMapWithRestaurants(resource.data);
            }
        });
    }

    private void updateMapWithRestaurants(List<PlaceNearbySearch> placeSearches) {
        mMap.clear();
        BitmapDescriptor defaultMarker = bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_red, R.drawable.lunch_icon);
        BitmapDescriptor chosenMarker = bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_green, R.drawable.lunch_icon);

        for (PlaceNearbySearch placeSearch : placeSearches) {
            for (Results result : placeSearch.getResults()) {
                LatLng placeLocation = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                MarkerOptions markerOptions = new MarkerOptions().position(placeLocation).title(result.getName());

                if (chosenRestaurantIds.contains(result.getPlace_id())) {
                    markerOptions.icon(chosenMarker);
                } else {
                    markerOptions.icon(defaultMarker);
                }
                Marker marker = mMap.addMarker(markerOptions);
                if (marker != null) {
                    marker.setTag(result.getPlace_id());
                }
            }
        }
    }

    private void updateMapMarkers() {
        if (mMap != null) {
            mMap.clear(); // Clear existing markers
            // Re-add the markers with updated icons based on chosenRestaurantIds
            subscribeToUpdates();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId, @DrawableRes int pngResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * 2,
                vectorDrawable.getIntrinsicHeight() * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        Drawable pngDrawable = ContextCompat.getDrawable(context, pngResId);
        int pngSize = Math.min(canvas.getWidth() / 3, canvas.getHeight() / 3);

        int left = (canvas.getWidth() - pngSize) / 2;
        int top = (int) (canvas.getHeight() * 0.25);

        assert pngDrawable != null;
        pngDrawable.setBounds(left, top, left + pngSize, top + pngSize);
        pngDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String placeId = (String) marker.getTag();
        if (placeId != null) {
            Log.d("ONM MapFragment", "Marker clicked with place ID: " + placeId);
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, placeId);
            startActivity(intent);
        } else {
            Log.d("MapFragment", "Marker clicked without place ID");
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
        fetchChosenRestaurants();
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

    private void fetchChosenRestaurants() {
        mainActivity.getUserViewModel().refreshChosenRestaurantIds();
    }
}