package com.dcac.go4lunch.viewModel;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.utils.LiveDataTestUtils;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RunWith(MockitoJUnitRunner.class)
public class UserViewModelUnitTest {

    @Rule
    public final InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "John Doe";
    private static final String USER_EMAIL = "johndoe@example.com";
    private static final String USER_URL_PICTURE = "http://example.com/johndoe.jpg";
    private static final List<String> USER_RESTAURANTS_LIKE = Arrays.asList("Restaurant1", "Restaurant2");
    private static final String RESTAURANT_ID = "restaurant1";

    private static final String CHOICE_DATE = "2024-05-10";
    private static final String RESTAURANT_NAME = "At Gourmet";
    private static final String RESTAURANT_ADDRESS = "123 Gourmet Street";

    @Mock
    public UserRepository mockUserRepository;

    @Mock
    QuerySnapshot mockQuerySnapshot;
    @Mock
    DocumentSnapshot mockDocumentSnapshot;
    @Mock
    DocumentSnapshot mockDocumentSnapshot2;
    @Mock
    FirebaseUser mockFirebaseUser;

    UserViewModel SUT;

    MutableLiveData<Boolean> liveDataCreateUser = new MutableLiveData<>();
    MutableLiveData<QuerySnapshot> liveDataAllUsers = new MutableLiveData<>();
    MutableLiveData<FirebaseUser> liveDataCurrentUser = new MutableLiveData<>();
    MutableLiveData<Resource<Map<String, List<User>>>> liveDataRestaurantChoices = new MutableLiveData<>();
    MutableLiveData<Resource<Void>> signOutLiveData = new MutableLiveData<>();
    MutableLiveData<DocumentSnapshot> liveDataUser = new MutableLiveData<>();
    MutableLiveData<Boolean> liveDataAddToLiked = new MutableLiveData<>();
    MutableLiveData<Boolean> liveDataRemoveToLiked = new MutableLiveData<>();
    MutableLiveData<RestaurantChoice> liveDataGetRestaurantChoice = new MutableLiveData<>();
    MutableLiveData<Boolean> liveDataSetRestaurantChoice = new MutableLiveData<>();
    MutableLiveData<Boolean> liveDataRemoveRestaurantChoice = new MutableLiveData<>();
    MutableLiveData<List<User>> liveDataUsersByRestaurantChoice = new MutableLiveData<>();

    List<User> listUsersByRestaurantChoice = new ArrayList<>();

    MutableLiveData<List<String>> liveDataChosenRestaurantIds = new MutableLiveData<>();
    List<String> listStringChosenRestaurantIds = new ArrayList<>();

    @Before
    public void setup() {
        doReturn(liveDataCreateUser)
                .when(mockUserRepository)
                .createUser(USER_ID);
        doReturn(liveDataAllUsers)
                .when(mockUserRepository)
                .getAllUsers();
        doReturn(liveDataCurrentUser)
                .when(mockUserRepository)
                .getCurrentUserLiveData();
        doReturn(liveDataRestaurantChoices)
                .when(mockUserRepository)
                .getAllRestaurantChoices();
        doReturn(signOutLiveData)
                .when(mockUserRepository)
                .signOut();
        doReturn(liveDataUser)
                .when(mockUserRepository)
                .getUserData(USER_ID);
        doReturn(liveDataAddToLiked)
                .when(mockUserRepository)
                .addRestaurantToLiked(USER_ID, RESTAURANT_ID);
        doReturn(liveDataRemoveToLiked)
                .when(mockUserRepository)
                .removeRestaurantFromLiked(USER_ID, RESTAURANT_ID);
        doReturn(liveDataGetRestaurantChoice)
                .when(mockUserRepository)
                .getRestaurantChoice(USER_ID);
        doReturn(liveDataSetRestaurantChoice)
                .when(mockUserRepository)
                .setRestaurantChoice(USER_ID, RESTAURANT_ID, CHOICE_DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS);
        doReturn(liveDataRemoveRestaurantChoice)
                .when(mockUserRepository)
                .removeRestaurantChoice(USER_ID);
        doReturn(liveDataUsersByRestaurantChoice)
                .when(mockUserRepository)
                .getUsersByRestaurantChoice(RESTAURANT_ID);
        doReturn(liveDataChosenRestaurantIds)
                .when(mockUserRepository)
                .getChosenRestaurantIds();

        when(mockDocumentSnapshot.getString("userName")).thenReturn(USER_NAME);
        when(mockDocumentSnapshot.getString("email")).thenReturn(USER_EMAIL);
        when(mockDocumentSnapshot.getString("urlPicture")).thenReturn(USER_URL_PICTURE);
        when(mockDocumentSnapshot.getString("restaurantName")).thenReturn(RESTAURANT_NAME);
        when(mockDocumentSnapshot.getString("restaurantAddress")).thenReturn(RESTAURANT_ADDRESS);
        when(mockDocumentSnapshot.get("restaurantsLike")).thenReturn(USER_RESTAURANTS_LIKE);

        SUT = new UserViewModel(mockUserRepository);
    }

