package com.dcac.go4lunch.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.AutoComplete;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.PlaceNearbySearch;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.repository.StreamGoogleMapRepository;
import com.dcac.go4lunch.utils.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamGoogleMapViewModel extends ViewModel {

    private final StreamGoogleMapRepository mapRepository;
    private final Map<LiveData<?>, Observer<?>> observers = new HashMap<>();

    public StreamGoogleMapViewModel(StreamGoogleMapRepository mapRepository) {
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

    public LiveData<Resource<List<PlaceNearbySearch>>> getCombinedNearbyPlaces(String location, int radius, List<String> types) {
        return mapRepository.getCombinedNearbyPlaces(location, radius, types);
    }

    public LiveData<Resource<PlaceDetails>> getPlaceDetails(String placeId) {
        MutableLiveData<Resource<PlaceDetails>> liveData = new MutableLiveData<>();
        observeForever(mapRepository.getPlaceDetails(placeId), liveData);
        return liveData;
    }

    public LiveData<Resource<AutoComplete>> getAutoCompletePlaces(String input) {
        MutableLiveData<Resource<AutoComplete>> liveData = new MutableLiveData<>();
        observeForever(mapRepository.getAutoCompletePlaces(input), liveData);
        return liveData;
    }


}
