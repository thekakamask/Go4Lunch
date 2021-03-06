package com.example.go4lunch.activities.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityRestaurantBinding;
import com.example.go4lunch.models.API.PlaceDetailsAPI.PlaceDetailsResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.viewModels.UserManager;
import com.example.go4lunch.viewModels.UserViewModel;
import com.example.go4lunch.views.RestaurantAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import static com.example.go4lunch.utils.DatesHours.getCurrentTime;

// 1µ = CHANGEMENTS DU BIND DES VIEWS DE L'XML ; AVANT UTILISATION DE BUTTERKNIFE ET MAINTENANT
// UTILISATION DE L'HERITAGE DE LA CLASSE BASEACTIVITY QUI ELLE S'OCCUPE DE BINDER LES VIEWS
// CHAQUE CHANGEMENT EST INDIQUE PAR 1µ AU DEBUT

// 1µ : AVANT CHANGEMENT : extends AppCompatActivity et @BindView avec le recyclerview, le layout et tous les elements du XML
// APRES CHANGEMENT : extends BaseActivity<ActivityRestaurantBinding> (le bind du xml activity_main)
public class RestaurantActivity extends BaseActivity<ActivityRestaurantBinding> implements Serializable {


    /*@BindView(R.id.header_pic_resto)
    ImageView mRestoPhoto;
    @BindView(R.id.floating_act_btn)
    FloatingActionButton mFloatingActionButton;
    @BindView(R.id.resto_name)
    TextView mRestoName;
    @BindView(R.id.resto_address)
    TextView mRestoAddress;
    @BindView(R.id.rating_bar)
    RatingBar mRatingBar;
    @BindView(R.id.phone_btn)
    Button mPhoneButton;
    @BindView(R.id.star_btn)
    Button mStarButton;
    @BindView(R.id.internet_btn)
    Button mInternetButton;
    @BindView(R.id.restaurant_RV)
    RecyclerView mRestaurantRecyclerView;
    @BindView(R.id.restaurant_activity_layout)
    RelativeLayout mRelativeLayout;*/


    String GOOGLE_MAP_API_KEY = BuildConfig.API_KEY;
    //private final UserManager userManager = UserManager.getInstance(); I USE VIEWMODEL INTEAD
    private UserViewModel userViewModel;
    private String placeId;
    private RequestManager mGlide;
    private static final String SELECTED = "SELECTED";
    private static final String UNSELECTED = "UNSELECTED";
    //change after update (put final at db)
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    //change after update (put final at collectionUsers)
    private final CollectionReference collectionUsers = db.collection("users");
    private RestaurantAdapter restaurantAdapter;
    private static final int REQUEST_CALL=100;
    private String formattedPhoneNumber;
    //private List<String> userLike;

    //1µ : DEBUT AJOUT (inflate du layout de activity_restaurant)
    @Override
    ActivityRestaurantBinding getViewBinding() {
        return ActivityRestaurantBinding.inflate(getLayoutInflater());
    }
    //1µ :FIN AJOUT

    // 1µ : AVANT CHANGEMENT : protected void onCreate (au lieu de public) et setContentView(R.layout.activity_restaurant)
    // et ButterKnife.bind(this);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_restaurant);
        ButterKnife.bind(this);*/

