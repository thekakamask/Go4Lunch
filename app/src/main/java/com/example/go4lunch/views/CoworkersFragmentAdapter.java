package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;

public class CoworkersFragmentAdapter extends FirestoreRecyclerAdapter<User, CoworkersViewHolder> {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.coworker_name)
    TextView mCoworkerName;

    private final RequestManager glide;

    public CoworkersFragmentAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide ) {
        super(options);
        this.glide = glide;

    }

    @Override
    protected void onBindViewHolder(@NonNull CoworkersViewHolder coworkersViewHolder, int position, @NonNull User model) {
        coworkersViewHolder.updateWithDetails(model, this.glide);
    }

    @NonNull
    @Override
    public CoworkersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.fragment_coworkers_item, parent, false);
            return new CoworkersViewHolder(view);
    }
}
