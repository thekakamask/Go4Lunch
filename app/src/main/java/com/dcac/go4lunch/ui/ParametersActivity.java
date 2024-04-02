package com.dcac.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.databinding.ActivityParametersBinding;
import com.dcac.go4lunch.models.ParametersItem;
import com.dcac.go4lunch.views.ParametersActivityAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ParametersActivity extends BaseActivity<ActivityParametersBinding> {

    private ParametersActivityAdapter adapter;

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

        listChildData.put(getString(R.string.notifications), Arrays.asList(new ParametersItem(getString(R.string.lunch_notifications), getString(R.string.notifications_description), notificationsEnabled)));

        /*listChildData.put("Groupe 2", Arrays.asList(new ParametersItem("Titre 2", "Description 2", false)));
        listChildData.put("Groupe 3", Arrays.asList(new ParametersItem("Titre 3", "Description 3", true)));*/

        adapter = new ParametersActivityAdapter(this, listGroupTitles, listChildData);
        binding.expandableListView.setAdapter(adapter);
    }

}