    @Test
    public void testGetCurrentUserSuccess() throws InterruptedException {
        liveDataCurrentUser.setValue(mockFirebaseUser);
        when(mockFirebaseUser.getEmail()).thenReturn("email");
        when(mockFirebaseUser.getUid()).thenReturn("uid");
        Resource<FirebaseUser> valueAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getCurrentUser());
        Assert.assertEquals("email", valueAwaited.data.getEmail());
        Assert.assertEquals("uid", valueAwaited.data.getUid());
    }

    @Test
    public void testGetCurrentUserFailure() throws InterruptedException {
        liveDataCurrentUser.setValue(null);
        when(mockUserRepository.getCurrentUserLiveData()).thenReturn(liveDataCurrentUser);

        Resource<FirebaseUser> valueAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getCurrentUser());

        Assert.assertNotNull(valueAwaited);
        Assert.assertEquals(Resource.Status.ERROR, valueAwaited.status);
        Assert.assertNull(valueAwaited.data);
    }

    @Test
    public void testSignOutSuccess() throws InterruptedException {
        signOutLiveData.setValue(Resource.success(null));
        Resource<Void> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.signOut());
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
    }

    @Test
    public void testSignOutFailure() throws InterruptedException {
        signOutLiveData.setValue(Resource.error("Error signing out", null));
        Resource<Void> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.signOut());
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertEquals("Error signing out", resultAwaited.message);
    }

    @Test
    public void testGetUserDataSuccess() throws InterruptedException {
        liveDataUser.setValue(mockDocumentSnapshot);

        Resource<DocumentSnapshot> result = LiveDataTestUtils.getOrAwaitValue(SUT.getUserData(USER_ID));
        Assert.assertNotNull(result);
        Assert.assertEquals(Resource.Status.SUCCESS, result.status);
        Assert.assertEquals(USER_NAME, result.data.getString("userName"));
        Assert.assertEquals(USER_EMAIL, result.data.getString("email"));
        Assert.assertEquals(USER_URL_PICTURE, result.data.getString("urlPicture"));
        Assert.assertEquals(RESTAURANT_NAME, result.data.getString("restaurantName"));
        Assert.assertEquals(RESTAURANT_ADDRESS, result.data.getString("restaurantAddress"));

        @SuppressWarnings("unchecked")
        List<String> restaurantsLike = (List<String>) result.data.get("restaurantsLike");

        Assert.assertNotNull(restaurantsLike);
        Assert.assertTrue(restaurantsLike.containsAll(USER_RESTAURANTS_LIKE));
    }

    @Test
    public void testGetUserDataFailure() throws InterruptedException {
        liveDataUser.setValue(null);
        Resource<DocumentSnapshot> result = LiveDataTestUtils.getOrAwaitValue(SUT.getUserData(USER_ID));
        Assert.assertNotNull(result);
        Assert.assertEquals(Resource.Status.ERROR, result.status);
        Assert.assertNull(result.data);
    }

    @Test
    public void testGetAllUsersSuccess() throws InterruptedException {
        liveDataAllUsers.setValue(mockQuerySnapshot);
        when(mockDocumentSnapshot.getString("key")).thenReturn("value");
        when(mockDocumentSnapshot2.getString("key")).thenReturn("value");
        when(mockQuerySnapshot.getDocuments()).thenReturn(Arrays.asList(mockDocumentSnapshot, mockDocumentSnapshot2));
        Resource<QuerySnapshot> listAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getAllUsers());
        List<DocumentSnapshot> docs = listAwaited.data.getDocuments();
        Assert.assertEquals(2, docs.size());
        Assert.assertEquals("value", docs.get(0).getString("key"));
        Assert.assertEquals("value", docs.get(1).getString("key"));
    }

    @Test
    public void testGetAllUsersFailure() throws InterruptedException {
        liveDataAllUsers.setValue(null);
        Resource<QuerySnapshot> result = LiveDataTestUtils.getOrAwaitValue(SUT.getAllUsers());
        Assert.assertNotNull(result);
        Assert.assertEquals(Resource.Status.ERROR, result.status);
        Assert.assertNull(result.data);
        Assert.assertEquals("Error fetching all users", result.message);
    }

    @Test
    public void testCreateUserSuccess() throws InterruptedException {
        liveDataCreateUser.setValue(true);
        Resource<Boolean> valueAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.createUser(USER_ID));
        Assert.assertTrue(valueAwaited.data);
        Assert.assertEquals(Resource.Status.SUCCESS, valueAwaited.status);
        verify(mockUserRepository).createUser(USER_ID);
    }

    @Test
    public void testCreateUserFailure() throws InterruptedException {
        liveDataCreateUser.setValue(false);
        Resource<Boolean> valueAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.createUser(USER_ID));
        Assert.assertFalse(valueAwaited.data);
        Assert.assertEquals(Resource.Status.ERROR, valueAwaited.status);
        Assert.assertEquals("Error creating user", valueAwaited.message);
        verify(mockUserRepository).createUser(USER_ID);
    }

    @Test
    public void testAddRestaurantToLikedSuccess() throws InterruptedException {
        liveDataAddToLiked.setValue(true);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.addRestaurantToLiked(USER_ID, RESTAURANT_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertTrue(resultAwaited.data);
    }

    @Test
    public void testAddRestaurantToLikedFailure() throws InterruptedException {
        liveDataAddToLiked.setValue(false);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.addRestaurantToLiked(USER_ID, RESTAURANT_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertFalse(resultAwaited.data);
        Assert.assertEquals("Failed to add to liked list", resultAwaited.message);
    }

    @Test
    public void testRemoveRestaurantFromLikedSuccess() throws InterruptedException {
        liveDataRemoveToLiked.setValue(true);
        Resource<Boolean> result = LiveDataTestUtils.getOrAwaitValue(SUT.removeRestaurantFromLiked(USER_ID, RESTAURANT_ID));
        Assert.assertNotNull(result);
        Assert.assertEquals(Resource.Status.SUCCESS, result.status);
        Assert.assertTrue(result.data);
    }

    @Test
    public void testRemoveRestaurantFromLikedFailure() throws InterruptedException {
        liveDataRemoveToLiked.setValue(false);
        Resource<Boolean> result = LiveDataTestUtils.getOrAwaitValue(SUT.removeRestaurantFromLiked(USER_ID, RESTAURANT_ID));
        Assert.assertNotNull(result);
        Assert.assertEquals(Resource.Status.ERROR, result.status);
        Assert.assertFalse(result.data);
        Assert.assertEquals("Failed to remove from liked list", result.message);
    }

    @Test
    public void testGetRestaurantChoiceSuccess() throws InterruptedException {
        RestaurantChoice choice = new RestaurantChoice(RESTAURANT_ID, CHOICE_DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS);
        liveDataGetRestaurantChoice.setValue(choice);
        Resource<RestaurantChoice> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getRestaurantChoice(USER_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertEquals(choice, resultAwaited.data);
    }

    @Test
    public void testGetRestaurantChoiceFailure() throws InterruptedException {
        liveDataGetRestaurantChoice.setValue(null);
        Resource<RestaurantChoice> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getRestaurantChoice(USER_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertNull(resultAwaited.data);
    }

    @Test
    public void testSetRestaurantChoiceSuccess() throws InterruptedException {
        liveDataSetRestaurantChoice.setValue(true);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.setRestaurantChoice(USER_ID, RESTAURANT_ID, CHOICE_DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertTrue(resultAwaited.data);
    }

    @Test
    public void testSetRestaurantChoiceFailure() throws InterruptedException {
        liveDataSetRestaurantChoice.setValue(false);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.setRestaurantChoice(USER_ID, RESTAURANT_ID, CHOICE_DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertFalse(resultAwaited.data);
        Assert.assertEquals("Failed to set restaurant choice", resultAwaited.message);
    }

    @Test
    public void testRemoveRestaurantChoiceSuccess() throws InterruptedException {
        liveDataRemoveRestaurantChoice.setValue(true);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.removeRestaurantChoice(USER_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertTrue(resultAwaited.data);
    }

    @Test
    public void testRemoveRestaurantChoiceFailure() throws InterruptedException {
        liveDataRemoveRestaurantChoice.setValue(false);
        Resource<Boolean> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.removeRestaurantChoice(USER_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertFalse(resultAwaited.data);
        Assert.assertEquals("Failed to remove restaurant choice", resultAwaited.message);
    }

    @Test
    public void testGetUsersByRestaurantChoiceSuccess() throws InterruptedException {
        listUsersByRestaurantChoice.add(new User(USER_ID, USER_NAME, USER_URL_PICTURE, USER_EMAIL, USER_RESTAURANTS_LIKE, new RestaurantChoice(RESTAURANT_ID, CHOICE_DATE, RESTAURANT_NAME, RESTAURANT_ADDRESS)));
        liveDataUsersByRestaurantChoice.setValue(listUsersByRestaurantChoice);
        Resource<List<User>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getUsersByRestaurantChoice(RESTAURANT_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertFalse(resultAwaited.data.isEmpty());
        Assert.assertEquals(USER_NAME, resultAwaited.data.get(0).getUserName());
    }

    @Test
    public void testGetUsersByRestaurantChoiceFailure() throws InterruptedException {
        liveDataUsersByRestaurantChoice.setValue(null);
        Resource<List<User>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getUsersByRestaurantChoice(RESTAURANT_ID));
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertNull(resultAwaited.data);
    }

    @Test
    public void testGetAllRestaurantChoices() throws InterruptedException {
        liveDataRestaurantChoices.setValue(getRestaurantsChoices());
        Resource<Map<String, List<User>>> allChoices = LiveDataTestUtils.getOrAwaitValue(SUT.getAllRestaurantChoices());
        String user1 = Objects.requireNonNull(allChoices.data.get("users")).get(0).getUserName();
        String user2 = Objects.requireNonNull(allChoices.data.get("users")).get(1).getUserName();
        Assert.assertEquals("user1", user1);
        Assert.assertEquals("user2", user2);
    }

    @Test
    public void testGetAllRestaurantChoicesFailure() throws InterruptedException {
        liveDataRestaurantChoices.setValue(getRestaurantsChoicesFailure());
        Resource<Map<String, List<User>>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getAllRestaurantChoices());
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertNull(resultAwaited.data);
        Assert.assertEquals("Error fetching restaurant choices", resultAwaited.message);
    }

    private Resource<Map<String, List<User>>> getRestaurantsChoices() {
        User user1 = new User();
        user1.setUserName("user1");
        User user2 = new User();
        user2.setUserName("user2");
        List<User> users = Arrays.asList(user1, user2);
        Map<String, List<User>> userMap = new HashMap<>();
        userMap.put("users", users);
        return Resource.success(userMap);
    }

    private Resource<Map<String, List<User>>> getRestaurantsChoicesFailure() {
        return Resource.error("Error fetching restaurant choices", null);
    }

    @Test
    public void testGetChosenRestaurantIdsSuccess() throws InterruptedException {
        listStringChosenRestaurantIds.add(RESTAURANT_ID);
        liveDataChosenRestaurantIds.setValue(listStringChosenRestaurantIds);

        Resource<List<String>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getChosenRestaurantIds());

        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertFalse(resultAwaited.data.isEmpty());
        Assert.assertEquals(RESTAURANT_ID, resultAwaited.data.get(0));

    }

    @Test
    public void testGetChosenRestaurantIdsFailure() throws InterruptedException {
        liveDataChosenRestaurantIds.setValue(null);
        Resource<List<String>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getChosenRestaurantIds());
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertNull(resultAwaited.data);

    }

    @Test
    public void testRefreshChosenRestaurantIdsSuccess() throws InterruptedException {
        listStringChosenRestaurantIds.add(RESTAURANT_ID);
        liveDataChosenRestaurantIds.setValue(listStringChosenRestaurantIds);

        SUT.refreshChosenRestaurantIds();
        Resource<List<String>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getChosenRestaurantIds());

        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.SUCCESS, resultAwaited.status);
        Assert.assertNotNull(resultAwaited.data);
        Assert.assertEquals(listStringChosenRestaurantIds, resultAwaited.data);

    }

    @Test
    public void testRefreshChosenRestaurantIdsFailure() throws InterruptedException {
        liveDataChosenRestaurantIds.setValue(null);

        SUT.refreshChosenRestaurantIds();
        Resource<List<String>> resultAwaited = LiveDataTestUtils.getOrAwaitValue(SUT.getChosenRestaurantIds());
        Assert.assertNotNull(resultAwaited);
        Assert.assertEquals(Resource.Status.ERROR, resultAwaited.status);
        Assert.assertNull(resultAwaited.data);
        Assert.assertEquals("Error fetching chosen restaurant ids", resultAwaited.message);

    }

}