package com.dcac.go4lunch.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationManager {

    private static LocationManager instance;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final MutableLiveData<String> locationLiveData = new MutableLiveData<>();

    private LocationManager(Context context) {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context);
    }

    public static synchronized LocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocationManager(context.getApplicationContext());
        }
        return instance;
    }


    public void startLocationUpdates(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'a pas été accordée. Vous pouvez ici demander la permission ou informer l'utilisateur.
            //PERMISSION NOT GRANTED; I CAN ASK PERMISSION HERE OR INFORM USER
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    locationLiveData.postValue(latitude + "," + longitude);
                }
            }
        }, null);
    }

    public LiveData<String> getLocationLiveData() {
        return locationLiveData;
    }


}
