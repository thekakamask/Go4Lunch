package com.dcac.go4lunch;

import android.location.Location;

import androidx.lifecycle.LiveData;

import com.dcac.go4lunch.repository.interfaceRepository.IStreamLocation;
import com.google.android.gms.location.LocationCallback;

public class LocationRepositoryTestDouble implements IStreamLocation {


    @Override
    public LiveData<Location> getLastKnownLocation() {
        return null;
    }

    @Override
    public void startLocationUpdates(LocationCallback callback) {

    }

    @Override
    public void stopLocationUpdates(LocationCallback callback) {

    }
}
