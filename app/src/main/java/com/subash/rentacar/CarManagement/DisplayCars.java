package com.subash.rentacar.CarManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.subash.rentacar.CarManagement.Models.CarModel;
import com.subash.rentacar.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayCars extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.car_container)
    RecyclerView friendList;


    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_cars);

        ButterKnife.bind(this);
        init();
        getCarList();
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getCarList() {
        Query query = db.collection("Cars").document(user.getUid()).collection("CarCollection");

        FirestoreRecyclerOptions<CarModel> response = new FirestoreRecyclerOptions.Builder<CarModel>()
                .setQuery(query, CarModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CarModel, CarHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull CarHolder holder, int position, @NonNull CarModel model) {
                progressBar.setVisibility(View.GONE);
                holder.carTitle.setText(model.getCarTitle());
                holder.registrationNo.setText(model.getRegistrationNo());
                holder.uploadedBy.setText(model.getUploadedBy());
                holder.textView.setText(model.getPrice());
                Glide.with(getApplicationContext())
                        .load(model.getImage()).placeholder(R.drawable.carimage).into(holder.imageView);


                holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getRegistrationNo() + ", " + model.getBrand() + " at " + model.getModel(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Intent intent = new Intent(DisplayCars.this, UserCarDetail.class);
                    Bundle bucket = new Bundle();
                    bucket.putString("Brand", model.getBrand());
                    bucket.putString("Image", model.getImage());
                    bucket.putString("Title", model.getCarTitle());
                    bucket.putString("Model", model.getModel());
                    bucket.putString("RegistrationNo", model.getRegistrationNo());
                    bucket.putString("Price", model.getPrice());
                    intent.putExtras(bucket);
                    startActivity(intent);

                });

            }


            @NonNull
            @Override
            public CarHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new CarHolder(view);
            }


            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }


        };

        adapter.notifyDataSetChanged();
        friendList.setAdapter(adapter);

    }

    public class CarHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.carTitle)
        TextView carTitle;
        @BindView(R.id.registrationNo)
        TextView registrationNo;
        @BindView(R.id.car_image)
        ImageView imageView;
        @BindView(R.id.uploadedBy)
        TextView uploadedBy;

        @BindView(R.id.txtPrice)
        TextView textView;

        public CarHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
