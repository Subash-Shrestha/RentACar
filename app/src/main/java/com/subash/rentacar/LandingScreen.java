package com.subash.rentacar;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.MenuItem;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.subash.rentacar.CarManagement.AddCar;
import com.subash.rentacar.CarManagement.CarDetail;
import com.subash.rentacar.CarManagement.DisplayCars;
import com.subash.rentacar.CarManagement.Models.CarModel;
import com.subash.rentacar.CarManagement.MyBookings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView userImage;
    private TextView userDisplayName, userDisplayEmail;
    private NavigationView mNavigationView;

    @BindView(R.id.AllCarProgress_bar)
    ProgressBar progressBar;

    @BindView(R.id.AllCar_container)
    RecyclerView friendList;

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //      .setAction("Action", null).show();
            Intent intent = new Intent(LandingScreen.this, AddCar.class);
            startActivity(intent);

        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mNavigationView = findViewById(R.id.nav_view);
        userImage = findViewById(R.id.userImage);
        userDisplayName = mNavigationView.getHeaderView(0).findViewById(R.id.userDisplayName);
        userDisplayEmail = mNavigationView.getHeaderView(0).findViewById(R.id.userDisplayEmail);
        if (user != null) {
            userDisplayName.setText(user.getDisplayName());
            userDisplayEmail.setText(user.getEmail());
        }

        ButterKnife.bind(this);
        init();
        getCarList();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_myCars) {
            Intent intent = new Intent(LandingScreen.this, DisplayCars.class);
            startActivity(intent);

        } else if (id == R.id.nav_myBookings) {
            Intent intent = new Intent(LandingScreen.this, MyBookings.class);
            startActivity(intent);

        } else if (id == R.id.nav_SignOut) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void init() {
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        friendList.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    private void getCarList() {
        Query query = db.collection("AllCars");

        FirestoreRecyclerOptions<CarModel> response = new FirestoreRecyclerOptions.Builder<CarModel>()
                .setQuery(query, CarModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<CarModel, LandingScreen.CarHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull LandingScreen.CarHolder holder, int position, @NonNull CarModel model) {
                progressBar.setVisibility(View.GONE);
                holder.carTitle.setText(model.getCarTitle());
                holder.registrationNo.setText(model.getRegistrationNo());
                holder.uploadedBy.setText(model.getUploadedBy());
                Glide.with(getApplicationContext())
                        .load(model.getImage()).placeholder(R.drawable.carimage).into(holder.imageView);

                holder.itemView.setOnClickListener(v -> {
                    Snackbar.make(friendList, model.getRegistrationNo() + ", " + model.getBrand() + " at " + model.getModel(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Intent intent = new Intent(LandingScreen.this, CarDetail.class);
                    Bundle bucket = new Bundle();
                    bucket.putString("Brand", model.getBrand());
                    bucket.putString("Image", model.getImage());
                    bucket.putString("Title", model.getCarTitle());
                    bucket.putString("RegistrationNo", model.getRegistrationNo());
                    intent.putExtras(bucket);
                    startActivity(intent);

                });
            }

            @NonNull
            @Override
            public LandingScreen.CarHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);

                return new LandingScreen.CarHolder(view);
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
