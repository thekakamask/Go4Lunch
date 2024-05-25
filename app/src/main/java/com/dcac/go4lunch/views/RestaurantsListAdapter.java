package com.dcac.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ItemRestaurantListBinding;
import com.dcac.go4lunch.models.apiGoogleMap.placeNearbySearch.Results;
import com.dcac.go4lunch.ui.RestaurantActivity;
import com.dcac.go4lunch.utils.ApiKeyUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestaurantsListAdapter extends ListAdapter<Results, RestaurantsListAdapter.RestaurantsListViewHolder> {

    //List.Adapter in replacement of RecyclerView.Adapter. it is more performant

    private Location userLocation;
    private final Context context;
    private Map<String, Integer> userChoices = new HashMap<>();
    private Map<String, String> openingHours = new HashMap<>();

    public RestaurantsListAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<Results> DIFF_CALLBACK = new DiffUtil.ItemCallback<Results>() {
        @Override
        public boolean areItemsTheSame(@NonNull Results oldItem, @NonNull Results newItem) {
            return oldItem.getPlace_id().equals(newItem.getPlace_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Results oldItem, @NonNull Results newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    @NonNull
    @Override
    public RestaurantsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRestaurantListBinding itemBinding = ItemRestaurantListBinding.inflate(layoutInflater, parent, false);
        return new RestaurantsListViewHolder(itemBinding, context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Results> newRestaurants, Location newUserLocation, Map<String, Integer> userChoices) {
        this.userLocation = newUserLocation;
        this.userChoices = userChoices;
        submitList(newRestaurants);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsListViewHolder holder, int position) {
        Results restaurant = getItem(position);
        holder.bind(restaurant, userLocation);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOpeningHours(Map<String, String> openingHours) {
        this.openingHours = openingHours;
        notifyDataSetChanged();
    }

    public class RestaurantsListViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantListBinding binding;
        private final String apiKey;

        public RestaurantsListViewHolder(ItemRestaurantListBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.apiKey = ApiKeyUtil.getApiKey(context);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Results restaurant = getItem(position);
                    Log.d("RestaurantSelected", "Place ID: " + restaurant.getPlace_id());
                    Intent intent = new Intent(context, RestaurantActivity.class);
                    intent.putExtra(RestaurantActivity.EXTRA_PLACE_ID, restaurant.getPlace_id());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Results results, Location userLocation) {
            binding.restaurantName.setText(results.getName());
            binding.restaurantAddress.setText(results.getVicinity());

            String hours = openingHours.get(results.getPlace_id());
            binding.restaurantOpeningHours.setText(hours != null ? hours : context.getString(R.string.restaurant_hours_unknown));

            Integer count = userChoices.containsKey(results.getPlace_id()) ? userChoices.get(results.getPlace_id()) : Integer.valueOf(0);
            binding.restaurantWorkers.setText(String.format(Locale.getDefault(), "%d", count));

            if (results.getPhotos() != null && !results.getPhotos().isEmpty()) {
                String photoReference = results.getPhotos().get(0).getPhotoReference();
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
                Glide.with(itemView.getContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.restaurant_list_image)
                        .error(R.drawable.restaurant_list_image)
                        .into(binding.restaurantImageView);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.restaurant_list_image)
                        .into(binding.restaurantImageView);
            }

            if (userLocation != null) {
                Location restaurantLocation = new Location("");
                restaurantLocation.setLatitude(results.getGeometry().getLocation().getLat());
                restaurantLocation.setLongitude(results.getGeometry().getLocation().getLng());
                float distanceInMeters = userLocation.distanceTo(restaurantLocation);
                String distance = String.format(Locale.getDefault(), "%.2f km", distanceInMeters / 1000);
                binding.restaurantDistance.setText(distance);
            } else {
                binding.restaurantDistance.setText(R.string.restaurant_distance_unknown);
            }

            setupRatingStars(results.getRating());
        }

        private void setupRatingStars(double rating) {
            int numberOfStars;
            if (rating >= 4.0) {
                numberOfStars = 3;
            } else if (rating >= 3.0) {
                numberOfStars = 2;
            } else if (rating >= 2.0) {
                numberOfStars = 1;
            } else {
                numberOfStars = 0; // No stars if rating is less than 2.0
            }

            binding.restaurantStar.setImageResource(numberOfStars >= 1 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);
            binding.restaurantStar2.setImageResource(numberOfStars >= 2 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);
            binding.restaurantStar3.setImageResource(numberOfStars == 3 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);
        }
    }
}