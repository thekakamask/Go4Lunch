package com.dcac.go4lunch.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.utils.Resource;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

public class AuthService {

    private final Context applicationContext;
    private FirebaseAuth firebaseAuth;

    public AuthService(Context applicationContext) {
        this.applicationContext= applicationContext.getApplicationContext();
    }

    public AuthService(Context context, FirebaseAuth firebaseAuth) {
        this.applicationContext = context != null ? context.getApplicationContext() : null;
        this.firebaseAuth = firebaseAuth != null ? firebaseAuth : FirebaseAuth.getInstance();
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

    public LiveData<Resource<Void>> deleteUser() {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            Task<Void> deleteAuthTask = currentUser.delete();
            Task<Void> deleteFirestoreTask = FirebaseFirestore.getInstance().collection("users").document(uid).delete();

            StorageReference userStorageRef = FirebaseStorage.getInstance().getReference().child("profileImages/" + uid);
            Task<Void> deleteStorageTask = userStorageRef.delete().continueWithTask(task -> {
                if (!task.isSuccessful() && task.getException() instanceof StorageException && ((StorageException) task.getException()).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {

                    return Tasks.forResult(null);
                }
                return task;
            });

            Task<Void> allTasks = Tasks.whenAll(deleteAuthTask, deleteFirestoreTask, deleteStorageTask);
            allTasks.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    liveData.setValue(Resource.success(null)); 
                } else {
                    liveData.setValue(Resource.error("Failed to delete user", null));
                }
            });
        } else {
            liveData.setValue(Resource.success(null)); 
        }

        return liveData;
    }





}
