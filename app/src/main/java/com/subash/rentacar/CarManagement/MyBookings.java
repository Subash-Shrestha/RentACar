package com.subash.rentacar.CarManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.subash.rentacar.CarManagement.Models.CarModel;
import com.subash.rentacar.CarManagement.Models.MyBookingModel;
import com.subash.rentacar.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBookings extends AppCompatActivity {

    @BindView(R.id.bookingProgress_bar)
    ProgressBar progressBar;

    @BindView(R.id.booking_container)
    RecyclerView recyclerView;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private CollectionReference collectionReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        ButterKnife.bind(this);
        init();
        getBookingList();
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("CarBookings").document(user.getUid()).collection("BookedCars");
    }


    private void getBookingList() {

        Query query = db.collection("Bookings").document(user.getUid()).collection("BookedCars");

        FirestoreRecyclerOptions<MyBookingModel> response = new FirestoreRecyclerOptions.Builder<MyBookingModel>()
                .setQuery(query, MyBookingModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<MyBookingModel, BookingHolder>(response) {

            @NonNull
            @Override
            public BookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.booking_list_item, parent, false);

                return new MyBookings.BookingHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }

            @Override
            protected void onBindViewHolder(@NonNull BookingHolder bookingHolder, int i, @NonNull MyBookingModel myBookingModel) {
                progressBar.setVisibility(View.GONE);
                bookingHolder.carBookingTitle.setText(myBookingModel.getCarTitle());
                bookingHolder.registrationNo.setText(myBookingModel.getRegistrationNo());

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }

    public class BookingHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bookingCarTitle)
        TextView carBookingTitle;
        @BindView(R.id.bookingRegistrationNo)
        TextView registrationNo;

        public BookingHolder(View itemView) {
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
