package com.dcac.go4lunch.injection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dcac.go4lunch.repository.LocationRepository;
import com.dcac.go4lunch.viewModels.LocationViewModel;

public class LocationViewModelFactory implements ViewModelProvider.Factory {
    private final LocationRepository locationRepository;

    private static LocationViewModelFactory factory;

    private LocationViewModelFactory(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public static LocationViewModelFactory getInstance(Context applicationContext) {
        if (factory == null) {
            synchronized (LocationViewModelFactory.class) {
                if (factory == null) {
                    LocationRepository repository = LocationRepository.getInstance(applicationContext);
                    factory = new LocationViewModelFactory(repository);
                }
            }
        }
        return factory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!LocationViewModel.class.isAssignableFrom(modelClass)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
        return (T) new LocationViewModel(locationRepository);
    }

}
