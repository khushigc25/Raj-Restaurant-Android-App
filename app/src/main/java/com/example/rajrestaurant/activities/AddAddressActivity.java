package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {
    EditText address_line1, address_line2, pincode;
    Button addAddressBtn;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // toolbar
        Toolbar myToolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.back);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        address_line1 = findViewById(R.id.address_line1);
        address_line2 = findViewById(R.id.address_line2);
        pincode = findViewById(R.id.pincode);

        addAddressBtn = findViewById(R.id.ad_add_address);

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAddressLine1 = address_line1.getText().toString();
                String userAddressLine2 = address_line2.getText().toString();
                String userPincode = pincode.getText().toString();

                String final_address = "";

                if (!userAddressLine1.isEmpty()) {
                    final_address += userAddressLine1;
                }
                if (!userAddressLine2.isEmpty()) {
                    final_address += userAddressLine2;
                }
                if (!userPincode.isEmpty()) {
                    final_address += userPincode;
                }


                if (!userAddressLine1.isEmpty() && !userAddressLine2.isEmpty() && !userPincode.isEmpty()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("userAddress", final_address);

                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                            .collection("Address").add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddAddressActivity.this, "Address Added Successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AddAddressActivity.this,MainActivity.class));
                                        finish();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(AddAddressActivity.this, "Kindly fill all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
