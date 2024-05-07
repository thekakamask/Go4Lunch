package com.dcac.go4lunch.testDouble;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Predictions;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Structured_formatting;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.Opening_hours;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.Result;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.utils.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StreamGoogleMapRepositoryTestDouble implements IStreamGoogleMap {


    @Override
    public LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type, String pageToken) {
        MutableLiveData<Resource<PlaceNearbySearch>> liveData = new MutableLiveData<>();
        List<Results> resultsList = new ArrayList<>();
        Results results = new Results();
        results.setName("Test Place");
        results.setVicinity("Test Location");
        resultsList.add(results);

        PlaceNearbySearch place = new PlaceNearbySearch();
        place.setResults(resultsList);
        place.setStatus("OK");

        Resource<PlaceNearbySearch> resource = Resource.success(place);
        liveData.setValue(resource);
        return liveData;
    }

    @Override
    public LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types) {
        MutableLiveData<Resource<List<PlaceNearbySearch>>> liveData = new MutableLiveData<>();
        List<PlaceNearbySearch> placeList = new ArrayList<>();
        for (String type : types) {
            List<Results> resultsList = new ArrayList<>();

            // Create first result with specified type
            Results result1 = new Results();
            result1.setName("Test Place 1 for type: " + type);
            result1.setVicinity("Test Location 1");
            resultsList.add(result1);

            Results result2 = new Results();
            result2.setName("Test Place 2 for type: " + type);
            result2.setVicinity("Test Location 2");
            resultsList.add(result2);

            PlaceNearbySearch place = new PlaceNearbySearch();
            place.setResults(resultsList);
            place.setStatus("OK");
            placeList.add(place);
        }
        Resource<List<PlaceNearbySearch>> resource = Resource.success(placeList);
        liveData.setValue(resource);
        return liveData;
    }

    @Override
    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId, String language) {
        MutableLiveData<Resource<PlaceDetails>> liveData = new MutableLiveData<>();
        if ("invalid_place_id1".equals(placeId) || "invalid_place_id2".equals(placeId)) {
            liveData.setValue(Resource.error("Details not found for the place", null));
            return liveData;
        }

        PlaceDetails details = new PlaceDetails();
        Result result = new Result();
        result.setName("Test Place Details");

        // Define opening hours in function of Place Id
        Opening_hours openingHours = new Opening_hours();
        if ("placeId1".equals(placeId)) {
            openingHours.setWeekday_text(Arrays.asList("Monday: 9 AM - 5 PM", "Tuesday: 10 AM - 6 PM"));
        } else if ("placeId2".equals(placeId)) {
            openingHours.setWeekday_text(Arrays.asList("Wednesday: 8 AM - 4 PM", "Thursday: 11 AM - 7 PM"));
        }

        result.setOpening_hours(openingHours);
        details.setResult(result);
        details.setStatus("OK");

        Resource<PlaceDetails> resource = Resource.success(details);
        liveData.setValue(resource);
        return liveData;
    }

    @Override
    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        MutableLiveData<Resource<AutoComplete>> liveData = new MutableLiveData<>();
        AutoComplete autoComplete = new AutoComplete();


        Predictions prediction = new Predictions();
        prediction.setDescription("Test Description for input: " + input);
        prediction.setPlace_id("test_place_id");
        prediction.setReference("test_reference");
        prediction.setTypes(Collections.singletonList("restaurant"));


        Structured_formatting structuredFormatting = new Structured_formatting();
        structuredFormatting.setMain_text("Main Text Example");
        structuredFormatting.setSecondary_text("Secondary Text Example");
        prediction.setStructured_formatting(structuredFormatting);


        List<Predictions> predictionsList = new ArrayList<>();
        predictionsList.add(prediction);
        autoComplete.setPredictions(predictionsList);
        autoComplete.setStatus("OK");

        Resource<AutoComplete> resource = Resource.success(autoComplete);
        liveData.setValue(resource);
        return liveData;
    }


}
