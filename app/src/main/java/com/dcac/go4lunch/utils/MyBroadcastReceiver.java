package com.dcac.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MyBroadcastReceiver", "onReceive: triggered");
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        Log.d("MyBroadcastReceiver", "Enqueuing work request");
        WorkManager.getInstance(context).enqueue(workRequest);
        Log.d("MyBroadcastReceiver", "Work request enqueued");

    }
}

