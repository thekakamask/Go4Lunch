package com.dcac.go4lunch.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ItemRestaurantActivityBinding;
import com.dcac.go4lunch.models.user.User;

public class RestaurantActivityAdapter  extends ListAdapter<User, RestaurantActivityAdapter.RestaurantActivityViewHolder> {

    public RestaurantActivityAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }
    };

    @NonNull
    @Override
    public RestaurantActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRestaurantActivityBinding itemBinding = ItemRestaurantActivityBinding.inflate(layoutInflater, parent,false);
        return new RestaurantActivityViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantActivityAdapter.RestaurantActivityViewHolder holder, int position) {
        User user=getItem(position);
        holder.bind(user);
    }

    static class RestaurantActivityViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantActivityBinding binding;

        public RestaurantActivityViewHolder(ItemRestaurantActivityBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(User user) {


            if (user.getUrlPicture() != null && !user.getUrlPicture().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getUrlPicture())
                        .placeholder(R.drawable.workmates_list_profile)
                        .error(R.drawable.workmates_list_profile)
                        .into(binding.profileImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.workmates_list_profile)
                        .into(binding.profileImage);
            }

            binding.nameTextView.setText(user.getUserName());
            binding.descriptionTextView.setText(R.string.is_joining);
        }
    }
}
