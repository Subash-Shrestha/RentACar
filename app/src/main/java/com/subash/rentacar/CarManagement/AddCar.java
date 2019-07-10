package com.subash.rentacar.CarManagement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.subash.rentacar.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class AddCar extends AppCompatActivity {
    private FirebaseFirestore db;
    private StorageReference mStorage;
    private ImageButton imageButton;
    private Uri mImageUri = null;
    private FirebaseUser user;
    private ImageView mImageView;
    private EditText edtRegistrationNo, edtModel, edtBrand;
    private final int PICK_IMAGE_REQUEST = 71;
    private String downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCarDetail();
            }
        });
        imageButton = findViewById(R.id.imageButton);
        edtRegistrationNo = findViewById(R.id.edtRegistrationNo);
        edtBrand = findViewById(R.id.edtBrand);
        edtModel = findViewById(R.id.edtModel);
        mImageView = findViewById(R.id.imageView);

        mStorage = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCarDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final String registrationNo, model, brand;
        registrationNo = edtRegistrationNo.getText().toString().trim();
        model = edtModel.getText().toString().trim();
        brand = edtBrand.getText().toString().trim();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        Random random = new Random();
        final String carID = String.format("%04d", random.nextInt(10000));

        if (!TextUtils.isEmpty(registrationNo) && !TextUtils.isEmpty(model) && !TextUtils.isEmpty(brand) && mImageUri != null) {

            StorageReference filepath = mStorage.child("Car_Images").child(Objects.requireNonNull(mImageUri.getLastPathSegment()));

            filepath.putFile(mImageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadUri = Objects.requireNonNull(task.getResult()).toString();
                }
            }).addOnSuccessListener(taskSnapshot -> {
                Map<String, Object> newCar = new HashMap<>();
                newCar.put("CarTitle", brand + " " + model);
                newCar.put("RegistrationNo", registrationNo);
                newCar.put("Model", model);
                newCar.put("Brand", brand);
                newCar.put("UploadedOn", formattedDate);
                newCar.put("UploadedBy", Objects.requireNonNull(user.getEmail()));
                assert downloadUri != null;
                newCar.put("Image", downloadUri);


                db.collection("Cars").document(user.getUid()).collection("CarCollection").document(brand + "-" + model).set(newCar)
                        .addOnSuccessListener(aVoid -> {
                        }).addOnFailureListener(e -> {
                    Toast.makeText(AddCar.this, "Error on Adding Car" + e.toString(), Toast.LENGTH_SHORT).show();

                });

                db.collection("AllCars").document(brand + "-" + model).set(newCar)
                        .addOnSuccessListener(aVoid -> {
                            progressDialog.dismiss();
                            Toast.makeText(AddCar.this, "Car Added Successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AddCar.this, "Error on Adding Car" + e.toString(), Toast.LENGTH_SHORT).show();

                });
            });
        }


    }


}
