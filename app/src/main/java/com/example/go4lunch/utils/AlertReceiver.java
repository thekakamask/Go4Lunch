package com.example.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.go4lunch.R;
import com.example.go4lunch.models.API.PlaceDetailsAPI.PlaceDetail;
import com.example.go4lunch.models.User;
import com.example.go4lunch.repository.StreamRepository;
import com.example.go4lunch.viewModels.UserManager;
import com.example.go4lunch.viewModels.UserViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.Objects;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class AlertReceiver extends BroadcastReceiver {

    //private final UserManager userManager = UserManager.getInstance();
    //I USE VIEWMODEL INSTEAD
    private String userIdNotif;
    private PlaceDetail detail;
    private String restoNameNotif;
    private Disposable mDisposable;
    private String restoAdressNotif;

    private String nameNotif;
    private String notifMessage;
    private Context context;
    private int timeUser;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        //INIT VIEWMODEL WITH PROVIDERS
        UserViewModel userViewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(UserViewModel.class);

        // PLACEID AND TIME REQUEST
        //UserManager.getInstance().getUserData(userManager.getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
        //INSTEAD I USE LINE 52

        Task<DocumentSnapshot> userUID = userViewModel.getUserData(userViewModel.getCurrentUser().getValue().getUid()).getValue();

        userUID.addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                if (!user.getIdOfPlace().isEmpty() && (user.getCurrentTime() <= 1200) && (user.getCurrentTime() > 0)) {

                    userIdNotif= user.getIdOfPlace();
                    timeUser = user.getCurrentTime();
                    executeHttpRequestWithRetrofit();
                    Log.d("IdNotifTest", userIdNotif);

                    if (this.mDisposable != null && !this.mDisposable.isDisposed())
                        this.mDisposable.dispose();
                }
            }
        });

    }

    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = StreamRepository.streamFetchDetails(userIdNotif)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(@NonNull PlaceDetail placeDetail) {
                        detail=placeDetail;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("RestoNotifError", Log.getStackTraceString(e));
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete() {
                        if(userIdNotif != null) {
                            restoNameNotif=detail.getResult().getName();
                            restoAdressNotif=detail.getResult().getVicinity();
                            notifCoworkers(userIdNotif,timeUser);
                            Log.d("RestoNameNotif", restoNameNotif+ "" + restoAdressNotif +"" + nameNotif +"" + notifMessage);

                        }
                    }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifCoworkers(String userIdNotif, int timeUser) {

        //INIT VIEWMODEL WITH PROVIDERS
        UserViewModel userViewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(UserViewModel.class);

        //userManager.getUsersCollection() INSTEAD I USE LIGNE 109

        userViewModel.getUsersCollection().getValue()
                .whereEqualTo("placeId", userIdNotif)
                .whereEqualTo("currentTime", timeUser)
                .whereLessThan("currentTime", 1200)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            Log.d("coworkersNotif", documentSnapshot.getId() + "" + documentSnapshot.getData());
                            nameNotif=String.valueOf(documentSnapshot.get("username"));
                            Log.d("nameNotif", Objects.requireNonNull(nameNotif));

                            if(nameNotif != null) {
                                notifMessage=(context.getString(R.string.lunch_at) +""+
                                        restoNameNotif + "" + restoAdressNotif +""+context.getString(R.string.with)+
                                        "" + nameNotif);
                            }else {
                                notifMessage=(context.getString(R.string.lunch_at) +"" +
                                        restoNameNotif+""+ restoAdressNotif+""+
                                        context.getString(R.string.alone));
                            }

                            NotificationHelper notificationHelper = new NotificationHelper(context);
                            NotificationCompat.Builder nc = notificationHelper.getChannelNotification(notifMessage);
                            notificationHelper.getManager().notify(1,nc.build());
                            }
                        } else {
                        Log.e("numberMatesError", "Error getting documents", task.getException());
                        }
                });
    }




}
