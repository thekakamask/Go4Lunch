package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.ui.RestaurantActivity;
import com.example.go4lunch.models.API.PlaceDetailsAPI.PlaceDetail;
import com.example.go4lunch.models.User;
import com.example.go4lunch.repository.StreamRepository;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class CoworkersViewHolder extends RecyclerView.ViewHolder {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.coworker_photo)
    ImageView mCoworkerphoto;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.coworker_name)
    TextView mCoworkerName;

    private Disposable mDisposable;
    private String restoName;
    private String idResto;
    private PlaceDetail detail;
    private String userName;

    public CoworkersViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        //RETRIEVE RESTAURANT WHEN CLICK ON COWORKER
        itemView.setOnClickListener(v -> {
            if(detail != null) {
                Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("placeDetailsResult", detail.getResult());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }


    public void updateWithDetails(User users, RequestManager glide) {
        //RETRIEVE NAME AND ID RESTO FOR REQUEST
        userName = users.getUsername();
        idResto = users.getIdOfPlace();

        Log.d("idRestoUser", "idRestoUsers" + "" + idResto);
        executeHttpRequestRetrofit();
        //RETRIEVE USER PHOTO
        if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
            glide.load(users.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mCoworkerphoto);
        } else {
            mCoworkerphoto.setImageResource(R.drawable.no_pic);
        }

        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    private void executeHttpRequestRetrofit() {
        this.mDisposable = StreamRepository.streamFetchDetails(idResto)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(@NonNull PlaceDetail placeDetail) {
                        detail= placeDetail;

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("onErrorWorkmates", Log.getStackTraceString(e));
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {
                        if (idResto != null) {
                            restoName = detail.getResult().getName();
                            mCoworkerName.setText(userName + " " + itemView.getContext().getString(R.string.eat_coworkers) + " " + restoName);
                            Log.d("OnCompleteRestoName", "restoName" + idResto);
                        } else {
                            mCoworkerName.setText(userName + " " + itemView.getContext().getString(R.string.no_decided));
                            Log.d("RestoName", "noResto" + userName);
                        }

                    }
                });
    }
}