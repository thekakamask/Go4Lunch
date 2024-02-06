package com.dcac.go4lunch.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class ApiKeyUtil {

    public static String getApiKey(Context context) {
        try {
            Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData;
            return metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not load api key from manifest", e);
        }
    }
}
