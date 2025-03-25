package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.OrderAdapter;
import com.example.rajrestaurant.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonMainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_person_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set up toolbar
        Toolbar myToolbar = findViewById(R.id.delivery_person_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.logout);
        myToolbar.setNavigationOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(DeliveryPersonMainActivity.this, RegistrationActivity.class));
            finish(); // Call finish() to close the current activity
        });

        // Initialize the RecyclerView and adapter
        RecyclerView recyclerView = findViewById(R.id.recycler_view_orders); // Assume you have a RecyclerView with this ID
        orderList = new ArrayList<>();
        // Pass the class of this activity to the adapter
        orderAdapter = new OrderAdapter(orderList, DeliveryPersonMainActivity.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        // Fetch assigned orders
        fetchAssignedOrders();
    }

    private void fetchAssignedOrders() {
        // Get the delivery person's ID from the authenticated user
        String deliveryPersonId = auth.getCurrentUser().getUid();

        // Fetch delivery person's details
        firestore.collection("DeliveryPerson")
                .document(deliveryPersonId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Get the delivery person's name from the document
                            String deliveryPersonName = document.getString("name"); // Ensure this field exists

                            // Now fetch the assigned orders using the delivery person's name
                            fetchAssignedOrdersByName(deliveryPersonName);
                        } else {
                            Log.d("DeliveryPersonMainActivity", "Delivery person document does not exist.");
                            Toast.makeText(this, "Delivery person document not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("DeliveryPersonMainActivity", "Error fetching delivery person details.", task.getException());
                        Toast.makeText(this, "Failed to fetch delivery person details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchAssignedOrdersByName(String deliveryPersonName) {
        // Fetch the assigned orders using the delivery person's name
        firestore.collection("DeliveryPerson")
                .document(deliveryPersonName) // Use the delivery person's name
                .collection("assignedOrders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Use toObject() and check for null values
                                Order order = document.toObject(Order.class);
                                if (order != null) {
                                    orderList.add(order);
                                    Log.d("Order", "Fetched Order: " + order.getOrderId()); // Log each fetched order
                                }
                            }
                            orderAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("DeliveryPersonMainActivity", "No assigned orders found.");
                            Toast.makeText(this, "No assigned orders found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("DeliveryPersonMainActivity", "Error getting assigned orders.", task.getException());
                        Toast.makeText(this, "Failed to fetch orders.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
