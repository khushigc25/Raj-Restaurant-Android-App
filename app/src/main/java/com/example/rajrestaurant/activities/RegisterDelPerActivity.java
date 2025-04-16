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

import java.util.HashMap;
import java.util.Map;

public class RegisterDelPerActivity extends AppCompatActivity {
    EditText delName, delEmail, delAge, delMobno, delPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_del_per);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Initialize EditTexts
        delName = findViewById(R.id.del_name);
        delEmail = findViewById(R.id.del_email);
        delAge = findViewById(R.id.del_age);
        delMobno = findViewById(R.id.del_phone_number);
        delPassword = findViewById(R.id.del_password);
    }

    public void addDeliveryPerson(View view) {
        // Get input values
        String userName = delName.getText().toString();
        String userEmail = delEmail.getText().toString();
        String userAge = delAge.getText().toString();
        String userMobNo = delMobno.getText().toString();
        String userPassword = delPassword.getText().toString();

        // Validate input fields
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter the name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter the email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userMobNo)) {
            Toast.makeText(this, "Enter Mobile Number!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password is too short! Please enter at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userMobNo.length() < 10) {
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the user ID from the authentication process
                            String userId = auth.getCurrentUser().getUid();

                            // Prepare data to store in Firestore
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", userName);
                            userData.put("email", userEmail);
                            userData.put("phone", userMobNo);
                            userData.put("age", userAge); // Added age
                            userData.put("role", "delivery"); // Updated role

                            // Save data to Firestore under the "DeliveryPerson" collection
                            firestore.collection("DeliveryPerson").document(userId)
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterDelPerActivity.this, "Delivery person added successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterDelPerActivity.this, AdminMainActivity.class));
                                                finish(); // Close this activity
                                            } else {
                                                Toast.makeText(RegisterDelPerActivity.this, "Failed to save user data!" + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterDelPerActivity.this, "Failed to create account!" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
