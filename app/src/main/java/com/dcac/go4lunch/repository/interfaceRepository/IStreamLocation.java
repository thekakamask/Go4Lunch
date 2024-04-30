package com.dcac.go4lunch.repository.interfaceRepository;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.LocationCallback;

public interface IStreamLocation {

    LiveData<Location> getLastKnownLocation();

    void startLocationUpdates(LocationCallback callback);

    void stopLocationUpdates(LocationCallback callback);


}
