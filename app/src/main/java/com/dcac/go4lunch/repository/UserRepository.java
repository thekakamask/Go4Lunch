package com.dcac.go4lunch.repository;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.User;
import com.dcac.go4lunch.utils.Resource;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import javax.annotation.Nullable;

public final class UserRepository {


    private static final String COLLECTION_USERS= "users";
    private final CollectionReference usersCollection;

    private static UserRepository instance;

    private final AuthService authService;

    private final MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();

    private UserRepository(AuthService authService) {
        this.usersCollection = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
        this.authService=authService;

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            currentUser.setValue(firebaseAuth.getCurrentUser());
        });
    }

    public static UserRepository getInstance(AuthService authService) {
        if (instance == null) {
            instance = new UserRepository(authService);
        }
        return instance;
    }





    public LiveData<QuerySnapshot> getAllUsers() {
        MutableLiveData<QuerySnapshot> liveData = new MutableLiveData<>();
        usersCollection.get()
                .addOnSuccessListener(liveData::setValue)
                .addOnFailureListener(e -> liveData.setValue(null));
        return liveData;
    }

    public LiveData<DocumentSnapshot> getUserData(String uid) {
        MutableLiveData<DocumentSnapshot> liveData = new MutableLiveData<>();
        if (uid != null) {
            usersCollection.document(uid).get()
                    .addOnSuccessListener(liveData::setValue)
                    .addOnFailureListener(e -> liveData.setValue(null));
        } else {
            liveData.setValue(null);
        }
        return liveData;
    }

    public LiveData<Boolean> createUser(String uid) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String urlPicture = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            String userName = user.getDisplayName();
            String email = user.getEmail();
            User userToCreate = new User(uid, userName, urlPicture, email);
            usersCollection.document(uid).set(userToCreate)
                    .addOnCompleteListener(task -> liveData.setValue(task.isSuccessful()));
        } else {
            liveData.setValue(false);
        }
        return liveData;
    }

    public LiveData<Boolean> updateUserName(String uid, String userName) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        usersCollection.document(uid).update("userName", userName)
                .addOnCompleteListener(task -> liveData.setValue(task.isSuccessful()));
        return liveData;
    }

    public LiveData<Boolean> updateUrlPicture(String uid, String urlPicture) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        usersCollection.document(uid).update("urlPicture", urlPicture)
                .addOnCompleteListener(task -> liveData.setValue(task.isSuccessful()));
        return liveData;
    }

    public LiveData<Resource<Void>> signOut() {
        return authService.signOut();
    }

    public LiveData<Resource<Void>> deleteUser() {
        return authService.deleteUser();
    }

    public LiveData<FirebaseUser> getCurrentUserLiveData() {
        return currentUser;
    }

    public CollectionReference getUsersCollection() {
        return usersCollection;
    }

}
