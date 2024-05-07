package com.dcac.go4lunch.testDouble;

import androidx.lifecycle.LiveData;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamUser;
import com.dcac.go4lunch.utils.Resource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class UserRepositoryTestDouble implements IStreamUser {


    @Override
    public LiveData<QuerySnapshot> getAllUsers() {
        return null;
    }

    @Override
    public Task<QuerySnapshot> getAllUsersTask() {
        return null;
    }

    @Override
    public LiveData<DocumentSnapshot> getUserData(String uid) {
        return null;
    }

    @Override
    public LiveData<Boolean> createUser(String uid) {
        return null;
    }

    @Override
    public LiveData<Boolean> updateUserName(String uid, String userName) {
        return null;
    }

    @Override
    public LiveData<Boolean> updateUrlPicture(String uid, String urlPicture) {
        return null;
    }

    @Override
    public LiveData<Boolean> addRestaurantToLiked(String uid, String restaurantId) {
        return null;
    }

    @Override
    public LiveData<Boolean> removeRestaurantFromLiked(String uid, String restaurantId) {
        return null;
    }

    @Override
    public LiveData<RestaurantChoice> getRestaurantChoice(String uid) {
        return null;
    }

    @Override
    public LiveData<Boolean> setRestaurantChoice(String uid, String restaurantId, String choiceDate, String restaurantName, String restaurantAddress) {
        return null;
    }

    @Override
    public LiveData<Boolean> removeRestaurantChoice(String uid) {
        return null;
    }

    @Override
    public LiveData<List<User>> getUsersByRestaurantChoice(String restaurantId) {
        return null;
    }

    @Override
    public LiveData<List<String>> getChosenRestaurantIds() {
        return null;
    }

    @Override
    public LiveData<Resource<Map<String, List<User>>>> getAllRestaurantChoices() {
        return null;
    }

    @Override
    public LiveData<Resource<Void>> signOut() {
        return null;
    }

    @Override
    public LiveData<Resource<Void>> deleteUser() {
        return null;
    }

    @Override
    public LiveData<FirebaseUser> getCurrentUserLiveData() {
        return null;
    }

    @Override
    public CollectionReference getUsersCollection() {
        return null;
    }
}
