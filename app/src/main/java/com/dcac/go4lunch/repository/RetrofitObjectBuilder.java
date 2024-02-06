package com.dcac.go4lunch.repository;

import android.content.Context;

import com.dcac.go4lunch.utils.ApiKeyAddingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitObjectBuilder {

    //private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(String baseUrl, Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new ApiKeyAddingInterceptor(context))
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

}
