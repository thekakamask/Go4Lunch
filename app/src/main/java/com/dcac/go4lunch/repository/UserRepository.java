package com.dcac.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.utils.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

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
            List<String> restaurantsLike = new ArrayList<>();
            User userToCreate = new User(uid, userName, urlPicture, email, restaurantsLike, null);
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

    public LiveData<Boolean> addRestaurantToLiked(String uid, String restaurantId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Log.d("ToggleLikeRepo", "Adding to liked list: " + restaurantId);
        usersCollection.document(uid)
                .update("restaurantsLike", FieldValue.arrayUnion(restaurantId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("ToggleLikeRepo", "Successfully added to liked list: " + restaurantId);
                    liveData.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("ToggleLikeRepo", "Failed to add to liked list: " + restaurantId, e);
                    liveData.setValue(false);
                });
        return liveData;
    }

    public LiveData<Boolean> removeRestaurantFromLiked(String uid, String restaurantId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Log.d("ToggleLikeRepo", "Removing from liked list: " + restaurantId);
        usersCollection.document(uid)
                .update("restaurantsLike", FieldValue.arrayRemove(restaurantId))
                .addOnSuccessListener(aVoid -> {
                    Log.d("ToggleLikeRepo", "Successfully removed from liked list: " + restaurantId);
                    liveData.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("ToggleLikeRepo", "Failed to remove from liked list: " + restaurantId, e);
                    liveData.setValue(false);
                });
        return liveData;
    }

    public LiveData<RestaurantChoice> getRestaurantChoice(String uid) {
        MutableLiveData<RestaurantChoice> liveData = new MutableLiveData<>();

        usersCollection.document(uid).get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null && user.getRestaurantChoice() != null) {
                liveData.setValue(user.getRestaurantChoice());
            } else {
                liveData.setValue(null);
            }
        }).addOnFailureListener(e -> liveData.setValue(null));

        return liveData;
    }

    public LiveData<Boolean> setRestaurantChoice(String uid, String restaurantId, String choiceDate) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        RestaurantChoice restaurantChoice = new RestaurantChoice(restaurantId, choiceDate);

        usersCollection.document(uid)
                .update("restaurantChoice", restaurantChoice)
                .addOnSuccessListener(aVoid -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<Boolean> removeRestaurantChoice(String uid) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        usersCollection.document(uid)
                .update("restaurantChoice", null)
                .addOnSuccessListener(aVoid -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }





    /*public LiveData<Boolean> toggleRestaurantLike(String uid, String restaurantId, boolean isLiked) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (isLiked) {
            Log.d("ToggleLikeRepo", "Removing from liked list: " + restaurantId);
            usersCollection.document(uid)
                    .update("restaurantsLike", FieldValue.arrayRemove(restaurantId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ToggleLikeRepo", "Successfully removed from liked list: " + restaurantId);
                        liveData.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ToggleLikeRepo", "Failed to remove from liked list: " + restaurantId, e);
                        liveData.setValue(false);
                    });
        } else {
            Log.d("ToggleLikeRepo", "Adding to liked list: " + restaurantId);
            usersCollection.document(uid)
                    .update("restaurantsLike", FieldValue.arrayUnion(restaurantId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ToggleLikeRepo", "Successfully added to liked list: " + restaurantId);
                        liveData.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ToggleLikeRepo", "Failed to add to liked list: " + restaurantId, e);
                        liveData.setValue(false);
                    });
        }
        return liveData;
    }*/

    /*public LiveData<Boolean> toggleRestaurantLike(String uid, String restaurantId, boolean isLiked) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        if (!isLiked) { // if the restaurant is not liked, add to the liked list.
            Log.d("ToggleLikeRepo", "Adding to liked list: " + restaurantId);
            usersCollection.document(uid)
                    .update("restaurantsLike", FieldValue.arrayUnion(restaurantId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ToggleLikeRepo", "Successfully added to liked list: " + restaurantId);
                        liveData.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ToggleLikeRepo", "Failed to add to liked list: " + restaurantId, e);
                        liveData.setValue(false);
                    });
        } else { // if the restaurant is already liked, withdraw it from the liked list
            Log.d("ToggleLikeRepo", "Removing from liked list: " + restaurantId);
            usersCollection.document(uid)
                    .update("restaurantsLike", FieldValue.arrayRemove(restaurantId))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ToggleLikeRepo", "Successfully removed from liked list: " + restaurantId);
                        liveData.setValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ToggleLikeRepo", "Failed to remove from liked list: " + restaurantId, e);
                        liveData.setValue(false);
                    });
        }
        return liveData;
    }*/



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
