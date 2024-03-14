package com.dcac.go4lunch.utils;

import android.os.Handler;
import android.os.Looper;

import com.dcac.go4lunch.repository.UserRepository;

import java.util.concurrent.TimeUnit;

public class RestaurantChoiceUpdater {
    private final UserRepository userRepository;

    public RestaurantChoiceUpdater(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateRestaurantChoice(String userId) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            userRepository.removeRestaurantChoice(userId);
        }, TimeUnit.HOURS.toMillis(2));
    }
}
