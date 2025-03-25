package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
    }

    public void signIn(View view) {
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password is too short! Please enter at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User is authenticated with Firebase, now check their role
                            checkUserRole(userEmail);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to check user role based on email field in Firestore
    private void checkUserRole(String email) {
        // Check the 'DeliveryPerson' collection for the email
        firestore.collection("DeliveryPerson")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // If a matching document is found in 'DeliveryPerson', the user is a delivery person
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful as Delivery Person!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, DeliveryPersonMainActivity.class));
                                    return;
                                }
                            }
                        } else {
                            // If not found in 'DeliveryPerson', check for customer or admin role
                            checkCustomerRole(email);
                        }
                    }
                });
    }

    // Check if user is in 'Customer' collection
    private void checkCustomerRole(String email) {
        firestore.collection("Customer")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // If a matching document is found in 'Customer', the user is a customer
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful as Customer!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    return;
                                }
                            }
                        } else {
                            // If not found in 'Customer', check 'Admin' role
                            checkAdminRole(email);
                        }
                    }
                });
    }

    // Check if user is in 'Admin' collection
    private void checkAdminRole(String email) {
        firestore.collection("Admin")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // If a matching document is found in 'Admin', the user is an admin
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Toast.makeText(LoginActivity.this, "Login Successful as Admin!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                    return;
                                }
                            }
                        } else {
                            // If the user doesn't exist in any collection
                            Toast.makeText(LoginActivity.this, "No role assigned or user does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signUp(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
