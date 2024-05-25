package com.dcac.go4lunch.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityParametersBinding;
import com.dcac.go4lunch.models.ParametersItem;
import com.dcac.go4lunch.views.ParametersActivityAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ParametersActivity extends BaseActivity<ActivityParametersBinding> {

    protected ActivityParametersBinding getViewBinding() {
        return ActivityParametersBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupExpandableListView();
    }


    private void setupExpandableListView() {
        List<String> listGroupTitles = Collections.singletonList("Notifications");
        HashMap<String, List<ParametersItem>> listChildData = new HashMap<>();

        // Recover actual state of notifications option from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = preferences.getBoolean("NotificationsEnabled", true);

        listChildData.put(getString(R.string.notifications), Collections.singletonList(new ParametersItem(getString(R.string.lunch_notifications), getString(R.string.notifications_description), notificationsEnabled)));


        ParametersActivityAdapter adapter = new ParametersActivityAdapter(this, listGroupTitles, listChildData);
        binding.expandableListView.setAdapter(adapter);
    }

}