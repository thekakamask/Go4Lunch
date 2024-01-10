package com.dcac.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityMainBinding;
import com.dcac.go4lunch.views.ViewPagerAdapter;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.configureMainViewPager();
    }

    private void configureMainViewPager() {
        // 1 - Get ViewPager from layout
        ViewPager2 pager = binding.mainViewPager;
        // 2 - Set Adapter PageAdapter and glue it together
        FragmentStateAdapter adapter = new ViewPagerAdapter(this, getResources().getIntArray(R.array.colorPagesViewPager));
        pager.setAdapter(adapter);
        Log.d("ViewPager", "Adapter set");

    }


}