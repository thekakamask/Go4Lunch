package com.dcac.go4lunch.viewModels;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.utils.Resource;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final Map<LiveData<?>, Observer<?>> observers = new HashMap<>();
    private final MutableLiveData<Resource<List<String>>> chosenRestaurantIds = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
        refreshChosenRestaurantIds();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        for (Map.Entry<LiveData<?>, Observer<?>> entry : observers.entrySet()) {
            LiveData<?> liveData = entry.getKey();
            Observer<?> observer = entry.getValue();
            removeObserver(liveData, observer);
        }
        observers.clear();
    }

    private <T> void removeObserver(LiveData<T> liveData, Observer<?> observer) {
        @SuppressWarnings("unchecked")
        Observer<T> typedObserver = (Observer<T>) observer;
        liveData.removeObserver(typedObserver);
    }

    private <T> void observeForever(LiveData<T> liveData, Observer<T> observer) {
        liveData.observeForever(observer);
        observers.put(liveData, observer);
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

    public LiveData<Resource<Boolean>> addRestaurantToLiked(String uid, String restaurantId) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));
        userRepository.addRestaurantToLiked(uid, restaurantId).observeForever(isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Failed to add to liked list", false));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Boolean>> removeRestaurantFromLiked(String uid, String restaurantId) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));
        userRepository.removeRestaurantFromLiked(uid, restaurantId).observeForever(isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                liveData.setValue(Resource.success(true));
            } else {
                liveData.setValue(Resource.error("Failed to remove from liked list", false));
            }
        });
        return liveData;
    }

    public LiveData<Resource<RestaurantChoice>> getRestaurantChoice(String uid) {
        MutableLiveData<Resource<RestaurantChoice>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getRestaurantChoice(uid).observeForever(restaurantChoice -> {
            if (restaurantChoice != null) {
                liveData.setValue(Resource.success(restaurantChoice));
            } else {
                liveData.setValue(Resource.error("No choice found", null));
            }
        });

        return liveData;
    }

    public LiveData<Resource<Boolean>> setRestaurantChoice(String uid, String restaurantId, String choiceDate, String restaurantName, String restaurantAddress) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.setRestaurantChoice(uid, restaurantId, choiceDate, restaurantName, restaurantAddress).observeForever(isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                liveData.setValue(Resource.success(true));
                refreshChosenRestaurantIds();
            } else {
                liveData.setValue(Resource.error("Failed to set restaurant choice", false));
            }
        });

        return liveData;
    }

    public LiveData<Resource<Boolean>> removeRestaurantChoice(String uid) {
        MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.removeRestaurantChoice(uid).observeForever(isSuccess -> {
            if (Boolean.TRUE.equals(isSuccess)) {
                liveData.setValue(Resource.success(true));
                refreshChosenRestaurantIds();
            } else {
                liveData.setValue(Resource.error("Failed to remove restaurant choice", false));
            }
        });

        return liveData;
    }

    public LiveData<Resource<List<User>>> getUsersByRestaurantChoice(String restaurantId) {
        MutableLiveData<Resource<List<User>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        userRepository.getUsersByRestaurantChoice(restaurantId).observeForever(users -> {
            if (users != null) {
                liveData.setValue(Resource.success(users));
            } else {
                liveData.setValue(Resource.error("Error fetching users", null));
            }
        });

        return liveData;
    }

    public LiveData<Resource<Map<String, List<User>>>> getAllRestaurantChoices() {
        return userRepository.getAllRestaurantChoices();
    }

    public LiveData<Resource<List<String>>> getChosenRestaurantIds() {
        return chosenRestaurantIds;
    }

    public void refreshChosenRestaurantIds() {
        userRepository.getChosenRestaurantIds().observeForever(resource -> {
            if (resource != null) {
                chosenRestaurantIds.setValue(Resource.success(resource));
            } else {
                chosenRestaurantIds.setValue(Resource.error("Error fetching chosen restaurant ids", null));
            }
        });
    }
}
