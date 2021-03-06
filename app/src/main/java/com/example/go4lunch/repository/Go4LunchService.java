package com.example.go4lunch.repository;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.API.NearbySearchAPI.PlaceNearbySearch;
import com.example.go4lunch.models.API.PlaceDetailsAPI.PlaceDetail;
import com.example.go4lunch.models.API.AutoCompleteAPI.AutoCompeteResult;

public interface Go4LunchService {

    //String GOOGLE_MAP_API_KEY = "AIzaSyBrdvHdbW8KQiRGj7dUolbL6mLtszf2g0g";
    String GOOGLE_MAP_API_KEY = BuildConfig.API_KEY;
//    String GOOGLE_MAP_API_KEY = MAPS_API_KEY;
    //GoogleMap API Request
    //JUTILISE A LA BASE PLACENEARBYSEARCH DANS LOBSERVABLE

    @GET("nearbysearch/json?"+"&key="+ GOOGLE_MAP_API_KEY)
    Observable<PlaceNearbySearch> getRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type) ;

    /*@GET("nearbysearch/json?")
    Observable<PlaceNearbySearch> getRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("key") String key ) ;*/

//    @GET("nearbysearch/json?"+"&key="+ GOOGLE_MAP_API_KEY)
//    Observable<PlaceNearbySearch> getNextPageRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type, @Query("pagetoken") String nextPageToken) ;

    //PlaceDetails API Request


    @GET("details/json?"+"&key=" + GOOGLE_MAP_API_KEY)
    Observable<PlaceDetail> getDetails(@Query("place_id") String placeId);


    /*@GET("details/json?")
    Observable<PlaceDetail> getDetails(@Query("place_id") String placeId, @Query ("key") String key);*/

    //Autocomplete API Request
    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/output?parameters"+ GOOGLE_MAP_API_KEY)
    Observable<AutoCompeteResult> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);

    /*@GET("maps/api/place/autocomplete/json?strictbounds&key="+GOOGLE_MAP_API_KEY)
    Observable<AutoCompeteResult> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);
*/


}
