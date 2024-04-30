package com.dcac.go4lunch.viewModels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.repository.LocationRepository;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamLocation;
import com.google.android.gms.location.LocationCallback;

public class LocationViewModel extends ViewModel {

    private final IStreamLocation locationRepository;
    //private final LocationRepository locationRepository;
    private final LiveData<Location> locationLiveData;

    public LocationViewModel(IStreamLocation repository) {
        this.locationRepository = repository;
        this.locationLiveData = repository.getLastKnownLocation();
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }

    public void startLocationUpdates(LocationCallback callback) {
        locationRepository.startLocationUpdates(callback);
    }

    public void stopLocationUpdates(LocationCallback callback) {
        locationRepository.stopLocationUpdates(callback);
    }
}
