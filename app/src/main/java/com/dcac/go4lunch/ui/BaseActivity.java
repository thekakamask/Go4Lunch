package com.dcac.go4lunch.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity <T extends ViewBinding> extends AppCompatActivity {

    protected abstract T getViewBinding();
    protected T binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
    }

    private void initBinding(){
        binding = getViewBinding();
        View view = binding.getRoot();
        setContentView(view);
    }
}
