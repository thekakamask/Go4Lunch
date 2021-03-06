package com.example.go4lunch.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Objects;

public final class UserRepository {

    @SuppressLint("StaticFieldLeak")
    private static volatile UserRepository instance;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static final String COLLECTION_USERS = "users";

    private UserRepository() {}

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    public void setContext(Context context) {
        UserRepository.context = context;
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public OnFailureListener onFailureListener(){
        return e -> Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_LONG).show();
    }

    /*@Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null)? user.getUid(): null;
    }*/

    //GET THE COLLECTION REF
    public CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    public Task<DocumentSnapshot> getUserData(String uid) {
        //String uid =this.getCurrentUserUID();
        if(uid != null) {
            return getUsersCollection().document(uid).get();
        }else {
            return null;
        }
    }

    public void createUserInFirestore(String uid) {
        //FirebaseUser user = getCurrentUser();

            String urlPicture = (Objects.requireNonNull(getCurrentUser()).getPhotoUrl() != null) ? Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString() : null;
            String userName = getCurrentUser().getDisplayName();
            //String uid = getCurrentUser().getUid();

            getUserData(uid).addOnSuccessListener(documentSnapshot -> {
                User user =documentSnapshot.toObject(User.class);

                if (user != null) {
                    createUser(uid, userName, urlPicture, user.getIdOfPlace(), user.getLike(), user.getCurrentTime()).addOnFailureListener(onFailureListener());
                } else {
                    createUser(uid, userName, urlPicture, "" , new ArrayList<>() , 0).addOnFailureListener(onFailureListener());
                }
            });

    }

    private Task<Void> createUser(String uid, String username, String urlPicture, String idOfPlace, ArrayList<String> like, int currentTime) {
        //Create user object
        User userToCreate = new User(uid, username, urlPicture, idOfPlace, like, currentTime);
        //Add a new user Document in Firestore
        return getUsersCollection()
                .document(uid) //Setting uID for Document
                .set(userToCreate);//Setting object for Document
    }

    /*public static Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }*/

    public void deleteIdOfPlace(String uid) {
        getUsersCollection().document(uid).update("idOfPlace", null);
    }

    public void deleteLike(String uid, String idOfPlace) {
        getUsersCollection().document(uid).update("like", FieldValue.arrayRemove(idOfPlace));
    }

    public void updateIdOfPlace(String uid, String idOfPlace, int currentTime){
        getUsersCollection().document(uid).update("idOfPlace", idOfPlace, "currentTime", currentTime);

    }

    public void updateLike(String uid, String idOfPlace) {
        getUsersCollection().document(uid).update("like", FieldValue.arrayUnion(idOfPlace));
    }



    /*// CREATE USER IN FIRESTORE
    public static Task<Void> createUser(String uid, String username, String urlPicture, String placeId, ArrayList<String> like, int currentTime){
        // CREATE USER OBJECT
        User userToCreate = new User(uid, username, urlPicture, placeId, like, currentTime);
        // ADD NEW DOCUMENT IN FIRESTORE
        return getUsersCollection()
                .document(uid) // SETTING UID FOR DOCUMENT
                .set(userToCreate); // SET OBJECT FOR DOCUMENT
    }

    //GET USER FROM FIRESTORE
    public static Task<DocumentSnapshot> getUser(String uid) {
        return getUsersCollection().document(uid).get();
    }

    public static Task<Void> updateUsername(String username, String uid) {
        return getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updatePlaceId(String uid, String placeId, int currentTime) {
        return getUsersCollection().document(uid).update("placeId", placeId, "currentTime", currentTime);
    }

    public static Task<Void> updateLike(String uid, String placeId) {
        return getUsersCollection().document(uid).update("like", FieldValue.arrayUnion(placeId));
    }

    public static Task<Void> deleteUser(String uid) {
        return getUsersCollection().document(uid).delete();
    }

    public static Task<Void> deletePlaceId(String uid) {
        return getUsersCollection().document(uid).update("placeId", null);
    }

    public static Task<Void> deleteLike(String uid, String placeId) {
        return getUsersCollection().document(uid).update("like", FieldValue.arrayRemove(placeId));
    }*/
}
