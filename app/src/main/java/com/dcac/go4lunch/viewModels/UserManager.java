package com.dcac.go4lunch.viewModels;

import android.content.Context;

import com.dcac.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    //REMPALCER USER MANAGER PAR UN VIEWMODEL A PROPREMENT PARLER


    public static UserManager getInstance() {
        UserManager result=instance;
        if(result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if(instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    //REMPALCER USER MANAGER PAR UN VIEWMODEL A PROPREMENT PARLER


    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return userRepository.deleteUser(context);
    }

    public void createUser(String uid) {
        userRepository.createUserInFirestore(uid);
    }

    public Task<DocumentSnapshot> getUserData(String uid) {
        userRepository.getUserData(uid);
        return userRepository.getUsersCollection().document(uid).get();
    }

    public CollectionReference getUsersCollection() {
        return userRepository.getUsersCollection();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return userRepository.getAllusers();
    }

    public void updateUserName(String uid, String userName) {
        userRepository.updateUserName(uid, userName);
    }

    public void updateUrlPicture(String uid, String urlPicture) {
        userRepository.updateUrlPicture(uid, urlPicture);
    }






}
