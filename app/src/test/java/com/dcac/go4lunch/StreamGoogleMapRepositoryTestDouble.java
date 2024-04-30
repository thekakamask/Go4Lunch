package com.dcac.go4lunch;

import androidx.lifecycle.LiveData;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.utils.Resource;

import java.util.List;

public class StreamGoogleMapRepositoryTestDouble implements IStreamGoogleMap {


    @Override
    public LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type, String pageToken) {
        return null; // DOIT RETOURNER LES VALEURS DE TEST
    }

    @Override
    public LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types) {
        return null;
    }

    @Override
    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId, String language) {
        return null;
    }

    @Override
    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        return null;
    }
}
