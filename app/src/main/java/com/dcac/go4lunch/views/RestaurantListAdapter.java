package com.dcac.go4lunch.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcac.go4lunch.databinding.ItemRestaurantListBinding;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantListViewHolder> {

    //List.Adapter in replacement of RecyclerView.Adapter. it is more performant
    private List<PlaceDetails> restaurantList;

    public RestaurantListAdapter(List<PlaceDetails> restaurantList) {
        this.restaurantList=restaurantList;
    }

    @NonNull
    @Override
    public RestaurantListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemRestaurantListBinding itemBinding = ItemRestaurantListBinding.inflate(layoutInflater, parent, false);
        return new RestaurantListViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListViewHolder holder, int position) {
        PlaceDetails restaurant = restaurantList.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurantList != null ? restaurantList.size() : 0;
    }

    public void updateData(List<PlaceDetails> newRestaurantList) {
        this.restaurantList = newRestaurantList;
        notifyDataSetChanged();
    }

    static class RestaurantListViewHolder extends RecyclerView.ViewHolder {
        private final ItemRestaurantListBinding binding;

        public RestaurantListViewHolder(ItemRestaurantListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PlaceDetails placeDetails) {

            if (placeDetails.getDisplayName() != null) {
                binding.restaurantName.setText(placeDetails.getDisplayName().getText());
            }
            if (placeDetails.getFormattedAddress() != null) {
                binding.restaurantAddress.setText(placeDetails.getFormattedAddress());
            }
            if (placeDetails.getRegularOpeningHours()!= null) {
                binding.restaurantOpeningHours.setText(placeDetails.getRegularOpeningHours());
            }

            if(placeDetails.getLocation() != null) {
                binding.restaurantDistance.setText(placeDetails.getLocation());
            }

            if (placeDetails.getPhotos() != null){
                binding.restaurantImageView.setImageResource(placeDetails.getPhotos());
            }

            if(placeDetails.getRating() != null) {
                binding.restaurantStar.;
                binding.restaurantStar2.;
                binding.restaurantStar3.;
            }

            binding.restaurantWorkers.





        }
    }
}
