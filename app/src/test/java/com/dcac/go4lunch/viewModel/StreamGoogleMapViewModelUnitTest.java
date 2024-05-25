package com.dcac.go4lunch.viewModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Predictions;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.testDouble.StreamGoogleMapRepositoryTestDouble;
import com.dcac.go4lunch.utils.LiveDataTestUtils;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


// Use MockitoJUnitRunner for initialise automatically the annotated mocks
public class StreamGoogleMapViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private StreamGoogleMapViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        IStreamGoogleMap testDoubleRepository = new StreamGoogleMapRepositoryTestDouble();
        viewModel = new StreamGoogleMapViewModel(testDoubleRepository);
    }

    @Test
    public void getNearbyPlacesReturnsExpectedValues() throws InterruptedException {
        LiveData<Resource<PlaceNearbySearch>> valueAwaited = viewModel.getNearbyPlaces("mockLocation", 1000, "restaurant", "mockToken");
        Resource<PlaceNearbySearch> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        assertNotNull("The result should not be null", result);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data should not be null", result.data);
        assertNotNull("The details should contain a Result object", result.data.getResults().get(0).getName());
        assertEquals("The place name should match the expected test data", "Test Place", result.data.getResults().get(0).getName());
        assertEquals("The place name should match the expected test data", "Test Location", result.data.getResults().get(0).getVicinity());
    }

    @Test
    public void getCombinedNearbyPlacesReturnsExpectedValues() throws InterruptedException {
        List<String> types = Arrays.asList("restaurant", "cafe");
        viewModel.fetchAndStoreNearbyPlaces("mockLocation", 1000, types);

        Resource<List<PlaceNearbySearch>> valueAwaited = LiveDataTestUtils.getOrAwaitValue(viewModel.getStoredNearbyPlaces());

        assertNotNull("The observed value should not be null", valueAwaited);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, valueAwaited.status);
        assertNotNull("The data should not be null", valueAwaited.data);
        assertEquals("There should be two elements in the data list (one for each type)", 2, valueAwaited.data.size());

        for (int i = 0; i < valueAwaited.data.size(); i++) {
            PlaceNearbySearch place = valueAwaited.data.get(i);
            assertEquals("Each PlaceNearbySearch should contain two Results", 2, place.getResults().size());
            String type = types.get(i);

            for (int j = 0; j < place.getResults().size(); j++) {
                Results result = place.getResults().get(j);
                int placeNumber = j + 1;

                assertEquals("Test Place " + placeNumber + " for type: " + type, result.getName());
                assertEquals("Test Location " + placeNumber, result.getVicinity());
            }
        }
    }

    @Test
    public void getPlaceDetailsReturnsExpectedValues() throws InterruptedException {
        LiveData<Resource<PlaceDetails>> valueAwaited = viewModel.getPlaceDetails("test_place_id");
        Resource<PlaceDetails> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        assertNotNull("The result should not be null", result);
        assertEquals("The status should be SUCCESS", Resource.Status.SUCCESS, result.status);
        assertNotNull("The data should not be null", result.data);
        assertNotNull("The details should contain a Result object", result.data.getResult());
        assertEquals("The place name should match the expected test data", "Test Place Details", result.data.getResult().getName());
    }

    @Test
    public void getAutoCompletePlacesReturnsExpectedValues() throws InterruptedException {
        LiveData<Resource<AutoComplete>> valueAwaited = viewModel.getAutoCompletePlaces("test_input");
        Resource<AutoComplete> result = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

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

        LiveData<Map<String, String>> valueAwaited = viewModel.getOpeningHours(placeIds);
        Map<String, String> openingHours = LiveDataTestUtils.getOrAwaitValue(valueAwaited);

        assertNotNull("Opening hours should not be null", openingHours);
        assertFalse("Opening hours should not be empty", openingHours.isEmpty());
        assertEquals("Should have the correct opening hours for placeId1", "Monday: 9 AM - 5 PM", openingHours.get("placeId1"));
        assertEquals("Should have the correct opening hours for placeId2", "Wednesday: 8 AM - 4 PM", openingHours.get("placeId2"));
    }

    @Test
    public void getNearbyPlacesReturnsError() throws InterruptedException {
        LiveData<Resource<PlaceNearbySearch>> result = viewModel.getNearbyPlaces("mockLocation", 1000, "restaurant", "mockToken");
        ((MutableLiveData<Resource<PlaceNearbySearch>>) result).postValue(Resource.error("Failed to fetch nearby places due to network error", null));
        Resource<PlaceNearbySearch> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertEquals("Failed to fetch nearby places due to network error", response.message);
    }

    @Test
    public void getCombinedNearbyPlacesReturnsError() throws InterruptedException {
        LiveData<Resource<List<PlaceNearbySearch>>> result = viewModel.getStoredNearbyPlaces();
        ((MutableLiveData<Resource<List<PlaceNearbySearch>>>) result).postValue(Resource.error("Failed due to server error", null));
        Resource<List<PlaceNearbySearch>> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertEquals("Failed due to server error", response.message);
    }

    @Test
    public void getPlaceDetailsReturnsError() throws InterruptedException {
        LiveData<Resource<PlaceDetails>> result = viewModel.getPlaceDetails("invalid_place_id");
        ((MutableLiveData<Resource<PlaceDetails>>) result).postValue(Resource.error("Error fetching details", null));
        Resource<PlaceDetails> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertEquals("Error fetching details", response.message);
    }

    @Test
    public void getAutoCompletePlacesReturnsError() throws InterruptedException {
        LiveData<Resource<AutoComplete>> result = viewModel.getAutoCompletePlaces("invalid_input");
        ((MutableLiveData<Resource<AutoComplete>>) result).postValue(Resource.error("Permission denied", null));
        Resource<AutoComplete> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull(response);
        assertEquals(Resource.Status.ERROR, response.status);
        assertNull(response.data);
        assertEquals("Permission denied", response.message);
    }

    @Test
    public void getOpeningHoursReturnsError() throws InterruptedException {
        List<String> placeIds = Arrays.asList("invalid_place_id1", "invalid_place_id2");

        LiveData<Map<String, String>> result = viewModel.getOpeningHours(placeIds);
        Map<String, String> response = LiveDataTestUtils.getOrAwaitValue(result);

        assertNotNull("The response should not be null", response);
        assertTrue("The map should be empty, indicating that all details fetches failed", response.isEmpty());
    }
}
