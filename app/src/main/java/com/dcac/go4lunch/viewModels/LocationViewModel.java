package com.dcac.go4lunch.viewModels;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.repository.LocationRepository;

public class LocationViewModel extends ViewModel {


    private final LiveData<Location> locationLiveData;

    public LocationViewModel(LocationRepository locationRepository) {
        this.locationLiveData = locationRepository.getLastKnownLocation();
    }

    public LiveData<Location> getLocationLiveData() {
        return locationLiveData;
    }


}
