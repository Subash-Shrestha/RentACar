package com.subash.rentacar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    List<AuthUI.IdpConfig> providers;
    FirebaseFirestore db;

    private static final int RC_SIGN_IN = 7117;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();


        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()

        );
        showSignInOptions();
    }

    private void showSignInOptions() {

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(true)
                        .setTheme(R.style.CustomTheme)
                        .build(), RC_SIGN_IN
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                addUserIntoUserDB(user);
                Intent startMainActivity = new Intent(LoginActivity.this, LandingScreen.class);
                startActivity(startMainActivity);
                // ...
            } else {
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Canceled By User", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addUserIntoUserDB(FirebaseUser user) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("Name", Objects.requireNonNull(user.getDisplayName()));
        newUser.put("Email", Objects.requireNonNull(user.getEmail()));
        newUser.put("UID", Objects.requireNonNull(user.getUid()));
        newUser.put("isAdmin", false);
        db.collection("Users").document(user.getUid()).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(LoginActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "User registration Failed" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
