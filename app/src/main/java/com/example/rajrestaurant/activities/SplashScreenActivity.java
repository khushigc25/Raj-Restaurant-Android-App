package com.example.rajrestaurant.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.rajrestaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser() != null) {
                    // User is already logged in, check their role
                    String userId = auth.getCurrentUser().getUid();

                    // Check if the user is an Admin
                    firestore.collection("Admin").document(userId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    startActivity(new Intent(SplashScreenActivity.this, AdminMainActivity.class));
                                    finish();
                                } else {
                                    // Check if the user is a Customer
                                    firestore.collection("Customer").document(userId)
                                            .get()
                                            .addOnSuccessListener(customerSnapshot -> {
                                                if (customerSnapshot.exists()) {
                                                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class)); // MainActivity for customer
                                                    finish();
                                                } else {
                                                    // Check if the user is a DeliveryPerson
                                                    firestore.collection("DeliveryPerson").document(userId)
                                                            .get()
                                                            .addOnSuccessListener(deliverySnapshot -> {
                                                                if (deliverySnapshot.exists()) {
                                                                    startActivity(new Intent(SplashScreenActivity.this, DeliveryPersonMainActivity.class));
                                                                    finish();
                                                                } else {
                                                                    // User has no role, redirect to registration
                                                                    startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                                                                    finish();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Error in checking role, redirect to registration
                                startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                                finish();
                            });
                } else {
                    // User not logged in, redirect to registration
                    startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                    finish();
                }
            }
        }, 2000); // Delay for splash screen
    }
}
