package com.dcac.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.databinding.ActivityParametersBinding;
import com.dcac.go4lunch.models.ParametersItem;
import com.dcac.go4lunch.views.ParametersActivityAdapter;

import java.util.Arrays;
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
        List<String> listGroupTitles = Arrays.asList("Groupe 1", "Groupe 2", "Groupe 3");
        HashMap<String, List<ParametersItem>> listChildData = new HashMap<>();
        listChildData.put("Groupe 1", Arrays.asList(new ParametersItem("Titre 1", "Description 1", true)));
        listChildData.put("Groupe 2", Arrays.asList(new ParametersItem("Titre 2", "Description 2", false)));
        listChildData.put("Groupe 3", Arrays.asList(new ParametersItem("Titre 3", "Description 3", true)));

        adapter = new ParametersActivityAdapter(this, listGroupTitles, listChildData);
        binding.expandableListView.setAdapter(adapter);
    }

}