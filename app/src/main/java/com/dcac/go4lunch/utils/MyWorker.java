package com.dcac.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.models.user.RestaurantChoice;
import com.dcac.go4lunch.models.user.User;
import com.dcac.go4lunch.repository.AuthService;
import com.dcac.go4lunch.repository.UserRepository;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyWorker extends Worker {

    private final UserRepository userRepository;
    private final RestaurantChoiceUpdater restaurantChoiceUpdater;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AuthService authService = new AuthService(context);
        this.userRepository = UserRepository.getInstance(authService);
        this.restaurantChoiceUpdater = new RestaurantChoiceUpdater(userRepository);
        Log.d("MyWorker", "MyWorker initialized");
    }

    @NonNull
    @Override
    public Result doWork() {
        final Context context = getApplicationContext();
        String userId = getInputData().getString("userId");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        Log.d("MyWorker", "doWork: started");

        try {
            QuerySnapshot snapshot = Tasks.await(userRepository.getAllUsersTask());
            Map<String, List<User>> restaurantToUsersMap = new HashMap<>();

            for (QueryDocumentSnapshot document : snapshot) {
                User user = document.toObject(User.class);
                if (user != null && user.getRestaurantChoice() != null && currentDate.equals(user.getRestaurantChoice().getChoiceDate())) {
                    List<User> usersList = restaurantToUsersMap.get(user.getRestaurantChoice().getRestaurantId());
                    if (usersList == null) {
                        usersList = new ArrayList<>();
                        restaurantToUsersMap.put(user.getRestaurantChoice().getRestaurantId(), usersList);
                    }
                    usersList.add(user);
                }
            }

            if (restaurantToUsersMap.isEmpty()) {
                Log.d("MyWorker", "No users or restaurant choices found for today");
            }

            for (Map.Entry<String, List<User>> entry : restaurantToUsersMap.entrySet()) {
                List<User> usersList = entry.getValue();
                User sampleUser = usersList.get(0);
                RestaurantChoice restaurantChoice = sampleUser.getRestaurantChoice();
                String restaurantName = restaurantChoice.getRestaurantName();
                String restaurantAddress = restaurantChoice.getRestaurantAddress();
                List<String> userNames = new ArrayList<>();
                for (User user : usersList) {
                    userNames.add(user.getUserName());
                }
                String message = "Lunch at " + restaurantName + " (" + restaurantAddress + ") with " + String.join(", ", userNames);
                sendNotification(context, message);
                restaurantChoiceUpdater.updateRestaurantChoice(userId);
                Log.d("MyWorker", "Notification sent for restaurant: " + restaurantName);
            }

            Log.d("MyWorker", "Work finished successfully");
            return Result.success();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("MyWorker", "Error waiting for results", e);
            return Result.failure();
        }
    }

    private void sendNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LunchChannel";
            String description = "Channel for Lunch Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Lunch Time!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Unique Id gen for notifications
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
        Log.d("MyWorker", "Notification sent: " + message);
    }


}
