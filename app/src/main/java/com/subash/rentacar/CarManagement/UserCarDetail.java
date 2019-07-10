package com.subash.rentacar.CarManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UserCarDetail extends AppCompatActivity {

    private ImageView carImageView;
    private EditText carTitle, carDescription, carRegNo;
    private String Title, Image, Desc, RegNo, newTitle, Model;
    private Button addCarDetails;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_car_detail);
        initViews();
        getDataFromBundle();
        setDataOnViews();

    }

    void initViews() {
        carImageView = findViewById(R.id.UserHeaderImage);
        carTitle = findViewById(R.id.UserCarTitle);
        carTitle.setEnabled(false);
        carDescription = findViewById(R.id.UserCarDesc);
        addCarDetails = findViewById(R.id.btnUpdateDetails);
        carRegNo = findViewById(R.id.UserCarReg);


    }

    void getDataFromBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Title = bundle.getString("Title");
            Model = bundle.getString("Model");
            Image = bundle.getString("Image");
            Desc = bundle.getString("Brand");
            RegNo = bundle.getString("RegistrationNo");
            newTitle = Desc + "-" + Model;

        }

    }

    void setDataOnViews() {
        Glide.with(getApplicationContext())
                .load(Image).placeholder(R.drawable.carimage).into(carImageView);
        carTitle.setText(newTitle);
        carDescription.setText(Desc);
        carRegNo.setText(RegNo);
    }


    public void addUpdateDetails(View view) {
        Intent intent = new Intent(UserCarDetail.this, CarUpdateActivity.class);
        startActivity(intent);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Car...");
        progressDialog.show();
        RegNo = carRegNo.getText().toString().trim();

        if (user != null) {
            db.collection("Cars").document(user.getUid()).
                    collection("CarCollection").document(newTitle).update("RegistrationNo", RegNo)
                    .addOnSuccessListener(aVoid -> {
                    }).addOnFailureListener(e -> {
                Toast.makeText(UserCarDetail.this, "Error on Adding Car" + e.toString(), Toast.LENGTH_SHORT).show();

            });

            db.collection("AllCars").document(newTitle).update("RegistrationNo", RegNo)
                    .addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(UserCarDetail.this, "Car Updated Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(UserCarDetail.this, "Error on Adding Car" + e.toString(), Toast.LENGTH_SHORT).show();

            });
        }


    }
}
