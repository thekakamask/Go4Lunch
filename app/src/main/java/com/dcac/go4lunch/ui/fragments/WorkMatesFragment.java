package com.dcac.go4lunch.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dcac.go4lunch.databinding.FragmentWorkMatesBinding;
import com.dcac.go4lunch.viewModels.UserViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkMatesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkMatesFragment extends Fragment {

    private FragmentWorkMatesBinding binding;


    /*private static final String KEY_POSITION="position";
    private static final String KEY_COLOR="color";*/
    public static WorkMatesFragment newInstance() {

        /*Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        WorkMatesFragment.setArguments(args);*/

        return new WorkMatesFragment();
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
        /*if (getArguments() != null) {
            int position = getArguments().getInt(KEY_POSITION, -1);
            int color = getArguments().getInt(KEY_COLOR, -1);

            binding.workmatesLayout.setBackgroundColor(color);
            binding.workmatesTitle.setText("List of workmates. Page number "+ position);
        }*/


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.workmatesTitle.setText("List of workmates");
    }
}