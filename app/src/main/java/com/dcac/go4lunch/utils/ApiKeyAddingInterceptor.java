package com.dcac.go4lunch.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiKeyAddingInterceptor implements Interceptor {

    private final Context context;

    public ApiKeyAddingInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String apiKey = getApiKeyFromManifest();

        Request newRequest = originalRequest.newBuilder()
                .url(originalRequest.url().newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build())
                .build();

        return chain.proceed(newRequest);
    }

    private String getApiKeyFromManifest() {
        try {
            Bundle bundle = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Could not get package name: " + e);
        }
    }
}
