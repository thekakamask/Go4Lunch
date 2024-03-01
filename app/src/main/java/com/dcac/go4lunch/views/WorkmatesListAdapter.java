package com.dcac.go4lunch.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

;import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.user.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.ViewHolder> {

    private List<User> mUsers;
    private OnItemClickListener listener;

    public WorkmatesListAdapter (List<User> users, OnItemClickListener listener) {
        mUsers = users;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    @NonNull
    @Override
    public WorkmatesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workmates_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(mUsers.get(position));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesListAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        Context context = holder.itemView.getContext();

        Glide.with(context)
                .load(user.getUrlPicture())
                .placeholder(R.drawable.workmates_list_profile)
                .error(R.drawable.workmates_list_profile)
                .into(holder.profileImageView);


        String text;
        if (user.getRestaurantChoice() != null && user.getRestaurantChoice().getRestaurantName() != null && !user.getRestaurantChoice().getRestaurantName().isEmpty()) {
            text = user.getUserName() + " is eating at " + user.getRestaurantChoice().getRestaurantName();
        } else {
            text = user.getUserName() + " hasn't decided yet";
        }

        holder.nameTextView.setText(text);
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
