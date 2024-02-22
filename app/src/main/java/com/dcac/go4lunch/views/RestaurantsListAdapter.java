package com.dcac.go4lunch.views;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.Locale;

public class RestaurantsListAdapter extends ListAdapter<Results, RestaurantsListAdapter.RestaurantsListViewHolder> {

    //List.Adapter in replacement of RecyclerView.Adapter. it is more performant

    private Location userLocation;
    private Context context;

    public RestaurantsListAdapter(Context context, Location userLocation) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.userLocation = userLocation;
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
        return new RestaurantsListViewHolder(itemBinding, userLocation, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantsListViewHolder holder, int position) {
        Results restaurant = getItem(position);
        holder.bind(restaurant);
    }

    public void setUserLocation(Location newUserLocation) {
        this.userLocation = newUserLocation;
        notifyDataSetChanged();
    }

    static class RestaurantsListViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantListBinding binding;
        private Location userLocation;
        private String apiKey;

        public RestaurantsListViewHolder(ItemRestaurantListBinding binding, Location userLocation, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.userLocation = userLocation;
            this.apiKey = ApiKeyUtil.getApiKey(context);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, RestaurantActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(Results results) {
            binding.restaurantName.setText(results.getName());
            binding.restaurantAddress.setText(results.getVicinity());
            //binding.restaurantOpeningHours.setText((CharSequence) results.getOpening_hours());

            // MODIFY THE NUMBER OF WORKERS WHEN OTHER FUNCTIONNALITY ARE IMPLEMENTED
            binding.restaurantWorkers.setText("3");


            if (results.getPhotos() != null && !results.getPhotos().isEmpty()) {
                String photoReference = results.getPhotos().get(0).getPhotoReference();
                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
                Glide.with(itemView.getContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.restaurant_list_image)
                        .error(R.drawable.restaurant_list_image)
                        .into(binding.restaurantImageView);
            } else {
                // Default image
                Glide.with(itemView.getContext())
                        .load(R.drawable.restaurant_list_image)
                        .into(binding.restaurantImageView);
            }

            if (userLocation != null) {
                // Distance calculation
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
            int numberOfStars = (int) (rating / 1.66);
            binding.restaurantStar.setImageResource(numberOfStars >= 1 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);
            binding.restaurantStar2.setImageResource(numberOfStars >= 2 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);
            binding.restaurantStar3.setImageResource(numberOfStars == 3 ? R.drawable.restaurant_list_star : R.drawable.restaurant_list_star_empty);

        }

    }
}
