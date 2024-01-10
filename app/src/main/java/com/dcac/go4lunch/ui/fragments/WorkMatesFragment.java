package com.dcac.go4lunch.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.FragmentRestaurantsMapBinding;
import com.dcac.go4lunch.databinding.FragmentWorkMatesBinding;
import com.dcac.go4lunch.viewModels.UserManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkMatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkMatesFragment extends Fragment {

    private FragmentWorkMatesBinding binding;

    private UserManager userManager = UserManager.getInstance();

    private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";
    public static WorkMatesFragment newInstance(int position, int color) {
        WorkMatesFragment WorkMatesFragment = new WorkMatesFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        args.putInt(KEY_COLOR, color);
        WorkMatesFragment.setArguments(args);

        return(WorkMatesFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkMatesBinding.inflate(inflater, container, false);

        // Retrieve and use data from Bundle
        if (getArguments() != null) {
            int position = getArguments().getInt(KEY_POSITION, -1);
            int color = getArguments().getInt(KEY_COLOR, -1);

            binding.workmatesLayout.setBackgroundColor(color);
            binding.workmatesTitle.setText("List of workmates. Page number "+ position);
        }

        return binding.getRoot();
    }
}