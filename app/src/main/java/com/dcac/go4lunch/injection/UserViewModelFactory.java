package com.dcac.go4lunch.injection;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dcac.go4lunch.repository.AuthService;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.viewModels.UserViewModel;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;

    private static UserViewModelFactory factory;

    private UserViewModelFactory(Context applicationContext) {
        AuthService authService = new AuthService(applicationContext);
        this.userRepository = UserRepository.getInstance(authService);
    }

    public static UserViewModelFactory getInstance(Context applicationContext) {
        if (factory== null) {
            synchronized (UserViewModelFactory.class) {
                if (factory==null) {
                    factory = new UserViewModelFactory(applicationContext);
                }
            }
        }

        return factory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (!UserViewModel.class.isAssignableFrom(modelClass)) {
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }

        UserViewModel viewModel = new UserViewModel(userRepository);
        T castedViewModel = modelClass.cast(viewModel);

        if (castedViewModel == null) {
            throw new IllegalStateException("Could not cast ViewModel to the requested type: " + modelClass.getName());
        }

        return castedViewModel;
    }

}
