package com.dcac.go4lunch.ui;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityRestaurantBinding;
import com.dcac.go4lunch.injection.ViewModelFactory;
import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.PlaceDetails;
import com.dcac.go4lunch.models.apiGoogleMap.placedetailsAPI.Result;
import com.dcac.go4lunch.utils.ApiKeyUtil;
import com.dcac.go4lunch.utils.Resource;
import com.dcac.go4lunch.viewModels.StreamGoogleMapViewModel;
import com.dcac.go4lunch.viewModels.UserViewModel;
import com.dcac.go4lunch.views.RestaurantActivityAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RestaurantActivity extends BaseActivity<ActivityRestaurantBinding> {

    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";
    RestaurantActivityAdapter adapter;
    UserViewModel userViewModel;
    StreamGoogleMapViewModel streamGoogleMapViewModel;
    private String apiKey;
    private boolean isRestaurantLiked = false;


    @Override
    protected ActivityRestaurantBinding getViewBinding() {
        return ActivityRestaurantBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplicationContext());
        userViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);
        streamGoogleMapViewModel = new ViewModelProvider(this,factory).get(StreamGoogleMapViewModel.class);
        apiKey = ApiKeyUtil.getApiKey(getApplicationContext());

        String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        Log.d("PlaceDetailsRequest", "Received Place ID: " + placeId);
        if (placeId != null) {
            streamGoogleMapViewModel.getPlaceDetails(placeId).observe(this, this::handlePlaceDetailsResponse);
        }


        initRecyclerView();
    }

    private void handlePlaceDetailsResponse(Resource<PlaceDetails> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            updateUIWithDetails(resource.data);
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(this, getString(R.string.error_fetching_details) + (resource.message != null ? "\n" + resource.message : ""), Toast.LENGTH_LONG).show();

            String placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
            if (placeId != null) {
                new Handler().postDelayed(() -> streamGoogleMapViewModel.getPlaceDetails(placeId).observe(this, this::handlePlaceDetailsResponse), 5000);
            }
        }
    }

    private void updateUIWithDetails(PlaceDetails placeDetails) {

        Result result = placeDetails.getResult();

        String placeId = result.getPlace_id();
        Log.d("UPUI RestaurantActivity", "Checking like status for place ID: " + placeId);

        checkRestaurantLikedStatus(placeId);

        if(result.getName() != null) {
            binding.activityRestaurantName.setText(result.getName());
            Log.d("UIUpdate", "Restaurant Name :" + result.getName());
        } else {
            binding.activityRestaurantName.setText(getString(R.string.restaurant_name_not_available));
        }

        if(result.getAdr_address() != null) {
            binding.activityRestaurantAddress.setText(Html.fromHtml(result.getAdr_address()));
            Log.d("UIUpdate", "Restaurant Address :" + Html.fromHtml(result.getAdr_address()).toString());
        } else {
            binding.activityRestaurantAddress.setText(R.string.restaurant_address_not_available);
        }

        if (result.getPhotos() != null && !result.getPhotos().isEmpty()) {
            String photoReference = result.getPhotos().get(0).getPhoto_reference();
            Log.d("UIUpdate", "Photo Reference: " + photoReference);
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.restaurant_activity_base_image)
                    .error(R.drawable.restaurant_activity_base_image)
                    .into(binding.activityRestaurantImage);
        } else {
            Log.d("UIUpdate", "No photos available");
        }

        if (result.getInternational_phone_number() != null && !result.getInternational_phone_number().isEmpty()) {
            binding.activityRestaurantButtonCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + result.getInternational_phone_number()));
                startActivity(intent);
            });
        } else {
            binding.activityRestaurantButtonCall.setOnClickListener(v -> {
                Toast.makeText(this, R.string.phone_not_available, Toast.LENGTH_SHORT).show();
            });
        }

        if (result.getWebsite() != null && !result.getWebsite().isEmpty()) {
            binding.activityRestaurantButtonWebsite.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(result.getWebsite()));
                startActivity(intent);
            });
        } else {
            binding.activityRestaurantButtonWebsite.setOnClickListener(v -> {
                Toast.makeText(this, R.string.website_not_available, Toast.LENGTH_SHORT).show();
            });
        }

        binding.activityRestaurantButtonLike.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                checkIfRestaurantIsLikedAndToggle(uid, placeId);
            }
        });

        binding.activityRestaurantButtonSelect.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                // Retrieve actual Date for mark the choice
                String choiceDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                // Call the method to define or pull restaurant choice
                handleRestaurantChoice(uid, placeId, choiceDate);
            }
        });
    }


    private void handleRestaurantChoice(String uid, String restaurantId, String choiceDate) {
        userViewModel.getUserData(uid).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                User user = resource.data.toObject(User.class);
                if (user != null) {
                    RestaurantChoice currentChoice = user.getRestaurantChoice();
                    if (currentChoice != null && restaurantId.equals(currentChoice.getRestaurantId())) {
                        // remove choice
                        userViewModel.removeRestaurantChoice(uid).observe(this, successResource -> updateUIBasedOnChoice(successResource, true));
                    } else {
                        // Define new choice
                        userViewModel.setRestaurantChoice(uid, restaurantId, choiceDate).observe(this, successResource -> updateUIBasedOnChoice(successResource, false));
                    }
                }
            }
        });
    }

    private void updateUIBasedOnChoice(Resource<Boolean> resource, boolean isChoiceRemoved) {
        if (resource.status == Resource.Status.SUCCESS) {
            if (isChoiceRemoved) {
                Log.d("RestaurantActivity", "Choice removed successfully.");
                binding.activityRestaurantButtonSelect.setImageResource(R.drawable.uncheck_button);
                Snackbar.make(binding.getRoot(), "Success pull restaurant .", Snackbar.LENGTH_SHORT).show();
            } else {
                Log.d("RestaurantActivity", "Choice updated successfully.");
                binding.activityRestaurantButtonSelect.setImageResource(R.drawable.check_button);
                Snackbar.make(binding.getRoot(), "Restaurant choose with success !", Snackbar.LENGTH_SHORT).show();
            }
        } else if (resource.status == Resource.Status.ERROR) {
            Toast.makeText(this, "fail update choice", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRestaurantLikedStatus(String placeId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            userViewModel.getUserData(uid).observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    try {
                        User user = resource.data.toObject(User.class);
                        boolean isLiked = user != null && user.getRestaurantsLike() != null && user.getRestaurantsLike().contains(placeId);
                        binding.restaurantLiked.setVisibility(isLiked ? View.VISIBLE : View.GONE);
                    } catch (Exception e) {
                        Log.e("RestaurantActivity", "Error processing like status", e);
                    }
                }
            });
        }
    }

    private void checkIfRestaurantIsLikedAndToggle(String uid, String placeId) {
        userViewModel.getUserData(uid).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                try {
                    User user = resource.data.toObject(User.class);
                    boolean isLiked = user != null && user.getRestaurantsLike() != null && user.getRestaurantsLike().contains(placeId);
                    binding.restaurantLiked.setVisibility(isLiked ? View.VISIBLE : View.GONE);

                    if (isLiked) {
                        userViewModel.removeRestaurantFromLiked(uid, placeId).observe(this, successResource -> {
                            if (successResource.status == Resource.Status.SUCCESS) {
                                Log.d("RestaurantActivity", "Removed from likes.");
                                binding.restaurantLiked.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        userViewModel.addRestaurantToLiked(uid, placeId).observe(this, successResource -> {
                            if (successResource.status == Resource.Status.SUCCESS) {
                                Log.d("RestaurantActivity", "Added to likes.");
                                // Assurez-vous de mettre à jour l'UI immédiatement après la modification
                                binding.restaurantLiked.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("RestaurantActivity", "Error processing like status", e);
                }
            }
        });
    }

    /*private void toggleRestaurantLike(String uid, String placeId, boolean isLiked) {
        Log.d("TOR RestaurantActivity", "Toggling like status for place ID: " + placeId + " to " + isLiked);
        userViewModel.toggleRestaurantLike(uid, placeId, isLiked).observe(this, resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                Log.d("TOR RestaurantActivity", "Successfully toggled like status for place ID: " + placeId);
                checkIfRestaurantIsLiked(placeId);
            } else {
                Log.e("TOR RestaurantActivity", "Failed to toggle like status for place ID: " + placeId);
                Toast.makeText(this, "Error updating like status", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void initRecyclerView(){
        adapter= new RestaurantActivityAdapter();
        binding.recyclerViewWorkmates.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerViewWorkmates.setAdapter(adapter);
    }






}