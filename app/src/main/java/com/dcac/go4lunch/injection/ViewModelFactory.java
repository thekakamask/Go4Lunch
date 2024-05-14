package com.dcac.go4lunch.injection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dcac.go4lunch.repository.AuthService;
import com.dcac.go4lunch.repository.ChatRepository;
import com.dcac.go4lunch.repository.LocationRepository;
import com.dcac.go4lunch.repository.StreamGoogleMapRepository;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.viewModels.ChatViewModel;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;
    private final LocationRepository locationRepository;
    private final StreamGoogleMapRepository streamGoogleMapRepository;
    private final UserRepository userRepository;

    private final ChatRepository chatRepository;

    private ViewModelFactory(Context applicationContext) {
        locationRepository = LocationRepository.getInstance(applicationContext);
        streamGoogleMapRepository = StreamGoogleMapRepository.getInstance(applicationContext);
        AuthService authService = new AuthService(applicationContext);
        userRepository = UserRepository.getInstance(authService);
        chatRepository=ChatRepository.getInstance();
    }

    public static ViewModelFactory getInstance(Context applicationContext) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(applicationContext);
                }
            }
        }
        return factory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModel viewModel;

        // Creation of the Viewmodel needed Création du ViewModel according to the provided class
        if (LocationViewModel.class.isAssignableFrom(modelClass)) {
            viewModel = new LocationViewModel(locationRepository);
        } else if (StreamGoogleMapViewModel.class.isAssignableFrom(modelClass)) {
            viewModel = new StreamGoogleMapViewModel(streamGoogleMapRepository);
        } else if (UserViewModel.class.isAssignableFrom(modelClass)) {
            viewModel = new UserViewModel(userRepository);
        } else if (ChatViewModel.class.isAssignableFrom(modelClass)) {
            viewModel = new ChatViewModel(chatRepository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }

        // Tentative to cast ViewModel in the asked type
        T castedViewModel = modelClass.cast(viewModel);

        // Result cast verification
        if (castedViewModel == null) {
            throw new IllegalStateException("Could not cast ViewModel to the requested type: " + modelClass.getName());
        }

        return castedViewModel;
    }
}
