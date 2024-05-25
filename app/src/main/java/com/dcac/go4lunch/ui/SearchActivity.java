package com.dcac.go4lunch.ui;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.dcac.go4lunch.databinding.ActivitySearchBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Predictions;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.views.SearchActivityAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity<ActivitySearchBinding> {

    private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private SearchActivityAdapter adapter;

    protected ActivitySearchBinding getViewBinding() {
        return ActivitySearchBinding.inflate(getLayoutInflater());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        streamGoogleMapViewModel = new ViewModelProvider(this, factory).get(StreamGoogleMapViewModel.class);

        initRecyclerView();
        setupSearch();
    }

    private void initRecyclerView() {
        adapter = new SearchActivityAdapter(this, new ArrayList<>(), this::handlePredictionClick);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.searchResultsRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() >= 3) {
                    fetchAutocompleteResults(charSequence.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void fetchAutocompleteResults(String query) {
        streamGoogleMapViewModel.getAutoCompletePlaces(query).observe(this, autoCompleteResource -> {
            if (autoCompleteResource != null && autoCompleteResource.status == Resource.Status.SUCCESS && autoCompleteResource.data != null) {
                // Filtered by restaurant
                List<Predictions> filteredPredictions = new ArrayList<>();
                for (Predictions prediction : autoCompleteResource.data.getPredictions()) {
                    if (prediction.getTypes().contains("restaurant")) {
                        filteredPredictions.add(prediction);
                    }
                }

                // Update with filtered predictions
                adapter.updateData(filteredPredictions);
            } else {
                assert autoCompleteResource != null;
                if (autoCompleteResource.status == Resource.Status.ERROR) {
                    showError(autoCompleteResource.message);
                }
            }
        });

    }

    private void handlePredictionClick(Predictions prediction) {
        navigateToRestaurantDetails(prediction.getPlace_id());
    }

    private void navigateToRestaurantDetails(String placeId) {
        Intent intent = new Intent(SearchActivity.this, RestaurantActivity.class);
        intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, placeId);
        startActivity(intent);
    }

    private void showError(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }


}