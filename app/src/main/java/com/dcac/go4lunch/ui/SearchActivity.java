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

public class SearchActivity extends BaseActivity<ActivitySearchBinding> {

    private StreamGoogleMapViewModel streamGoogleMapViewModel;
    private SearchActivityAdapter adapter;

    private static final long SEARCH_DELAY_MS = 300;
    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                // Cancel previous pending search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                if (query.length() < 3) {
                    adapter.updateData(new ArrayList<>());
                    return;
                }

                searchRunnable = () -> fetchAutocompleteResults(query);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }

            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchAutocompleteResults(String query) {
        streamGoogleMapViewModel
                .getAutoCompletePlaces(query)
                .observe(this, autoCompleteResource -> {

                    if (autoCompleteResource == null) return;

                    if (autoCompleteResource.status == Resource.Status.SUCCESS
                            && autoCompleteResource.data != null
                            && autoCompleteResource.data.getPredictions() != null) {

                        adapter.updateData(autoCompleteResource.data.getPredictions());

                    } else if (autoCompleteResource.status == Resource.Status.ERROR) {
                        showError(autoCompleteResource.message);
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