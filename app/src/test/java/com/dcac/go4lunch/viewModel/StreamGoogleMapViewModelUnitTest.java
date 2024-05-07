package com.dcac.go4lunch.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Predictions;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.Opening_hours;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.Result;
import com.dcac.go4lunch.repository.StreamGoogleMapRepository;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.testDouble.StreamGoogleMapRepositoryTestDouble;
import com.dcac.go4lunch.utils.LiveDataTestUtils;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


// Use MockitoJUnitRunner for initialise automatically the annotated mocks
public class StreamGoogleMapViewModelUnitTest {

    // This rule ensure that tasks of the LiveData are immediately executed on the principal thread
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // Recuperation of the RepositoryTestDouble
    private IStreamGoogleMap testDoubleRepository;

    // Declaration of the ViewModel who being tested
    private StreamGoogleMapViewModel viewModel;

    @Before
    public void setUp() {
        // Initialise all annotated mocks
        MockitoAnnotations.initMocks(this);
        // instantiate the ViewModel with the mocked repository
        testDoubleRepository = new StreamGoogleMapRepositoryTestDouble();
        viewModel = new StreamGoogleMapViewModel(testDoubleRepository);
    }

    @Test
    public void getNearbyPlacesReturnsExpectedValues() throws InterruptedException {

        LiveData<Resource<PlaceNearbySearch>> valueAwaited = viewModel.getNearbyPlaces
                ("mockLocation", 1000, "restaurant", "mockToken");
        Resource<PlaceNearbySearch> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        assertNotNull("The result should not be null", result);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data should not be null", result.data);
        assertNotNull("The details should contain a Result object", result.data.getResults().get(0).getName());
        assertEquals("The place name should match the expected test data", "Test Place",
                result.data.getResults().get(0).getName());
        assertEquals("The place name should match the expected test data", "Test Location",
                result.data.getResults().get(0).getVicinity());

        /*Resource<PlaceNearbySearch> valueAwaited = LiveDataTestUtils.getOrAwaitValue(viewModel.getNearbyPlaces("mockLocation", 1000, "restaurant", "mockToken"));
        PlaceNearbySearch nbs = valueAwaited.data;

        assertEquals("Test Place", nbs.getResults().get(0).getName());
        assertEquals("Test Location", nbs.getResults().get(0).getVicinity());*/

    }

    @Test
    public void getCombinedNearbyPlacesReturnsExpectedValues() throws InterruptedException {
        // Arrange
        List<String> types = Arrays.asList("restaurant", "cafe");
        viewModel.fetchAndStoreNearbyPlaces("mockLocation", 1000, types);

        // Act
        Resource<List<PlaceNearbySearch>> valueAwaited = LiveDataTestUtils.getOrAwaitValue(viewModel.getStoredNearbyPlaces());

        // Assert
        assertNotNull("The observed value should not be null", valueAwaited);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, valueAwaited.status);
        assertNotNull("The data should not be null", valueAwaited.data);
        assertEquals("There should be two elements in the data list (one for each type)",
                2, valueAwaited.data.size());

