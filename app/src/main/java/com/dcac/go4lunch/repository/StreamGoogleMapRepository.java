package com.dcac.go4lunch.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.utils.Resource;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class StreamGoogleMapRepository {
    private final ServiceGoogleMap service;
    private static StreamGoogleMapRepository instance;

    public static synchronized StreamGoogleMapRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StreamGoogleMapRepository(context);
        }
        return instance;
    }

    private StreamGoogleMapRepository(Context context) {
        this.service = RetrofitObjectBuilder.getRetrofitInstance("https://maps.googleapis.com/", context)
                .create(ServiceGoogleMap.class);
    }

    public LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type) {
        return LiveDataReactiveStreams.fromPublisher(
                service.getNearbyPlaces(location, radius, type, null)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> {
                            // Create and return an Error Object
                            PlaceNearbySearch errorResponse = new PlaceNearbySearch();
                            errorResponse.setStatus("ERROR");
                            errorResponse.setResults(null);
                            return errorResponse;
                        })
                        .map(response -> {
                            // Transform response in Resource
                            if (response.getStatus().equals("ERROR")) {
                                return Resource.error("Error fetching nearby places", response);
                            } else {
                                return Resource.success(response);
                            }
                        })
        );
    }
    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId) {
        return LiveDataReactiveStreams.fromPublisher(
                service.getPlaceDetails(placeId, null)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> {
                            // Create and return an Error Object
                            PlaceDetails errorResponse = new PlaceDetails();
                            errorResponse.setStatus("ERROR");
                            return errorResponse;
                        })
                        .map(response -> {
                            // Transform response in Resource
                            if ("ERROR".equals(response.getStatus())) {
                                return Resource.error("Error fetching place details", response);
                            } else {
                                return Resource.success(response);
                            }
                        })
        );
    }

    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        return LiveDataReactiveStreams.fromPublisher(
                service.getAutocompletePlaces(input, null)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> {
                            // Create and return an Error Object
                            AutoComplete errorResponse = new AutoComplete();
                            errorResponse.setStatus("ERROR");
                            return errorResponse;
                        })
                        .map(response -> {
                            // Transform response in Resource
                            if ("ERROR".equals(response.getStatus())) {
                                return Resource.error("Error fetching autocomplete data", response);
                            } else {
                                return Resource.success(response);
                            }
                        })
        );
    }
}
