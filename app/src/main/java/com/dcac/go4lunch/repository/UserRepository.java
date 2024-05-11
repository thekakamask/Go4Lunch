package com.dcac.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.utils.Resource;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {


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

    private UserRepository (AuthService authService, CollectionReference usersCollection) {
        this.usersCollection = usersCollection;
        this.authService=authService;

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            currentUser.setValue(firebaseAuth.getCurrentUser());
        });
    }

    public static UserRepository getInstance(AuthService authService, CollectionReference usersCollection) {
        if (instance ==null) {
            instance = new UserRepository(authService, usersCollection);
        }
        return instance;
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

    public Task<QuerySnapshot> getAllUsersTask() {
        return usersCollection.get();
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

    public LiveData<Boolean> setRestaurantChoice(String uid, String restaurantId, String choiceDate, String restaurantName, String restaurantAddress) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        RestaurantChoice restaurantChoice = new RestaurantChoice(restaurantId, choiceDate, restaurantName, restaurantAddress);

        usersCollection.document(uid)
                .update("restaurantChoice", restaurantChoice)
                .addOnSuccessListener(aVoid -> liveData.setValue(true))
                .addOnFailureListener(e -> liveData.setValue(false));

        return liveData;
    }

    public LiveData<Boolean> removeRestaurantChoice(String uid) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        DocumentReference userDocRef = usersCollection.document(uid);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    userDocRef.update("restaurantChoice", null)
                            .addOnSuccessListener(aVoid -> liveData.setValue(true))
                            .addOnFailureListener(e -> {
                                Log.e("removeRestaurantChoice", "Error updating document", e);
                                liveData.setValue(false);
                            });
                } else {
                    Log.e("removeRestaurantChoice", "Document does not exist");
                    liveData.setValue(true);
                }
            } else {
                Log.e("removeRestaurantChoice", "Failed to fetch document", task.getException());
                liveData.setValue(false);
            }
        });

        return liveData;
    }

    public LiveData<List<User>> getUsersByRestaurantChoice(String restaurantId) {
        MutableLiveData<List<User>> liveData = new MutableLiveData<>();

        usersCollection.whereEqualTo("restaurantChoice.restaurantId", restaurantId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                    liveData.setValue(userList);
                })
                .addOnFailureListener(e -> {
                    liveData.setValue(new ArrayList<>());
                    Log.e("UserRepository", "Error getting users by restaurant choice", e);
                });

        return liveData;
    }

    public LiveData<List<String>> getChosenRestaurantIds() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        usersCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> restaurantIds = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        User user = snapshot.toObject(User.class);
                        if (user != null && user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantId() != null && !user.getRestaurantChoice().getRestaurantId().isEmpty()) {
                            restaurantIds.add(user.getRestaurantChoice().getRestaurantId());
                        }
                    }
                    liveData.setValue(restaurantIds);
                })
                .addOnFailureListener(e -> liveData.setValue(new ArrayList<>()));
        return liveData;
    }

    public LiveData<Resource<Map<String, List<User>>>> getAllRestaurantChoices() {
        MutableLiveData<Resource<Map<String, List<User>>>> liveData = new MutableLiveData<>();
        usersCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Map<String, List<User>> restaurantChoices = new HashMap<>();
            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                User user = snapshot.toObject(User.class);
                if (user != null && user.getRestaurantChoice() != null) {
                    RestaurantChoice choice = user.getRestaurantChoice();
                    if (!restaurantChoices.containsKey(choice.getRestaurantId())) {
                        restaurantChoices.put(choice.getRestaurantId(), new ArrayList<>());
                    }
                    restaurantChoices.get(choice.getRestaurantId()).add(user);
                }
            }
            liveData.setValue(Resource.success(restaurantChoices));
        }).addOnFailureListener(e -> liveData.setValue(Resource.error("Error fetching restaurant choices", null)));
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

    /*public void setUsersCollection(CollectionReference usersCollection) {
        this.usersCollection = usersCollection;
    }*/

}