        // Check details for each type and each place
        for (int i = 0; i < valueAwaited.data.size(); i++) {
            PlaceNearbySearch place = valueAwaited.data.get(i);
            assertEquals("Each PlaceNearbySearch should contain two Results", 2, place.getResults().size());
            String type = types.get(i);  // Direct mapping to type based on index

            for (int j = 0; j < place.getResults().size(); j++) {
                Results result = place.getResults().get(j);
                int placeNumber = j + 1;  // To differentiate between the first and second place of each type

                assertEquals("Test Place " + placeNumber + " for type: " + type, result.getName());
                assertEquals("Test Location " + placeNumber, result.getVicinity());
            }
        }
    }

    @Test
    public void getPlaceDetailsReturnsExpectedValues() throws InterruptedException {

        // Act
        LiveData<Resource<PlaceDetails>> valueAwaited = viewModel.getPlaceDetails("test_place_id");
        Resource<PlaceDetails> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        // Assert
        assertNotNull("The result should not be null", result);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data should not be null", result.data);
        assertNotNull("The details should contain a Result object", result.data.getResult());
        assertEquals("The place name should match the expected test data", "Test Place Details",
                result.data.getResult().getName());
    }

    @Test
    public void getAutoCompletePlacesReturnsExpectedValues() throws InterruptedException {

        // Act
        LiveData<Resource<AutoComplete>> valueAwaited = viewModel.getAutoCompletePlaces("test_input");
        Resource<AutoComplete> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        // Assert
        assertNotNull("The result should not be null", result);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data should not be null", result.data);
        assertNotNull("The predictions list should not be null", result.data.getPredictions());
        assertFalse("The predictions list should not be empty", result.data.getPredictions().isEmpty());

        Predictions prediction = result.data.getPredictions().get(0);
        assertEquals("The description should match the expected test data", "Test Description for input: " + "test_input", prediction.getDescription());
        assertEquals("The place ID should match the expected test data", "test_place_id", prediction.getPlace_id());
        assertEquals("The reference should match the expected test data", "test_reference", prediction.getReference());
        assertEquals("The main text of structured formatting should match", "Main Text Example", prediction.getStructured_formatting().getMain_text());
        assertEquals("The secondary text of structured formatting should match", "Secondary Text Example", prediction.getStructured_formatting().getSecondary_text());
        assertTrue("The types should contain 'restaurant'", prediction.getTypes().contains("restaurant"));
    }

    @Test
    public void getOpeningHoursReturnsExpectedValues() throws InterruptedException {
        List<String> placeIds = Arrays.asList("placeId1", "placeId2");

        // Act
        LiveData<Map<String, String>> valueAwaited = viewModel.getOpeningHours(placeIds);
        Map<String, String> openingHours = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        // Assert
        assertNotNull("Opening hours should not be null", openingHours);
        assertFalse("Opening hours should not be empty", openingHours.isEmpty());
        assertEquals("Should have the correct opening hours for placeId1",
                "Monday: 9 AM - 5 PM", openingHours.get("placeId1"));
        assertEquals("Should have the correct opening hours for placeId2",
                "Wednesday: 8 AM - 4 PM", openingHours.get("placeId2"));
    }

    @Test
    public void getNearbyPlacesReturnsError() throws InterruptedException {
        // Simulate error response
        LiveData<Resource<PlaceNearbySearch>> result = viewModel.getNearbyPlaces("mockLocation", 1000, "restaurant", "mockToken");
        ((MutableLiveData<Resource<PlaceNearbySearch>>) result).postValue(Resource.error("Failed to fetch nearby places due to network error", null));
        Resource<PlaceNearbySearch> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertNull(response.message); // expect message be null
    }

    @Test
    public void getCombinedNearbyPlacesReturnsError() throws InterruptedException {
        // Simulate an error
        LiveData<Resource<List<PlaceNearbySearch>>> result = viewModel.getStoredNearbyPlaces();
        ((MutableLiveData<Resource<List<PlaceNearbySearch>>>) result).postValue(Resource.error("Failed due to server error", null));
        Resource<List<PlaceNearbySearch>> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertNull(response.message); // expect message be null
    }

    @Test
    public void getPlaceDetailsReturnsError() throws InterruptedException {
        // Simulate an error
        LiveData<Resource<PlaceDetails>> result = viewModel.getPlaceDetails("invalid_place_id");
        ((MutableLiveData<Resource<PlaceDetails>>) result).postValue(Resource.error("Error fetching details", null));
        Resource<PlaceDetails> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertNull(response.message); // expect message be null
    }

    @Test
    public void getAutoCompletePlacesReturnsError() throws InterruptedException {
        // Simulate an error
        LiveData<Resource<AutoComplete>> result = viewModel.getAutoCompletePlaces("invalid_input");
        ((MutableLiveData<Resource<AutoComplete>>) result).postValue(Resource.error("Permission denied", null));
        Resource<AutoComplete> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertNull(response.message); // expect message be null
    }

    @Test
    public void getOpeningHoursReturnsError() throws InterruptedException {
        List<String> placeIds = Arrays.asList("invalid_place_id1", "invalid_place_id2");

        // Act
        LiveData<Map<String, String>> result = viewModel.getOpeningHours(placeIds);
        Map<String, String> response = LiveDataTestUtils.getOrAwaitValue(result);

        // Assert
        assertNotNull("The response should not be null", response);
        assertTrue("The map should be empty, indicating that all details fetches failed", response.isEmpty());
    }





}
