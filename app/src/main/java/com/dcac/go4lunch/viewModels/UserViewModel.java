package com.dcac.go4lunch.viewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.utils.Resource;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final Map<LiveData<?>, Observer<Object>> observers = new HashMap<>();

    public UserViewModel(UserRepository userRepository) {this.userRepository = userRepository;}

    @Override
    protected void onCleared() {
        super.onCleared();

        for (Map.Entry<LiveData<?>, Observer<Object>> entry : observers.entrySet()) {
            LiveData<?> liveData = entry.getKey();
            Observer<Object> observer = entry.getValue();
            liveData.removeObserver(observer);
        }
        observers.clear();
    }

    private <T> void observeForever(LiveData<T> liveData, Observer<T> observer) {
        Observer<Object> castedObserver = (Observer<Object>) observer;
        liveData.observeForever(castedObserver);
        observers.put(liveData, castedObserver);
    }


    public LiveData<Resource<Boolean>> isCurrentUserLogged() {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getCurrentUserLiveData().observeForever(user -> {
            if (user != null) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("User not logged in", false));
            }
        });

        return liveData;
    }

    public LiveData<Resource<FirebaseUser>> getCurrentUser() {
        MutableLiveData<Resource<FirebaseUser>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getCurrentUserLiveData().observeForever(user -> {
            if (user != null) {
                liveData.setValue(Resource.success(user));
            } else {
                liveData.setValue(Resource.error("No current user", null));
            }
        });

        return liveData;
    }



    public LiveData<Resource<Void>> signOut() {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        LiveData<Resource<Void>> signOutResult = userRepository.signOut();
        signOutResult.observeForever(result -> {
            if (result != null && result.status == Resource.Status.SUCCESS) {
                liveData.setValue(Resource.success(null));
            } else if (result != null && result.status == Resource.Status.ERROR) {
                liveData.setValue(Resource.error(result.message, null));
            }
        });

        return liveData;
    }


    public LiveData<Resource<Void>> deleteUser() {
        LiveData<Resource<Void>> deleteUserResult = userRepository.deleteUser();

        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        deleteUserResult.observeForever(result -> {
            if (result != null && result.status == Resource.Status.SUCCESS) {
                liveData.setValue(Resource.success(null));
            } else if (result != null && result.status == Resource.Status.ERROR) {
                liveData.setValue(Resource.error(result.message, null));
            }
        });

        return liveData;
    }


    public LiveData<Resource<DocumentSnapshot>> getUserData(String uid) {
        MutableLiveData<Resource<DocumentSnapshot>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<DocumentSnapshot> observer = snapshot -> {
            if (snapshot != null) {
                liveData.setValue(Resource.success(snapshot));
            } else {
                liveData.setValue(Resource.error("Error fetching user data", null));
            }
        };
        observeForever(userRepository.getUserData(uid), observer);
        return liveData;
    }


    public LiveData<Resource<QuerySnapshot>> getAllUsers() {
        MutableLiveData<Resource<QuerySnapshot>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<QuerySnapshot> observer = querySnapshot -> {
            if (querySnapshot != null) {
                liveData.setValue(Resource.success(querySnapshot));
            } else {
                liveData.setValue(Resource.error("Error fetching all users", null));
            }
        };
        observeForever(userRepository.getAllUsers(), observer);
        return liveData;
    }

    public LiveData<Resource<Boolean>> createUser(String uid) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error creating user", false));
            }
        };
        observeForever(userRepository.createUser(uid), observer);
        return liveData;
    }

    public CollectionReference getUsersCollection() {
        return userRepository.getUsersCollection();
    }


    public LiveData<Resource<Boolean>> updateUserName(String uid, String userName) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error updating user name", false));
            }
        };
        observeForever(userRepository.updateUserName(uid, userName), observer);
        return liveData;
    }

    public LiveData<Resource<Boolean>> updateUrlPicture(String uid, String urlPicture) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        Observer<Boolean> observer = isSuccess -> {
            if (isSuccess != null && isSuccess) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Error updating URL picture", false));
            }
        };
        observeForever(userRepository.updateUrlPicture(uid, urlPicture), observer);
        return liveData;
    }

    /*private static volatile UserViewModel instance;
    private final UserRepository userRepository;

    private UserViewModel() {
        userRepository = UserRepository.getInstance();
    }

    //REMPALCER USER MANAGER PAR UN VIEWMODEL A PROPREMENT PARLER


    public static UserViewModel getInstance() {
        UserViewModel result=instance;
        if(result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if(instance == null) {
                instance = new UserViewModel();
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
    }*/






}
