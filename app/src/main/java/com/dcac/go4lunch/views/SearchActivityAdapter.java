package com.dcac.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.apiGoogleMap.autoCompleteAPI.Predictions;

import java.util.List;

public class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityAdapter.ViewHolder> {

    private List<Predictions> predictionsList;
    private final LayoutInflater mInflater;
    private final OnPredictionClickListener clickListener;

    public SearchActivityAdapter(Context context, List<Predictions> data, OnPredictionClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.predictionsList = data;
        this.clickListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Predictions> newPredictions) {
        predictionsList = newPredictions;
        notifyDataSetChanged();
    }

    public interface OnPredictionClickListener {
        void onPredictionClick(Predictions prediction);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Predictions prediction = predictionsList.get(position);
        holder.restaurantName.setText(prediction.getDescription());
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onPredictionClick(prediction);
            }
        });

    }

    @Override
    public int getItemCount() {
        return predictionsList == null ? 0 : predictionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurantName);
        }
    }
}