package com.dcac.go4lunch.injection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dcac.go4lunch.repository.AuthService;
import com.dcac.go4lunch.repository.StreamGoogleMapRepository;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;

public class StreamGoogleMapViewModelFactory implements ViewModelProvider.Factory {

    private final StreamGoogleMapRepository streamGoogleMapRepository;

    private static StreamGoogleMapViewModelFactory factory;

    private StreamGoogleMapViewModelFactory(Context applicationContext) {
        this.streamGoogleMapRepository = StreamGoogleMapRepository.getInstance(applicationContext);
    }

    public static StreamGoogleMapViewModelFactory getInstance(Context applicationContext) {
        if (factory== null) {
            synchronized (StreamGoogleMapViewModelFactory.class) {
                if (factory==null) {
                    factory = new StreamGoogleMapViewModelFactory(applicationContext);
                }
            }
        }

        return factory;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!StreamGoogleMapViewModel.class.isAssignableFrom(modelClass)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }

        StreamGoogleMapViewModel viewModel = new StreamGoogleMapViewModel(streamGoogleMapRepository);
        T castedViewModel = modelClass.cast(viewModel);

        if (castedViewModel == null) {
            throw new IllegalStateException("Could not cast ViewModel to the requested type: " + modelClass.getName());
        }

        return castedViewModel;
    }
}
