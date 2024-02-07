package com.dcac.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityRestaurantBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.viewModels.LocationViewModel;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.dcac.go4lunch.views.RestaurantActivityAdapter;

import java.util.ArrayList;

public class RestaurantActivity extends BaseActivity<ActivityRestaurantBinding> {

    RestaurantActivityAdapter adapter;
    UserViewModel userViewModel;
    StreamGoogleMapViewModel streamGoogleMapViewModel;
    @Override
    protected ActivityRestaurantBinding getViewBinding() {
        return ActivityRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this,factory).get(StreamGoogleMapViewModel.class);

        initializeViews();
        initRecyclerView();
    }

    private void initializeViews() {

    }

    private void initRecyclerView(){
        adapter= new RestaurantActivityAdapter();
        binding.recyclerViewWorkmates.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerViewWorkmates.setAdapter(adapter);
    }






}