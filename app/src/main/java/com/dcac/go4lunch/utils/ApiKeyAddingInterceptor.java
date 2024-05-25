package com.dcac.go4lunch.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyAddingInterceptor implements Interceptor {

    private final Context context;

    public ApiKeyAddingInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String apiKey = getApiKeyFromManifest();

        //Display API
        Log.d("ApiKeyInterceptor", "API Key: " + apiKey);

        Request newRequest = originalRequest.newBuilder()
                .url(originalRequest.url().newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build())
                .build();

        // Display final URL
        Log.d("ApiKeyInterceptor", "Final URL: " + newRequest.url());

        return chain.proceed(newRequest);
    }

    private String getApiKeyFromManifest() {
        try {
            Bundle bundle = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            String apiKey = bundle.getString("com.google.android.geo.API_KEY");
            // Log for check API recuperation on manifest
            Log.d("ApiKeyInterceptor", "API Key from Manifest: " + apiKey);
            return apiKey;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ApiKeyInterceptor", "Could not get API key from manifest", e);
            throw new AssertionError("Could not get package name: " + e);
        }
    }
}
