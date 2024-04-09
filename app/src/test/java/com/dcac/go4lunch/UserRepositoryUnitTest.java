package com.dcac.go4lunch;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.repository.AuthService;
import com.dcac.go4lunch.repository.UserRepository;
import com.dcac.go4lunch.utils.Resource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FieldValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RunWith(RobolectricTestRunner.class)
public class UserRepositoryUnitTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private CollectionReference mockCollection;

    @Mock
    private DocumentReference mockDocumentRef;

    @Mock
    private Task<DocumentSnapshot> mockTask;

    @Mock
    private AuthService mockAuthService;

    @Mock
    private CollectionReference mockUsersCollection;

    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> captorUser;

    RestaurantChoice testRestaurantChoice = new RestaurantChoice("restaurantId123", "2022-04-01", "Test Restaurant", "123 Test St");

    @Before
    public void setUp() {

        userRepository= UserRepository.getInstance(mockAuthService, mockUsersCollection);

        // Simulate Firestore and FirebaseAuth interactions
        when(mockCollection.document(anyString())).thenReturn(mockDocumentRef);
        when(mockDocumentRef.get()).thenReturn(mockTask);

        MockitoAnnotations.initMocks(this);


        // Configure mocked success for the method get on DocumentReference
        when(mockTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener onSuccessListener = invocation.getArgument(0);
            DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
            // Simulate datas in DocumentSnapshot if necessary
            // when(mockDocumentSnapshot.exists()).thenReturn(true);
            // when(mockDocumentSnapshot.toObject(User.class)).thenReturn(new User(...));
            onSuccessListener.onSuccess(mockDocumentSnapshot);
            return mockTask;
        });

        // Create UserRepository with Mocked AuthService
        //userRepository = new UserRepository(mockAuthService, mockUsersCollection);

        // Inject MockCollection in userRepository
        //userRepository.setUsersCollection(mockCollection);
    }

    private <T> T ValueLiveData(LiveData<T> liveData) throws InterruptedException {
        final Object[] result = new Object[1];
        CountDownLatch lock = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T value) {
                result[0] = value;
                lock.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        lock.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) result[0];
    }


    //Verify that the method getUserData() work correctly
    @Test
    public void getUserDataShouldReturnUserData() throws InterruptedException {
        // Configure the mock for return a DocumentSnapshot when get() is called
        when(mockDocumentRef.get()).thenAnswer(invocation -> {
            Task<DocumentSnapshot> task = mock(Task.class);
            when(task.addOnSuccessListener(any())).thenAnswer(invocationOnSuccess -> {
                OnSuccessListener<DocumentSnapshot> onSuccessListener = invocationOnSuccess.getArgument(0);
                DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
                // Simulate data in DocumentSnapshot
                when(mockDocumentSnapshot.exists()).thenReturn(true);
                when(mockDocumentSnapshot.toObject(User.class)).thenReturn(new User("uid", "Test User", null, "email@test.com", new ArrayList<>(), null));
                onSuccessListener.onSuccess(mockDocumentSnapshot);
                return task;
            });
            return task;
        });

        // Call the method to test
        LiveData<DocumentSnapshot> result = userRepository.getUserData("testUid");

        // Use ValueLiveData for wait the response
        DocumentSnapshot fetchedData = ValueLiveData(result);

        // Verify that the DocumentSnapshot recover isn't null
        assertNotNull("DocumentSnapshot should not be null", fetchedData);
        assertTrue("DocumentSnapshot should exist", fetchedData.exists());

        // Conversion of the DocumentSnapshot into an User object and verifications
        User user = fetchedData.toObject(User.class);
        assertNotNull("Object User should not be null", user);
        assertEquals("UID should match", "uid", user.getUid());
        assertEquals("Name should match", "Test User", user.getUserName());
        assertEquals("Email should match", "email@test.com", user.getEmail());
    }

    // Error scenario for getUserDataShouldReturnUserData()
    @Test
    public void getUserDataShouldHandleFailure() throws InterruptedException {
        // Configure the mock to return a failure when get() is called
        when(mockDocumentRef.get()).thenReturn(Tasks.forException(new Exception("Failed to fetch")));

        // Call the method to test
        LiveData<DocumentSnapshot> result = userRepository.getUserData("testUid");

        // Use the utility method to wait for the LiveData response
        DocumentSnapshot fetchedData = ValueLiveData(result);

        // Since we simulate a failure, there should be no data
        assertNull("Fetched data should be null on failure", fetchedData);
    }

    // Verify if getAllUsers() return a users list correctly
    @Test
    public void getAllUsersReturnsCorrectData() throws InterruptedException {
        // Simulate the QuerySnapshot with specific data
        Task<QuerySnapshot> mockTask = Tasks.forResult(mock(QuerySnapshot.class));
        List<DocumentSnapshot> mockDocumentSnapshots = new ArrayList<>();

        // Create and configure 2 DocumentSnapshots for simulating users
        DocumentSnapshot docSnapshot1 = mock(DocumentSnapshot.class);
        when(docSnapshot1.toObject(User.class)).thenReturn(new User("uid1", "User One", null, "email1@test.com", new ArrayList<>(), null));
        mockDocumentSnapshots.add(docSnapshot1);

        DocumentSnapshot docSnapshot2 = mock(DocumentSnapshot.class);
        when(docSnapshot2.toObject(User.class)).thenReturn(new User("uid2", "User Two", null, "email2@test.com", new ArrayList<>(), null));
        mockDocumentSnapshots.add(docSnapshot2);

        // Configure mockQuerySnapshot to return simulated DocumentSnapshots
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockQuerySnapshot.getDocuments()).thenReturn(mockDocumentSnapshots);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);
        when(mockUsersCollection.get()).thenReturn(mockTask);

        // Call the method to test
        LiveData<QuerySnapshot> resultLiveData = userRepository.getAllUsers();

        // Use the utility method to wait for the LiveData response
        QuerySnapshot fetchedData = ValueLiveData(resultLiveData);

        assertNotNull("QuerySnapshot returned should not be null", fetchedData);
        assertEquals("QuerySnapshot should contain 2 documents", 2, fetchedData.getDocuments().size());

        // Example of using JUnit assertion to compare expected users with actual users
        User actualUser1 = fetchedData.getDocuments().get(0).toObject(User.class);
        User actualUser2 = fetchedData.getDocuments().get(1).toObject(User.class);
        assertEquals("User One", actualUser1.getUserName());
        assertEquals("email1@test.com", actualUser1.getEmail());
        assertEquals("User Two", actualUser2.getUserName());
        assertEquals("email2@test.com", actualUser2.getEmail());

        // Verifying interaction with mock
        verify(mockUsersCollection).get();
    }


    // Error scenario for getAllUsersReturnsCorrectData()
    @Test
    public void getAllUsersHandlesFailure() throws InterruptedException {
        // Configure the mock to simulate a failure
        Task<QuerySnapshot> failedTask = Tasks.forException(new Exception("Failed to fetch users"));
        when(mockUsersCollection.get()).thenReturn(failedTask);

        // Call the method under test
        LiveData<QuerySnapshot> resultLiveData = userRepository.getAllUsers();

        // Use the utility method to wait for the LiveData response
        QuerySnapshot fetchedData = ValueLiveData(resultLiveData);

        // Verify fetched data is null due to failure
        assertNull("Fetched data should be null on failure", fetchedData);
    }


    //Verify if "createUser()" create an user with success
    @Test
    public void createUserCreatesUserSuccessfully() throws InterruptedException {
        // Mock FirebaseUser returned by FirebaseAuth
        FirebaseUser mockFirebaseUser = Mockito.mock(FirebaseUser.class);
        when(mockFirebaseUser.getDisplayName()).thenReturn("Test Name");
        when(mockFirebaseUser.getEmail()).thenReturn("test@example.com");
        when(FirebaseAuth.getInstance().getCurrentUser()).thenReturn(mockFirebaseUser);

        // Simulate successful user creation in Firestore
        Task<Void> mockCreationTask = Tasks.forResult(null); // Simulate a successful task
        when(mockUsersCollection.document(anyString()).set(any(User.class))).thenReturn(mockCreationTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.createUser("uid");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Result verification
        assertTrue("User creation should be a success", result);

        // Capture and verify the user data sent to Firestore
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(mockUsersCollection.document("uid")).set(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNotNull("Captured user should not be null", capturedUser);
        assertEquals("User name should match", "Test Name", capturedUser.getUserName());
        assertEquals("User email should match", "test@example.com", capturedUser.getEmail());

    }

    // Error scenario for createUser()
    @Test
    public void createUserHandlesFailure() throws InterruptedException {
        // Suppose that FirebaseUser is correctly returned by FirebaseAuth
        FirebaseUser mockFirebaseUser = mock(FirebaseUser.class);
        when(FirebaseAuth.getInstance().getCurrentUser()).thenReturn(mockFirebaseUser);

        // Simulation of failed creation of a user in Firestore
        Task<Void> failedCreationTask = Tasks.forException(new Exception("Failed to create user"));
        when(mockUsersCollection.document(anyString()).set(any(User.class))).thenReturn(failedCreationTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.createUser("uid");

        // Use ValueLiveData for wait the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that user creation failed
        assertFalse("User creation should fail", result);
    }

    //Verify if "updateUserName()"
    @Test
    public void updateUserName_UpdatesSuccessfully() throws InterruptedException {
        // Mock configuration for simulate a task of a successful
        Task<Void> mockUpdateTask = Tasks.forResult(null); // Simulate a successful task directly
        when(mockUsersCollection.document("uid").update("userName", "New Name")).thenReturn(mockUpdateTask);

        // Call method to test
        LiveData<Boolean> resultLiveData = userRepository.updateUserName("uid", "New Name");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Check the result
        assertTrue("The update of the user name should succeed", result);

        // Verify that the update method is called with the correct arguments
        ArgumentCaptor<String> uidCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockUsersCollection.document(uidCaptor.capture())).update(fieldCaptor.capture(), valueCaptor.capture());

        assertEquals("UID should match", "uid", uidCaptor.getValue());
        assertEquals("Field name should match", "userName", fieldCaptor.getValue());
        assertEquals("New name should match", "New Name", valueCaptor.getValue());
    }

    // Error scenario for createUser()
    @Test
    public void updateUserName_HandlesFailure() throws InterruptedException {
        // Mock configuration for simulate a failure
        Task<Void> failedUpdateTask = Tasks.forException(new Exception("Failed to update user name"));
        when(mockUsersCollection.document("uid").update("userName", "New Name")).thenReturn(failedUpdateTask);

        // Call method to test
        LiveData<Boolean> resultLiveData = userRepository.updateUserName("uid", "New Name");

        // Use ValueLiveData for wait the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that user update failed
        assertFalse("The update of the user name should fail", result);
    }

    // Verify that an user picture is update successfully
    @Test
    public void updateUrlPicture_UpdatesSuccessfully() throws InterruptedException {
        // Configuration of a mock for simulate a successful update task
        Task<Void> mockUpdateTask = Tasks.forResult(null); // Simulate successful task directly
        when(mockUsersCollection.document("uid").update("urlPicture", "newPictureUrl")).thenReturn(mockUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.updateUrlPicture("uid", "newPictureUrl");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Result verification
        assertTrue("The update of the profile image URL should succeed", result);

        // Verify that the update method is called with the correct arguments
        ArgumentCaptor<String> uidCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockUsersCollection.document(uidCaptor.capture())).update(fieldCaptor.capture(), valueCaptor.capture());

        assertEquals("UID should match", "uid", uidCaptor.getValue());
        assertEquals("Field name should match", "urlPicture", fieldCaptor.getValue());
        assertEquals("New URL should match", "newPictureUrl", valueCaptor.getValue());
    }

    // Error scenario for updateUrlPicture()
    @Test
    public void updateUrlPicture_HandlesFailure() throws InterruptedException {
        // Configuration of a mock for simulate a failure
        Task<Void> failedUpdateTask = Tasks.forException(new Exception("Failed to update profile picture URL"));
        when(mockUsersCollection.document("uid").update("urlPicture", "newPictureUrl")).thenReturn(failedUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.updateUrlPicture("uid", "newPictureUrl");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that user update failed
        assertFalse("The update of the profile image URL should fail", result);
    }

    // Verify that like a restaurant work successfully
    @Test
    public void addRestaurantToLiked_AddsSuccessfully() throws InterruptedException {
        // Mock configuration for simulate a successful update task
        Task<Void> mockUpdateTask = Tasks.forResult(null); // Simulate directly a successful task
        when(mockUsersCollection.document("uid").update(eq("restaurantsLike"), eq(FieldValue.arrayUnion("restaurantId")))).thenReturn(mockUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.addRestaurantToLiked("uid", "restaurantId");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Check result
        assertTrue("Adding restaurant to favorites should succeed", result);

        // Verify the update method was called with the correct arguments
        ArgumentCaptor<String> uidCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockUsersCollection.document(uidCaptor.capture())).update(fieldCaptor.capture(), valueCaptor.capture());

        assertEquals("UID should match", "uid", uidCaptor.getValue());
        assertEquals("Field name should match", "restaurantsLike", fieldCaptor.getValue());
        // Note: Direct verification of FieldValue.arrayUnion() is not straightforward due to its unique instance creation
    }

    // Error scenario for addRestaurantToLiked
    @Test
    public void addRestaurantToLiked_HandlesFailure() throws InterruptedException {
        // Mock configuration for simulate a failure
        Task<Void> failedUpdateTask = Tasks.forException(new Exception("Failed to add restaurant to favorites"));
        when(mockUsersCollection.document("uid").update(eq("restaurantsLike"), eq(FieldValue.arrayUnion("restaurantId")))).thenReturn(failedUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.addRestaurantToLiked("uid", "restaurantId");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that adding restaurant to favorites failed
        assertFalse("Adding restaurant to favorites should fail", result);
    }

    // Verify that removing like work successfully
    @Test
    public void removeRestaurantFromLikedRemovesSuccessfully() throws InterruptedException {
        // Mock configuration for simulate a successful update task
        Task<Void> mockUpdateTask = Tasks.forResult(null); // Simulate directly a successful task
        when(mockUsersCollection.document("uid").update(eq("restaurantsLike"), eq(FieldValue.arrayRemove("restaurantId")))).thenReturn(mockUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.removeRestaurantFromLiked("uid", "restaurantId");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Check the result
        assertTrue("Removing restaurant from favorites should succeed", result);

        // Verify that the update method is called with the correct arguments
        ArgumentCaptor<String> uidCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> fieldCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockUsersCollection.document(uidCaptor.capture())).update(fieldCaptor.capture(), valueCaptor.capture());

        assertEquals("UID should match", "uid", uidCaptor.getValue());
        assertEquals("Field name should match", "restaurantsLike", fieldCaptor.getValue());
        // Note: Direct verification of FieldValue.arrayRemove() is not straightforward due to its unique instance creation
    }

    // Error scenario for removeRestaurantFromLiked
    @Test
    public void removeRestaurantFromLikedHandlesFailure() throws InterruptedException {
        // Mock configuration for simulate a failure
        Task<Void> failedUpdateTask = Tasks.forException(new Exception("Failed to remove restaurant from favorites"));
        when(mockUsersCollection.document("uid").update(eq("restaurantsLike"), eq(FieldValue.arrayRemove("restaurantId")))).thenReturn(failedUpdateTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.removeRestaurantFromLiked("uid", "restaurantId");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that removing restaurant from favorites failed
        assertFalse("Removing restaurant from favorites should fail", result);
    }

    // Verify that get a restaurant chosen work successfully
    @Test
    public void getRestaurantChoiceReturnsDataSuccessfully() throws InterruptedException {
        // Mock configuration to simulate the DocumentSnapshot return containing the user
        Task<DocumentSnapshot> mockGetTask = Tasks.forResult(mock(DocumentSnapshot.class));
        RestaurantChoice expectedRestaurantChoice = new RestaurantChoice("restaurantId", "today", "Restaurant Name", "Restaurant Address");
        User mockUser = new User();
        mockUser.setRestaurantChoice(expectedRestaurantChoice);

        when(mockGetTask.isSuccessful()).thenReturn(true);
        when(mockGetTask.getResult().exists()).thenReturn(true);
        when(mockGetTask.getResult().toObject(User.class)).thenReturn(mockUser);
        when(mockUsersCollection.document("uid").get()).thenReturn(mockGetTask);

        // Call the method to test
        LiveData<RestaurantChoice> resultLiveData = userRepository.getRestaurantChoice("uid");

        // Use ValueLiveData to wait for the response
        RestaurantChoice actualRestaurantChoice = ValueLiveData(resultLiveData);

        // Check results
        assertNotNull("The RestaurantChoice returned should not be null", actualRestaurantChoice);
        assertEquals("Restaurant ID should match", expectedRestaurantChoice.getRestaurantId(), actualRestaurantChoice.getRestaurantId());
        assertEquals("Restaurant Name should match", expectedRestaurantChoice.getRestaurantName(), actualRestaurantChoice.getRestaurantName());
        assertEquals("Restaurant Address should match", expectedRestaurantChoice.getRestaurantAddress(), actualRestaurantChoice.getRestaurantAddress());
        assertEquals("Choice Date should match", expectedRestaurantChoice.getChoiceDate(), actualRestaurantChoice.getChoiceDate());
    }

    // Error scenario for getRestaurantChoice
    @Test
    public void getRestaurantChoice_HandlesFailure() throws InterruptedException {
        // Mock configuration to simulate failure
        Task<DocumentSnapshot> failedGetTask = Tasks.forException(new Exception("Failed to fetch restaurant choice"));
        when(mockUsersCollection.document("uid").get()).thenReturn(failedGetTask);

        // Call the method under test
        LiveData<RestaurantChoice> resultLiveData = userRepository.getRestaurantChoice("uid");

        // Use ValueLiveData for wait the response
        RestaurantChoice result = ValueLiveData(resultLiveData);

        // Verify that restaurant choice retrieval failed
        assertNull("Restaurant choice retrieval should fail, resulting in null", result);
    }

    // Verify that the Choice of a restaurant is successfully made
    @Test
    public void setRestaurantChoiceSetsSuccessfully() throws InterruptedException {
        RestaurantChoice expectedRestaurantChoice = new RestaurantChoice("restaurantId", "choiceDate", "restaurantName", "restaurantAddress");

        // Mock configuration for simulate a successful update task
        Task<Void> mockSetTask = Tasks.forResult(null);
        when(mockUsersCollection.document("uid").update("restaurantChoice", expectedRestaurantChoice))
                .thenReturn(mockSetTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.setRestaurantChoice("uid", "restaurantId", "choiceDate", "restaurantName", "restaurantAddress");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify the result
        assertTrue("Setting restaurant choice should succeed", result);

        // Verify the update method is called with the correct arguments
        ArgumentCaptor<RestaurantChoice> restaurantChoiceCaptor = ArgumentCaptor.forClass(RestaurantChoice.class);
        verify(mockUsersCollection.document("uid")).update(eq("restaurantChoice"), restaurantChoiceCaptor.capture());
        RestaurantChoice capturedChoice = restaurantChoiceCaptor.getValue();

        assertNotNull("Captured restaurant choice should not be null", capturedChoice);
        assertEquals("Restaurant ID should match", "restaurantId", capturedChoice.getRestaurantId());
        assertEquals("Choice date should match", "choiceDate", capturedChoice.getChoiceDate());
        assertEquals("Restaurant name should match", "restaurantName", capturedChoice.getRestaurantName());
        assertEquals("Restaurant address should match", "restaurantAddress", capturedChoice.getRestaurantAddress());
    }

    // Error scenario for setRestaurantChoice
    @Test
    public void setRestaurantChoiceHandlesFailure() throws InterruptedException {
        // Mock configuration for simulate a failed update task
        Task<Void> failedSetTask = Tasks.forException(new Exception("Failed to set restaurant choice"));
        when(mockUsersCollection.document("uid").update(eq("restaurantChoice"), any(RestaurantChoice.class)))
                .thenReturn(failedSetTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.setRestaurantChoice("uid", "restaurantId", "choiceDate", "restaurantName", "restaurantAddress");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that setting restaurant choice failed
        assertFalse("Setting restaurant choice should fail", result);
    }

    // Verify that the choice of a restaurant is successfully remove
    @Test
    public void removeRestaurantChoiceRemovesSuccessfully() throws InterruptedException {
        // Mock configuration for simulate a successful update task
        Task<Void> mockRemoveTask = Tasks.forResult(null);
        when(mockUsersCollection.document("uid").update("restaurantChoice", null))
                .thenReturn(mockRemoveTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.removeRestaurantChoice("uid");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify the result
        assertTrue("Removing restaurant choice should succeed", result);

        // Verify that the update method is called with the correct arguments
        ArgumentCaptor<String> uidCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockUsersCollection.document(uidCaptor.capture())).update(eq("restaurantChoice"), (Object) null);
        assertEquals("Captured UID should match", "uid", uidCaptor.getValue());
    }

    // Error scenario for removeRestaurantChoice
    @Test
    public void removeRestaurantChoiceHandlesFailure() throws InterruptedException {
        // Mock configuration for simulate a failure in updating task
        Task<Void> failedRemoveTask = Tasks.forException(new Exception("Failed to remove restaurant choice"));
        when(mockUsersCollection.document("uid").update("restaurantChoice", null))
                .thenReturn(failedRemoveTask);

        // Call the method to test
        LiveData<Boolean> resultLiveData = userRepository.removeRestaurantChoice("uid");

        // Use ValueLiveData to wait for the response
        Boolean result = ValueLiveData(resultLiveData);

        // Verify that removing restaurant choice failed
        assertFalse("Removing restaurant choice should fail", result);
    }

    // Verify that recover user by restaurant choice work successfully
    @Test
    public void getUsersByRestaurantChoiceGetsUsersSuccessfully() throws InterruptedException {
        // Mock the successful retrieval of a QuerySnapshot from Firestore
        List<DocumentSnapshot> documentSnapshots = new ArrayList<>();
        DocumentSnapshot docSnapshot1 = mock(DocumentSnapshot.class);
        DocumentSnapshot docSnapshot2 = mock(DocumentSnapshot.class);
        when(docSnapshot1.toObject(User.class)).thenReturn(new User("uid1", "User One", null, "email1@test.com", new ArrayList<>(), new RestaurantChoice("restaurantId", "today", "Restaurant Name", "Restaurant Address")));
        when(docSnapshot2.toObject(User.class)).thenReturn(new User("uid2", "User Two", null, "email2@test.com", new ArrayList<>(), new RestaurantChoice("restaurantId", "yesterday", "Restaurant Name", "Restaurant Address")));
        documentSnapshots.add(docSnapshot1);
        documentSnapshots.add(docSnapshot2);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documentSnapshots);
        Task<QuerySnapshot> mockGetTask = Tasks.forResult(mockQuerySnapshot);
        when(mockUsersCollection.whereEqualTo("restaurantChoice.restaurantId", "restaurantId").get()).thenReturn(mockGetTask);

        // Call the method under test
        LiveData<List<User>> resultLiveData = userRepository.getUsersByRestaurantChoice("restaurantId");

        // Use ValueLiveData to wait for the response
        List<User> fetchedUsers = ValueLiveData(resultLiveData);

        // Verify the results
        assertNotNull("The returned list of users should not be null", fetchedUsers);
        assertEquals("The returned list should contain 2 users", 2, fetchedUsers.size());

    }

    // Error scenario for getUserByRestaurantChoice
    @Test
    public void getUsersByRestaurantChoiceHandlesFailure() throws InterruptedException {
        // Mock the failure in retrieval of a QuerySnapshot from Firestore
        Task<QuerySnapshot> failedTask = Tasks.forException(new Exception("Failed to fetch users"));
        when(mockUsersCollection.whereEqualTo("restaurantChoice.restaurantId", "restaurantId").get()).thenReturn(failedTask);

        // Call the method under test
        LiveData<List<User>> resultLiveData = userRepository.getUsersByRestaurantChoice("restaurantId");

        // Use ValueLiveData to wait for the response
        List<User> fetchedUsers = ValueLiveData(resultLiveData);

        // Verify fetched data is null or empty due to failure
        assertTrue("Fetched users list should be null or empty on failure", fetchedUsers == null || fetchedUsers.isEmpty());
    }

    // Verify that getChosenRestaurantIds work successfully
    @Test
    public void getChosenRestaurantIdsGetsIdsSuccessfully() throws InterruptedException {
        // Mock DocumentSnapshots representing user documents with different restaurant choices
        DocumentSnapshot docSnapshot1 = mock(DocumentSnapshot.class);
        User user1 = new User();
        user1.setRestaurantChoice(new RestaurantChoice("restaurantId1", "date1", "Restaurant Name 1", "Restaurant Address 1"));
        when(docSnapshot1.toObject(User.class)).thenReturn(user1);

        DocumentSnapshot docSnapshot2 = mock(DocumentSnapshot.class);
        User user2 = new User();
        user2.setRestaurantChoice(new RestaurantChoice("restaurantId2", "date2", "Restaurant Name 2", "Restaurant Address 2"));
        when(docSnapshot2.toObject(User.class)).thenReturn(user2);

        List<DocumentSnapshot> documentSnapshots = Arrays.asList(docSnapshot1, docSnapshot2);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockQuerySnapshot.getDocuments()).thenReturn(documentSnapshots);

        // Simulate a successful Task<QuerySnapshot>
        Task<QuerySnapshot> mockGetTask = Tasks.forResult(mockQuerySnapshot);
        when(mockUsersCollection.get()).thenReturn(mockGetTask);

        // Call the method under test
        LiveData<List<String>> resultLiveData = userRepository.getChosenRestaurantIds();

        // Use ValueLiveData to wait for the response
        List<String> fetchedRestaurantIds = ValueLiveData(resultLiveData);

        // Verify the results
        assertNotNull("The returned list of restaurant IDs should not be null", fetchedRestaurantIds);
        assertTrue("The returned list should contain the expected restaurant IDs", fetchedRestaurantIds.containsAll(Arrays.asList("restaurantId1", "restaurantId2")));
    }

    // Error scenario for getChosenRestaurantIds
    @Test
    public void getChosenRestaurantIdsHandlesFailure() throws InterruptedException {
        // Simulate a failed Task<QuerySnapshot>
        Task<QuerySnapshot> failedTask = Tasks.forException(new Exception("Failed to fetch restaurant IDs"));
        when(mockUsersCollection.get()).thenReturn(failedTask);

        // Call the method under test
        LiveData<List<String>> resultLiveData = userRepository.getChosenRestaurantIds();

        // Use ValueLiveData to wait for the response
        List<String> fetchedRestaurantIds = ValueLiveData(resultLiveData);

        // Verify fetched data is null or empty due to failure
        assertTrue("Fetched restaurant IDs list should be null or empty on failure", fetchedRestaurantIds == null || fetchedRestaurantIds.isEmpty());
    }

    // Verify getAllRestaurantChoices
    @Test
    public void getAllRestaurantChoicesGetsAllChoicesSuccessfully() throws InterruptedException {
        // Simulate fetching of data
        Map<String, List<User>> expectedData = new HashMap<>();
        expectedData.put("restaurantId1", Arrays.asList(
                new User("uid1", "User One", null, "email1@example.com", new ArrayList<>(), null),
                new User("uid2", "User Two", null, "email2@example.com", new ArrayList<>(), null)
        ));

        Resource<Map<String, List<User>>> successResource = Resource.success(expectedData);

        MutableLiveData<Resource<Map<String, List<User>>>> liveData = new MutableLiveData<>();
        liveData.setValue(successResource);

        when(userRepository.getAllRestaurantChoices()).thenReturn(liveData);

        // Call the method under test
        Resource<Map<String, List<User>>> result = ValueLiveData(userRepository.getAllRestaurantChoices());

        // Verify the results
        assertNotNull("The returned Resource should not be null", result);
        assertEquals("The status of the returned Resource should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data in the returned Resource should not be null", result.data);
        assertTrue("The data should contain the expected keys", result.data.keySet().containsAll(expectedData.keySet()));
    }

    // Error scenario for getAllRestaurantChoices
    @Test
    public void getAllRestaurantChoicesHandlesFailure() throws InterruptedException {
        Resource<Map<String, List<User>>> errorResource = Resource.error("Failed to fetch data", null);

        MutableLiveData<Resource<Map<String, List<User>>>> liveData = new MutableLiveData<>();
        liveData.setValue(errorResource);

        when(userRepository.getAllRestaurantChoices()).thenReturn(liveData);

        // Call the method under test
        Resource<Map<String, List<User>>> result = ValueLiveData(userRepository.getAllRestaurantChoices());

        // Verify that an error occurred
        assertNotNull("The returned Resource should not be null", result);
        assertEquals("The status of the returned Resource should be ERROR", Resource.Status.ERROR, result.status);
        assertNull("The data in the returned Resource should be null on error", result.data);
        assertEquals("The error message should match", "Failed to fetch data", result.message);
    }

    // Verify Sign out work successfully
    @Test
    public void signOutSignsOutSuccessfully() throws InterruptedException {
        // Simulate AuthService.signOut() to return a success
        MutableLiveData<Resource<Void>> mockSignOutResult = new MutableLiveData<>();
        mockSignOutResult.setValue(Resource.success(null));
        when(mockAuthService.signOut()).thenReturn(mockSignOutResult);

        // Call the signOut method on the UserRepository
        LiveData<Resource<Void>> resultLiveData = userRepository.signOut();

        // Use ValueLiveData to wait for the response
        Resource<Void> fetchedResult = ValueLiveData(resultLiveData);

        // Verify the results
        assertNotNull("The returned Resource should not be null", fetchedResult);
        assertEquals("The status of the returned Resource should be SUCCESS", Resource.Status.SUCCESS, fetchedResult.status);
        assertNull("The data in the returned Resource should be null", fetchedResult.data);
        assertNull("The message in the returned Resource should be null", fetchedResult.message);
    }

    // Error scenario for SignOut
    @Test
    public void signOutHandlesFailure() throws InterruptedException {
        // Simulate AuthService.signOut() to return an error
        MutableLiveData<Resource<Void>> mockSignOutResult = new MutableLiveData<>();
        mockSignOutResult.setValue(Resource.error("Error signing out", null));
        when(mockAuthService.signOut()).thenReturn(mockSignOutResult);

        // Call the signOut method on the UserRepository
        LiveData<Resource<Void>> resultLiveData = userRepository.signOut();

        // Use ValueLiveData to wait for the response
        Resource<Void> fetchedResult = ValueLiveData(resultLiveData);

        // Verify that an error occurred
        assertNotNull("The returned Resource should not be null", fetchedResult);
        assertEquals("The status of the returned Resource should be ERROR", Resource.Status.ERROR, fetchedResult.status);
        assertNull("The data in the returned Resource should be null on error", fetchedResult.data);
        assertEquals("The error message should match", "Error signing out", fetchedResult.message);
    }

    // Verify that delete user work successfully
    @Test
    public void deleteUser_DeletesUserSuccessfully() throws InterruptedException {
        // Simulate AuthService.deleteUser() to return a success
        MutableLiveData<Resource<Void>> mockDeleteUserResult = new MutableLiveData<>();
        mockDeleteUserResult.setValue(Resource.success(null));
        when(mockAuthService.deleteUser()).thenReturn(mockDeleteUserResult);

        // Call the deleteUser method on the UserRepository
        LiveData<Resource<Void>> resultLiveData = userRepository.deleteUser();

        // Use ValueLiveData to wait for the response
        Resource<Void> fetchedResult = ValueLiveData(resultLiveData);

        // Verify the results
        assertNotNull("The returned Resource should not be null", fetchedResult);
        assertEquals("The status of the returned Resource should be SUCCESS", Resource.Status.SUCCESS, fetchedResult.status);
        assertNull("The data in the returned Resource should be null", fetchedResult.data);
        assertNull("The message in the returned Resource should be null", fetchedResult.message);
    }

    // Error scenario for deleting user
    @Test
    public void deleteUser_HandlesFailure() throws InterruptedException {
        // Simulate AuthService.deleteUser() to return an error
        MutableLiveData<Resource<Void>> mockDeleteUserResult = new MutableLiveData<>();
        mockDeleteUserResult.setValue(Resource.error("Error deleting user", null));
        when(mockAuthService.deleteUser()).thenReturn(mockDeleteUserResult);

        // Call the deleteUser method on the UserRepository
        LiveData<Resource<Void>> resultLiveData = userRepository.deleteUser();

        // Use ValueLiveData to wait for the response
        Resource<Void> fetchedResult = ValueLiveData(resultLiveData);

        // Verify that an error occurred
        assertNotNull("The returned Resource should not be null", fetchedResult);
        assertEquals("The status of the returned Resource should be ERROR", Resource.Status.ERROR, fetchedResult.status);
        assertNull("The data in the returned Resource should be null on error", fetchedResult.data);
        assertEquals("The error message should match", "Error deleting user", fetchedResult.message);
    }

}
