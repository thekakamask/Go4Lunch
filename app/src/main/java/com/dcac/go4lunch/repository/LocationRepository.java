package com.dcac.go4lunch.repository;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.annotation.SuppressLint;

public final class LocationRepository {

    private static LocationRepository instance;
    private final FusedLocationProviderClient fusedLocationClient;
    private final MutableLiveData<Location> currentLocation = new MutableLiveData<>();

    // Singleton pattern
    public static synchronized LocationRepository getInstance(Context context) {
        if (instance == null) {
            instance = new LocationRepository(context.getApplicationContext());
        }
        return instance;
    }

    private LocationRepository(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public LiveData<Location> getLastKnownLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation.postValue(location);
            }
        });
        return currentLocation;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(LocationCallback callback) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        fusedLocationClient.requestLocationUpdates(locationRequest, callback, null);
    }

    public void stopLocationUpdates(LocationCallback callback) {
        fusedLocationClient.removeLocationUpdates(callback);
    }

}
