package com.example.rajrestaurant.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rajrestaurant.R;

public class AssignDeliveryPersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_delivery_person);

        // Retrieve the orderId from the intent
        String orderId = getIntent().getStringExtra("orderId");

        // You can now use this orderId to perform the logic of assigning a delivery person
        // For now, we'll just show a Toast message
        Toast.makeText(this, "Assign delivery person for Order ID: " + orderId, Toast.LENGTH_LONG).show();

        // Add logic to fetch available delivery persons and assign one to this order
    }
}