        //INIT VIEWMODEL WITH PROVIDERS
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        //retrieve data when activity is open
        Intent intent= this.getIntent();
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "restaurantLiked: placeDetailsResultBundle" + (bundle == null));


        //for like when activity is open
        PlaceDetailsResult placeDetailsResult = null;
        if(bundle != null) {
            placeDetailsResult = (PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
            Log.d(TAG, "restaurantLiked: placeDetailsResult1 " + placeDetailsResult.getName());
            Log.d(TAG, "restaurantLiked: placeDetailsResult1 " + placeDetailsResult.getPhotos());

        }
        if (placeDetailsResult != null) {
            final String placeRestaurantId = placeDetailsResult.getPlaceId();
            //UserManager.getInstance().getUserData(Objects.requireNonNull(UserManager.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            //UserManager.getInstance().getUserData(userManager.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            //INSTEAD I USE LINE 125
            Task<DocumentSnapshot> userUID = userViewModel.getUserData(userViewModel.getCurrentUser().getValue().getUid()).getValue();

            userUID.addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null){
                    if (user.getLike() != null && !user.getLike().isEmpty() && user.getLike().contains(placeRestaurantId)) {

                        //1µ : remplacement de mStarButton (qui etait lié avec @BindView(R.id.star_btn) Button mStarButton;
                        // par binding.starBtn (star_btn (id de l'xml) sans le _)

                        //binding.starBtn.setBackgroundColor(Color.BLUE);
                        //Drawable restaurantStarImg = getDrawable(R.drawable.activity_restaurant_star);
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star), null, null);
                        //binding.starBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.activity_restaurant_star));
                    } else {
                        //Drawable restaurantStarHollowImg = getDrawable(R.drawable.activity_restaurant_star);
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star_hollow), null, null);
                        //binding.starBtn.setBackgroundColor(Color.TRANSPARENT);
                        //binding.starBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.activity_restaurant_star_hollow));
                    }
                }
            });
        }

        this.starBtn();
        this.floatingBtn();
        this.setUpRV(placeId);
        this.retrieveData();

        //for action bar hiding
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }

    // LIKE BUTTON
    //1µ : remplacement de mStarButton (qui etait lié avec @BindView(R.id.star_btn) Button mStarButton;
    // par binding.starBtn (star_btn (id de l'xml) sans le _)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void starBtn() {
        binding.starBtn.setOnClickListener(view -> restaurantLiked());
    }


    // LIKE/DISLIKE
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void restaurantLiked() {

        Intent intent = this.getIntent();
        //Bundle bundle = intent.getBundleExtra("placeDetailsResult");
        Bundle bundle = intent.getExtras();
        PlaceDetailsResult placeDetailsResult = null;
        if (bundle != null) {
            placeDetailsResult= (PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }
        if (placeDetailsResult != null) {
            final String placeRestaurantId = placeDetailsResult.getPlaceId();
            Log.d(TAG, "restaurantLiked:" + placeDetailsResult);
            //UserManager.getInstance().getUserData(userManager.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            //INSTEAD I USE LINE 185
            Task<DocumentSnapshot> userUID = userViewModel.getUserData(userViewModel.getCurrentUser().getValue().getUid()).getValue();

            userUID.addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                Log.d(TAG, "restaurantLiked:" + user);
                if (user != null) {

                    /*if (binding.starBtn.equals(R.drawable.activity_restaurant_star_hollow)) {
                        Log.d(TAG, "restaurantLiked: testest");

                        userManager.updateLike(userManager.getCurrentUser().getUid(), placeRestaurantId);
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star), null, null);

                    } else if (binding.starBtn.equals(R.drawable.activity_restaurant_star)) {

                        userManager.deleteLike(userManager.getCurrentUser().getUid(), placeRestaurantId);
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star_hollow), null, null);
                        Log.d(TAG, "restaurantLiked: testest2");
                    }*/

                    //userLike = user.getLike();

                    //ERREUR
                    //java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.util.ArrayList.isEmpty()' on a null object reference
                    //        at com.example.go4lunch.activities.ui.RestaurantActivity.lambda$restaurantLiked$1$RestaurantActivity(RestaurantActivity.java:160)
                    if (user.getLike() == null) {
                        user.setLike(new ArrayList<String>());
                    }

                    if(!user.getLike().isEmpty() && user.getLike().contains(placeRestaurantId)) {

                        //userManager.deleteLike(userManager.getCurrentUser().getUid(), placeRestaurantId); INSTEAD I USE LINE 219

                        userViewModel.deleteLike(userViewModel.getCurrentUser().getValue().getUid(), placeRestaurantId);
                        //1µ : remplacement de mStarButton (qui etait lié avec @BindView(R.id.star_btn) Button mStarButton;
                        // par binding.starBtn (star_btn (id de l'xml) sans le _)
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star_hollow), null, null);
                    }else{
                        //userManager.updateLike(userManager.getCurrentUser().getUid(), placeRestaurantId); INSTEAD I USE LINE 226

                        userViewModel.updateLike(userViewModel.getCurrentUser().getValue().getUid(), placeRestaurantId);
                        binding.starBtn.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(R.drawable.activity_restaurant_star), null, null);
                    }
                }
            });
        }

    }

    //FLOATING BUTTON
    //1µ : remplacement de mFloatingActionButton (qui etait lié avec @BindView(R.id.floating_act_btn) FloatingActionButton mFloatingActionButton;
    // par binding.floatingActBtn (floating_act_btn (id de l'xml) sans le _)
    private void floatingBtn() {
        binding.floatingActBtn.setOnClickListener( v -> {
            if(v.getId() == R.id.floating_act_btn)
                if (SELECTED.equals(binding.floatingActBtn.getTag())) {
                    selectedRestaurant();
                }else if (binding.floatingActBtn.isSelected()) {
                    selectedRestaurant();
                } else {
                    removeRestaurant();
                }
        });
    }

    // RETRIEVING SELECTED RESTAURANT
    public void selectedRestaurant() {
        Intent intent= this.getIntent();
        Bundle bundle = intent.getExtras();

        PlaceDetailsResult placeDetailsResult = null;
        if (bundle != null) {
            placeDetailsResult = (PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }

        //1µ : remplacement de mFloatingActionButton (qui etait lié avec @BindView(R.id.floating_act_btn) FloatingActionButton mFloatingActionButton;
        // par binding.floatingActBtn (floating_act_btn (id de l'xml) sans le _)
        if(placeDetailsResult != null) {
            //userManager.updateIdOfPlace(Objects.requireNonNull(userManager.getCurrentUser()).getUid(), placeDetailsResult.getPlaceId(), getCurrentTime());
            //INSTEAD I USE LINE 267

            userViewModel.updateIdOfPlace(Objects.requireNonNull(userViewModel.getCurrentUser()).getValue().getUid(), placeDetailsResult.getPlaceId(), getCurrentTime());
            binding.floatingActBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.activity_restaurant_unvalid));
            binding.floatingActBtn.setTag(UNSELECTED);
        }
    }

    // REMOVING RESTAURANT CHOICE
    //1µ : remplacement de mFloatingActionButton (qui etait lié avec @BindView(R.id.floating_act_btn) FloatingActionButton mFloatingActionButton;
    // par binding.floatingActBtn (floating_act_btn (id de l'xml) sans le _)
    public void removeRestaurant() {
        //userManager.deleteIdOfPlace(Objects.requireNonNull(Objects.requireNonNull(userManager.getCurrentUser().getUid())));
        //INTEAD I USE LINE 280

        userViewModel.deleteIdOfPlace(Objects.requireNonNull(Objects.requireNonNull(userViewModel.getCurrentUser().getValue().getUid())));
        binding.floatingActBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.activity_restaurant_valid_done));
        binding.floatingActBtn.setTag(SELECTED);
    }

    //1µ : remplacement de mRestaurantRecyclerView (qui etait lié avec @BindView(R.id.restaurant_RV) RecyclerView mRestaurantRecyclerView;
    // par binding.restaurantRV (restaurant_RV (id de l'xml) sans le _)
    private void setUpRV(String placeId) {

        Query query = collectionUsers.whereEqualTo("place id", placeId);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        this.restaurantAdapter = new RestaurantAdapter(options, Glide.with(this));
        binding.restaurantRV.setHasFixedSize(true);
        binding.restaurantRV.setAdapter(restaurantAdapter);
        binding.restaurantRV.setLayoutManager(new LinearLayoutManager(this));
    }

    // RETRIEVE DATA FOR LISTFRAGMENT
    private void retrieveData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PlaceDetailsResult placeDetailsResult = null;
        if (bundle!=null) {
            placeDetailsResult = (PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }
        if (placeDetailsResult != null) {
            updateUI(placeDetailsResult,mGlide);
            placeId = placeDetailsResult.getPlaceId();
        }
    }

    // UPDATE UI
    //1µ : remplacement de mRestoPhoto (qui etait lié avec @BindView(R.id.header_pic_resto) ImageView mRestoPhoto;
    // par binding.headerPicResto (header_pic_resto (id de l'xml) sans le _)
    // meme chose pour mRestoName ( @BindView(R.id.resto_name) TextView mRestoName;) par binding.restoName
    // et mRestoAddress ( @BindView(R.id.resto_address) TextView mRestoAddress;) par binding.restoAddress
    private void updateUI(PlaceDetailsResult placeDetailsResult, RequestManager glide) {
        mGlide=glide;

        //photos with Glide
        if (placeDetailsResult.getPhotos() != null && !placeDetailsResult.getPhotos().isEmpty()) {
            Glide.with(this)
                 .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
                         placeDetailsResult.getPhotos().get(0).getPhotoReference() + "&key=" + GOOGLE_MAP_API_KEY)
                 .into(binding.headerPicRestaurant);
        }else{
            binding.headerPicRestaurant.setImageResource(R.drawable.no_pic);
        }
        //RESTAURANT NAME
        binding.restaurantName.setText(placeDetailsResult.getName());
        //RESTAURANT ADRESS
        binding.restaurantAddress.setText(placeDetailsResult.getVicinity());
        //RESTAURANT RATING
        restaurantRating(placeDetailsResult);
        //RESTAURANT PHONE
        String formattedPhoneNumber = placeDetailsResult.getFormattedPhoneNumber();
        phoneButton(formattedPhoneNumber);
        //RESTAURANT WEBSITE
        String url = placeDetailsResult.getWebsite();
        webButton(url);
    }

    //1µ : remplacement de mRatingBar (qui etait lié avec @BindView(R.id.rating_bar) RatingBar mRatingBar;
    // par binding.ratingBar (rating_Bar (id de l'xml) sans le _)
    private void restaurantRating(PlaceDetailsResult placeDetailsResult) {
        if (placeDetailsResult.getRating() != null) {
            double restaurantRating = placeDetailsResult.getRating();
            double rating = (restaurantRating/5) *3;
            this.binding.ratingBar.setRating((float) rating);
            this.binding.ratingBar.setVisibility(View.VISIBLE);

        } else {
            this.binding.ratingBar.setVisibility(View.GONE);
        }

    }

    //1µ : remplacement de mPhoneButton (qui etait lié avec @BindView(R.id.phone_btn) Button mPhoneButton;
    // par binding.phoneBtn (phone_btn (id de l'xml) sans le _)
    private void phoneButton(String formattedPhoneNumber) {
        binding.phoneBtn.setOnClickListener(view -> makePhoneCall(formattedPhoneNumber));

    }

    private void makePhoneCall (String formattedPhoneNumber) {
        if ( formattedPhoneNumber != null && !formattedPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNumber));
            Log.d("PhoneNumber", formattedPhoneNumber);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showSnackBar(getString(R.string.error_unknown));
            }
        } else {
            showSnackBar(getString(R.string.error_no_phone_available));
        }

        /*if (ContextCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RestaurantActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else if (formattedPhoneNumber != null && !formattedPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNumber));
            Log.d("PhoneNumber", formattedPhoneNumber);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showSnackBar(getString(R.string.error_unknown));
            }
        } else {
            showSnackBar(getString(R.string.error_no_phone_available));
        }*/
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(formattedPhoneNumber);
            } else {
                Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }*/




    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(formattedPhoneNumber);
            } else {
                Toast.makeText(this, R.string.error_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    //1µ : remplacement de mInternetButton (qui etait lié avec @BindView(R.id.internet_btn) Button mInternetButton;
    // par binding.internetBtn (internet_btn (id de l'xml) sans le _)
    private void webButton(String url) {
        //mInternetButton.setOnClickListener(view -> makeWebView(url));
        binding.internetBtn.setOnClickListener(view -> openWebPage(url));
    }

    /* private void makeWebView(String url) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(RestaurantActivity.this, WebViewActivity.class);
            intent.putExtra("website", url);
            Log.d("website",url);
            startActivity(intent);
        } else {
            showSnackBar(getString(R.string.error_no_website));
        }
    }*/

    public void openWebPage(String webUrl) {
        if (webUrl != null && webUrl.startsWith("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                showSnackBar(getString(R.string.error_no_website));
            }
        }
    }

    //1µ : remplacement de mRelativeLayout (qui etait lié avec @BindView(R.id.restaurant_activity_layout) RelativeLayout mRelativeLayout;
    // par binding.restaurantActivityLayout (restaurant_activity_layout (id de l'xml) sans le _)
    private void showSnackBar (String message) {
        Snackbar.make(binding.restaurantActivityLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        restaurantAdapter.stopListening();
    }


    @Override
    public void onResume() {
        super.onResume();
    }




}
