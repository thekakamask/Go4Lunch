package com.dcac.go4lunch.repository.interfaceRepository;

import androidx.lifecycle.LiveData;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.utils.Resource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public interface IStreamUser {

    LiveData<QuerySnapshot> getAllUsers();

    Task<QuerySnapshot> getAllUsersTask();

    LiveData<DocumentSnapshot> getUserData(String uid);

    LiveData<Boolean> createUser(String uid);

    LiveData<Boolean> updateUserName(String uid, String userName);

    LiveData<Boolean> updateUrlPicture(String uid, String urlPicture);

    LiveData<Boolean> addRestaurantToLiked(String uid, String restaurantId);

    LiveData<Boolean> removeRestaurantFromLiked(String uid, String restaurantId);

    LiveData<RestaurantChoice> getRestaurantChoice(String uid);

    LiveData<Boolean> setRestaurantChoice(String uid, String restaurantId, String choiceDate, String restaurantName, String restaurantAddress);

    LiveData<Boolean> removeRestaurantChoice(String uid);

    LiveData<List<User>> getUsersByRestaurantChoice(String restaurantId);

    LiveData<List<String>> getChosenRestaurantIds();

    LiveData<Resource<Map<String, List<User>>>> getAllRestaurantChoices();
    LiveData<Resource<Void>> signOut();

    LiveData<Resource<Void>> deleteUser();

    LiveData<FirebaseUser> getCurrentUserLiveData();

    CollectionReference getUsersCollection();




}
