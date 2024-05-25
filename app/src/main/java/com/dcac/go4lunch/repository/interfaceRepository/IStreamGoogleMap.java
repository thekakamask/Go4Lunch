package com.dcac.go4lunch.repository.interfaceRepository;

import androidx.lifecycle.LiveData;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.utils.Resource;

import java.util.List;

public interface IStreamGoogleMap {

    LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type, String pageToken);

    LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types);

    LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId, String language);

    LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input);



}
