package com.dcac.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

;import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.ViewHolder> {

    private List<User> mUsers;

    public WorkmatesListAdapter (List<User> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public WorkmatesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesListAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        Context context = holder.profileImageView.getContext();

        if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
            Glide.with(context)
                    .load(user.getUrlPicture())
                    .placeholder(R.drawable.workmates_list_profile) // Image par défaut pendant le chargement
                    .error(R.drawable.workmates_list_profile) // Image à afficher en cas d'erreur de chargement
                    .into(holder.profileImageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.workmates_list_profile)
                    .into(holder.profileImageView);
        }

//        holder.nameTextView.setText(user.getUserName() + R.string.scarlett_is_eating_french);

        String formattedText = context.getString(R.string.scarlett_is_eating_french, user.getUserName());
        holder.nameTextView.setText(formattedText);

        holder.descriptionTextView.setText(R.string.le_zinc);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void setUsers(List<User> users) {
        this.mUsers = users;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImageView;
        public TextView nameTextView;
        public TextView descriptionTextView;

        public ViewHolder(@NonNull View view) {
            super(view);
            profileImageView = view.findViewById(R.id.profile_image);
            nameTextView = view.findViewById(R.id.name_text_view);
            descriptionTextView = view.findViewById(R.id.description_text_view);
        }
    }
}
