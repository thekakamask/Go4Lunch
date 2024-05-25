package com.dcac.go4lunch.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dcac.go4lunch.databinding.FragmentWorkMatesBinding;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.ui.RestaurantActivity;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.views.WorkmatesListAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkMatesListFragment extends Fragment {

    private FragmentWorkMatesBinding binding;
    private WorkmatesListAdapter adapter;

    private MainActivity mainActivity;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkMatesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new WorkmatesListAdapter(new ArrayList<>(), user -> {
            if (user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantId() != null) {
                Intent intent = new Intent(getContext(), RestaurantActivity.class);
                intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, user.getRestaurantChoice().getRestaurantId());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), user.getUserName() + " hasn't decided yet.", Toast.LENGTH_SHORT).show();
            }
        });
        binding.workmatesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.workmatesListRecyclerView.setAdapter(adapter);

        if (mainActivity != null) {
            mainActivity.getUserViewModel().getAllUsers().observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    List<User> users = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : resource.data.getDocuments()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            users.add(user);
                        }
                    }
                    adapter.setUsers(users);
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(getContext(), "Error fetching users: " + resource.message, Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}