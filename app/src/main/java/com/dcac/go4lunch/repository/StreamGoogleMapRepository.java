package com.dcac.go4lunch.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;


import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.utils.Resource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class StreamGoogleMapRepository implements IStreamGoogleMap {
    private final ServiceGoogleMap service;
    private static StreamGoogleMapRepository instance;

    private Disposable disposable;

    public static synchronized StreamGoogleMapRepository getInstance(Context context) {
        if (instance == null) {
            instance = new StreamGoogleMapRepository(context);
        }
        return instance;
    }

    private StreamGoogleMapRepository(Context context) {
        this.service = RetrofitObjectBuilder.getRetrofitInstance("https://maps.googleapis.com/maps/api/", context)
                .create(ServiceGoogleMap.class);
    }

    @Override
    public LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type, String pageToken) {
        Log.d("Repository", "Preparing to fetch places of type: " + type + " with API Key.");
        return LiveDataReactiveStreams.fromPublisher(
                service.getNearbyPlaces(location, radius, type, pageToken, null)
                        .doOnSubscribe(disposable -> Log.d("Repository", "Subscribing to fetch nearby places."))
                        .doOnNext(placeNearbySearch -> Log.d("Repository", "Received response: " + new Gson().toJson(placeNearbySearch)))
                        .doOnError(throwable -> Log.e("Repository", "Error fetching nearby places: " + throwable.getMessage(), throwable))
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> {
                            PlaceNearbySearch errorResponse = new PlaceNearbySearch();
                            errorResponse.setStatus("ERROR");
                            errorResponse.setResults(null);
                            return errorResponse;
                        })
                        .map(response -> {
                            if ("ERROR".equals(response.getStatus())) {
                                Log.e("Repository", "API returned an error for nearby places: " + response.getStatus());
                                return Resource.error("Error fetching nearby places", response);
                            } else {
                                return Resource.success(response);
                            }
                        })
        );
    }

    @Override
    public LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types) {
        MutableLiveData<Resource<List<PlaceNearbySearch>>> liveData = new MutableLiveData<>();

        // Store combined results in list
        List<PlaceNearbySearch> combinedResults = new ArrayList<>();

        // Counter to indicate when all calls have been completed
        AtomicInteger callsRemaining = new AtomicInteger(types.size());

        for (String type : types) {
            fetchPlacesPage(location, radius, type, null, combinedResults, liveData, callsRemaining);
        }

        return liveData;
    }

    @SuppressLint("CheckResult")
    private void fetchPlacesPage(String location, int radius, String type, String pageToken, List<PlaceNearbySearch> combinedResults, MutableLiveData<Resource<List<PlaceNearbySearch>>> liveData, AtomicInteger callsRemaining) {
        Log.d("PLACES_REPO", "Request => type=" + type
                + ", location=" + location
                + ", radius=" + radius
                + ", pageToken=" + pageToken);
        disposable = service.getNearbyPlaces(location, radius, type, pageToken, null)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        placeNearbySearch -> {
                            Log.d("PLACES_REPO", "Response status=" + placeNearbySearch.getStatus()
                                    + ", results="
                                    + (placeNearbySearch.getResults() != null
                                    ? placeNearbySearch.getResults().size()
                                    : "null"));
                            synchronized (combinedResults) {
                                if (!"OK".equals(placeNearbySearch.getStatus())
                                        && !"ZERO_RESULTS".equals(placeNearbySearch.getStatus())) {

                                    Log.e("PLACES_REPO", "Google Places ERROR => " + placeNearbySearch.getStatus());
                                }
                                combinedResults.add(placeNearbySearch);
                            }
                            if (placeNearbySearch.getNextPageToken() != null) {
                                try {
                                    Thread.sleep(2000); // wait 2 sec
                                    fetchPlacesPage(location, radius, type, placeNearbySearch.getNextPageToken(), combinedResults, liveData, callsRemaining);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    Log.e("Repository", "Thread interrupted while waiting for the next page token", e);
                                }
                            } else if (callsRemaining.decrementAndGet() == 0) {
                                Log.d("PLACES_REPO", "FINAL RESULT => total responses = " + combinedResults.size());

                                for (int i = 0; i < combinedResults.size(); i++) {
                                    PlaceNearbySearch p = combinedResults.get(i);
                                    Log.d("PLACES_REPO", "Response[" + i + "] status=" + p.getStatus()
                                            + ", results="
                                            + (p.getResults() != null ? p.getResults().size() : "null"));
                                }
                                liveData.postValue(Resource.success(combinedResults));
                                disposeFetchPlacesPage(); // Disposable when all operation are termianted
                            }
                        },
                        throwable -> {
                            Log.e("PLACES_REPO", "onError() called", throwable);
                            liveData.postValue(Resource.error(throwable.getMessage(), combinedResults));
                            disposeFetchPlacesPage(); // Disposable in cas of mistake
                        }
                );
    }

    private void disposeFetchPlacesPage() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId, String language) {
        return LiveDataReactiveStreams.fromPublisher(
                service.getPlaceDetails(placeId, language, null)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturn(throwable -> {
                            PlaceDetails errorResponse = new PlaceDetails();
                            errorResponse.setStatus("ERROR");
                            return errorResponse;
                        })
                        .map(response -> {
                            if ("ERROR".equals(response.getStatus())) {
                                return Resource.error("Error fetching place details", response);
                            } else {
                                return Resource.success(response);
                            }
                        })
        );
    }

    @Override
    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        return LiveDataReactiveStreams.fromPublisher(
                service.getAutocompletePlaces(input,"establishment", null)
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
