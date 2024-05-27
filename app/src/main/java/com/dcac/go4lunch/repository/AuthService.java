package com.dcac.go4lunch.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.utils.Resource;
import com.firebase.ui.auth.AuthUI;

public class AuthService {

    private final Context applicationContext;

    public AuthService(Context applicationContext) {
        this.applicationContext= applicationContext.getApplicationContext();
    }



    public LiveData<Resource<Void>> signOut() {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null)); 

        AuthUI.getInstance().signOut(applicationContext)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        liveData.setValue(Resource.success(null));
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error signing out";
                        liveData.setValue(Resource.error(errorMessage, null));
                    }
                });

        return liveData;
    }







}
