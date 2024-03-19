package com.dcac.go4lunch.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.ui.RestaurantActivity;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RestaurantsMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FragmentRestaurantsMapBinding binding;
    private GoogleMap mMap;

    /*private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private UserViewModel userViewModel;
    private LocationViewModel locationViewModel;*/

    MainActivity mainActivity;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private List<PlaceNearbySearch> lastFetchedNearbyRestaurants = new ArrayList<>();


    private List<String> chosenRestaurantIds = new ArrayList<>();


    public static RestaurantsMapFragment newInstance() {
        return new RestaurantsMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        /*ViewModelFactory factory = ViewModelFactory.getInstance(requireContext().getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        locationViewModel = new ViewModelProvider(this, factory).get(LocationViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this,factory).get(StreamGoogleMapViewModel.class);*/
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
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            setupMap();

            mainActivity.getLocationViewModel().getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));
                    fetchNearbyRestaurants(userLocation, mainActivity.getStreamGoogleMapViewModel());
                    fetchChosenRestaurants(mainActivity.getUserViewModel());
                }
            });
        }
        mMap.setOnMarkerClickListener(this);
    }

    private void setupMap() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    /*private void subscribeToLocationUpdates(LocationViewModel locationViewModel) {
        locationViewModel.getLocationLiveData().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));
                fetchNearbyRestaurants(userLocation,mainActivity.getStreamGoogleMapViewModel());
                fetchChosenRestaurants(mainActivity.getUserViewModel());
            }
        });
    }*/

    private void fetchNearbyRestaurants(LatLng location, StreamGoogleMapViewModel streamGoogleMapViewModel) {
        String loc = location.latitude + "," + location.longitude;
        streamGoogleMapViewModel.getCombinedNearbyPlaces(loc, 1000, Arrays.asList("food", "restaurant")).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                lastFetchedNearbyRestaurants.clear();
                lastFetchedNearbyRestaurants.addAll(resource.data);
                updateMapWithRestaurants(resource.data);
            }
        });
    }

    private void fetchChosenRestaurants(UserViewModel userViewModel) {
        userViewModel.getChosenRestaurantIds().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                chosenRestaurantIds.clear();
                chosenRestaurantIds.addAll(resource.data);
                updateMapWithRestaurants(lastFetchedNearbyRestaurants);
            }
        });
    }

    private void updateMapWithRestaurants(List<PlaceNearbySearch> placeSearches) {
        mMap.clear();
        BitmapDescriptor defaultMarker = bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_red, R.drawable.lunch_icon); // Votre icône par défaut
        BitmapDescriptor chosenMarker = bitmapDescriptorFromVector(getContext(), R.drawable.ic_marker_green, R.drawable.lunch_icon); // Votre icône pour les restaurants choisis


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
                marker.setTag(result.getPlace_id());
            }

        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId, @DrawableRes int pngResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
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

        pngDrawable.setBounds(left, top, left + pngSize, top + pngSize);
        pngDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        fetchChosenRestaurants(mainActivity.getUserViewModel());
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