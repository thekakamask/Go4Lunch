package com.dcac.go4lunch.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dcac.go4lunch.databinding.FragmentWorkMatesBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.ui.MainActivity;
import com.dcac.go4lunch.ui.RestaurantActivity;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.dcac.go4lunch.views.WorkmatesListAdapter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkMatesListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkMatesListFragment extends Fragment {

    private FragmentWorkMatesBinding binding;
    private WorkmatesListAdapter adapter;

    private MainActivity mainActivity;

    public static WorkMatesListFragment newInstance() {
        return new WorkMatesListFragment();
    }


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
                    List<User> usersWithChoice = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : resource.data.getDocuments()) { // Assume resource.data is a QuerySnapshot
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantName() != null && !user.getRestaurantChoice().getRestaurantName().isEmpty()) {
                            usersWithChoice.add(user);
                        }
                    }
                    adapter.setUsers(usersWithChoice);
                } else if (resource.status == Resource.Status.ERROR) {
                    Toast.makeText(getContext(), "Error fetching users: " + resource.message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}