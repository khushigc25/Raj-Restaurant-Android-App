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

public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, mobno, password;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobno = findViewById(R.id.mobno);
        password = findViewById(R.id.password);

        // Removed auto-redirect based on the current user here, handled in SplashScreenActivity
    }

    public void signUp(View view) {
        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userMobNo = mobno.getText().toString();
        String userPassword = password.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userMobNo)) {
            Toast.makeText(this, "Enter your Mobile Number!", Toast.LENGTH_SHORT).show();
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
        if (userMobNo.length() < 10) {
            Toast.makeText(this, "Please enter a valid mobile number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
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
                            userData.put("role", "customer"); // Assuming 'customer' as default role during registration

                            // Save data to Firestore under the "Customer" collection
                            firestore.collection("Customer").document(userId)
                                    .set(userData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Your account has been created successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class)); // Redirect to customer MainActivity
                                                finish();
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, "Failed to save user data! " + task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Failed to create account! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)); // Redirect to LoginActivity for sign-in
    }
}
