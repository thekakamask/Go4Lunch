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


    /*private static final String COLLECTION_USERS = "users";

    private static Context context;
    private static volatile UserRepository instance;

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

    public void setContext (Context context) {
        UserRepository.context = context;
    }

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    public Task<QuerySnapshot> getAllusers() {
        return getUsersCollection().get();
    }

    public OnFailureListener onFailureListener() {
        return e-> Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    *//*@Nullable
    String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user!= null) ? user.getUid(): null;
    }*//*

    public Task<DocumentSnapshot> getUserData(String uid) {
        if (uid != null) {
            return getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public void createUserInFirestore(String uid) {
        //FirebaseUser user = getCurrentUser();

        String urlPicture = (Objects.requireNonNull(getCurrentUser()).getPhotoUrl() != null) ? Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString() : null;
        String userName = getCurrentUser().getDisplayName();
        String email = getCurrentUser().getEmail();
        //String uid = getCurrentUser().getUid();

        getUserData(uid).addOnSuccessListener(documentSnapshot -> {
            createUser(uid, userName, urlPicture, email);
        });

    }

    private Task<Void> createUser(String uid, String userName, String urlPicture, String email) {
        //CREATE USER OBJECT
        User userToCreate = new User(uid, userName, urlPicture, email);
        //ADD A NEW USER DOCUMENT IN FIRESTORE
        return getUsersCollection()
                .document(uid) //SETTING UID FOR DOCUMENT
                .set(userToCreate);// SETTING OBJECT FOR DOCUMENT
    }

    public void updateUserName(String uid, String userName) {
        getUsersCollection().document(uid).update("userName", userName);
    }

    public void updateUrlPicture(String uid, String urlPicture) {
        getUsersCollection().document(uid).update("urlPicture", urlPicture);
    }



    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }*/
}
