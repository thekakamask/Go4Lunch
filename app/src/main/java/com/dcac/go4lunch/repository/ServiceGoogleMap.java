package com.dcac.go4lunch.repository;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;

import io.reactivex.rxjava3.core.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceGoogleMap {
    // Place Nearby Search
    @GET("place/nearbysearch/json")
    Flowable<PlaceNearbySearch> getNearbyPlaces(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("pagetoken") String pageToken,
            @Query("key") String apiKey);

    // Place Details
    @GET("place/details/json")
    Flowable<PlaceDetails> getPlaceDetails(
            @Query("place_id") String placeId,
            @Query("language") String language,
            @Query("key") String apiKey);

    // Autocomplete
    @GET("place/autocomplete/json")
    Flowable<AutoComplete> getAutocompletePlaces(
            @Query("input") String input,
            @Query("types") String types,
            @Query("key") String apiKey);
}
