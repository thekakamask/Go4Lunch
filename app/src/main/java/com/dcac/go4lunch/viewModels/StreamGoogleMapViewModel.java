package com.dcac.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.repository.interfaceRepository.IStreamGoogleMap;
import com.dcac.go4lunch.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamGoogleMapViewModel extends ViewModel {


    private final IStreamGoogleMap mapRepository;
    private final Map<LiveData<?>, Observer<?>> observers = new HashMap<>();
    private Map<String, String> cachedOpeningHours= new HashMap<>();

    //private MutableLiveData<Resource<List<PlaceNearbySearch>>> combinedNearbyPlacesLiveData;

    private MutableLiveData<Resource<List<PlaceNearbySearch>>> nearbyPlacesLiveData = new MutableLiveData<>();


    public StreamGoogleMapViewModel(IStreamGoogleMap mapRepository) {
        this.mapRepository = mapRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        for (Map.Entry<LiveData<?>, Observer<?>> entry : observers.entrySet()) {
            LiveData<?> liveData = entry.getKey();
            Observer<?> observer = entry.getValue();
            // Cast the observer to Observer<Object> to match the removeObserver method signature
            liveData.removeObserver((Observer<Object>) observer);
        }
        observers.clear();
    }

    private <T> void observeForever(LiveData<Resource<T>> liveData, MutableLiveData<Resource<T>> liveDataMutable) {
        Observer<Resource<T>> observer = resource -> {
            if (resource != null) {
                if (resource.status == Resource.Status.SUCCESS) {
                    liveDataMutable.setValue(Resource.success(resource.data));
                } else if (resource.status == Resource.Status.ERROR) {
                    liveDataMutable.setValue(Resource.error(resource.message, resource.data));
                }
            } else {
                liveDataMutable.setValue(Resource.error("Data is null", null));
            }
        };
        liveData.observeForever(observer);
        observers.put(liveData, observer);
    }

    public LiveData<Resource<PlaceNearbySearch>> getNearbyPlaces(String location, int radius, String type, String pageToken) {
        MutableLiveData<Resource<PlaceNearbySearch>> liveData = new MutableLiveData<>();
        observeForever(mapRepository.getNearbyPlaces(location, radius, type, pageToken), liveData);
        return liveData;
    }

    public void fetchAndStoreNearbyPlaces(String location, int radius, List<String> types) {
        mapRepository.getCombinedNearbyPlaces(location, radius, types).observeForever(resource -> {
            nearbyPlacesLiveData.setValue(resource);
        });
    }

    public LiveData<Resource<List<PlaceNearbySearch>>> getStoredNearbyPlaces() {
        return nearbyPlacesLiveData;
    }

    /*public LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types) {
        // INIT LIVE DATE IF IT DOESNT EXIST
        if (combinedNearbyPlacesLiveData == null) {
            combinedNearbyPlacesLiveData = new MutableLiveData<>();
            // LOAD DATA FROM API
            loadCombinedNearbyPlaces(location, radius, types);
        }
        return combinedNearbyPlacesLiveData;
    }

    private void loadCombinedNearbyPlaces(String location, int radius, List<String> types) {
        mapRepository.getCombinedNearbyPlaces(location, radius, types).observeForever(resource -> {
            // UPDATE LIVE DATA WITH RESULT
            combinedNearbyPlacesLiveData.setValue(resource);
        });
    }*/

    // FORCE THE DATA RECHARGE
    /*public void refreshCombinedNearbyPlaces(String location, int radius, List<String> types) {
        loadCombinedNearbyPlaces(location, radius, types);
    }*/


    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId) {
        String language = Locale.getDefault().getLanguage(); // Obtain language of the system
        MutableLiveData<Resource<PlaceDetails>> liveData = new MutableLiveData<>();
        observeForever(mapRepository.getPlaceDetails(placeId, language), liveData);
        return liveData;
    }

    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        MutableLiveData<Resource<AutoComplete>> liveData = new MutableLiveData<>();
        observeForever(mapRepository.getAutoCompletePlaces(input), liveData);
        return liveData;
    }

    public LiveData<Map<String, String>> getOpeningHours(List<String> placeIds) {
        MediatorLiveData<Map<String, String>> openingHoursLiveData = new MediatorLiveData<>();

        AtomicInteger count = new AtomicInteger(placeIds.size());
        for (String placeId : placeIds) {
            if (cachedOpeningHours.containsKey(placeId)) {
                // Hours already charge, use them directly
                count.decrementAndGet();
                continue;
            }

            LiveData<Resource<PlaceDetails>> placeDetailsLiveData = mapRepository.getPlaceDetails(placeId, Locale.getDefault().getLanguage());
            openingHoursLiveData.addSource(placeDetailsLiveData, resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS) {
                    PlaceDetails details = resource.data;
                    if (details != null && details.getResult().getOpening_hours() != null) {
                        List<String> openingHoursList = details.getResult().getOpening_hours().getWeekday_text();
                        if (!openingHoursList.isEmpty()) {
                            cachedOpeningHours.put(placeId, openingHoursList.get(0));
                        }
                    }
                }
                if (count.decrementAndGet() == 0) {
                    openingHoursLiveData.setValue(cachedOpeningHours);
                }
                openingHoursLiveData.removeSource(placeDetailsLiveData);
            });
        }

        if (count.get() == 0) {
            // Hours already in cache
            openingHoursLiveData.setValue(cachedOpeningHours);
        }

        return openingHoursLiveData;
    }





}
