package com.subash.rentacar.CarManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.subash.rentacar.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CarDetail extends AppCompatActivity {

    private ImageView carImageView;
    private TextView carTitle, carDescription, carRegNo;
    private String Title, Image, Desc, RegNo;
    private Button addCarDetails;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        initViews();
        getDataFromBundle();
        setDataOnViews();
    }

    void initViews() {
        carImageView = findViewById(R.id.headerImage);
        carTitle = findViewById(R.id.carTitle);
        carDescription = findViewById(R.id.carDesc);
        addCarDetails = findViewById(R.id.btnAddCar);
        carRegNo = findViewById(R.id.carReg);


    }

    void getDataFromBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Title = bundle.getString("Brand");
            Image = bundle.getString("Image");
            Desc = bundle.getString("Title");
            RegNo = bundle.getString("RegistrationNo");
        }

    }

    void setDataOnViews() {
        Glide.with(getApplicationContext())
                .load(Image).placeholder(R.drawable.carimage).into(carImageView);
        carTitle.setText(Title);
        carDescription.setText(Desc);
        carRegNo.setText(RegNo);
    }

    public void addCarBookingDetails(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Booking Car...");
        progressDialog.show();
        Map<String, Object> newBooking = new HashMap<>();
        newBooking.put("CarTitle", Title);
        newBooking.put("RegistrationNo", RegNo);

        if (user != null) {
            db.collection("Bookings").
                    document(user.getUid()).collection("BookedCars").add(newBooking).addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(CarDetail.this, "Car Added Successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(CarDetail.this, "Error on Adding Car" + e.toString(), Toast.LENGTH_SHORT).show();

            });
        }


    }
}
