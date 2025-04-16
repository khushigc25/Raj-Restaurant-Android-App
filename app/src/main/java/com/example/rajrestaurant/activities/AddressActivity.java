package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.AddressAdapter;
import com.example.rajrestaurant.models.AddressModel;
import com.example.rajrestaurant.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    Button addAddress, placeOrder;
    RecyclerView recyclerView;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
    List<MyCartModel> cartModelList;  // List to hold cart items
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String selectedAddress = ""; // Selected address

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Set up toolbar
        Toolbar myToolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setNavigationOnClickListener(v -> finish());

        // Get cart model list from Intent
        cartModelList = (List<MyCartModel>) getIntent().getSerializableExtra("cartList");
        if (cartModelList == null) {
            cartModelList = new ArrayList<>(); // Initialize if not passed
        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        recyclerView = findViewById(R.id.address_recycler);
        placeOrder = findViewById(R.id.place_order);
        addAddress = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(), addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        // Fetch addresses from Firestore
        fetchAddresses();

        // Add new address
        addAddress.setOnClickListener(v -> startActivity(new Intent(AddressActivity.this, AddAddressActivity.class)));

        // Handle place order button click
        placeOrder.setOnClickListener(v -> {
            if (selectedAddress.isEmpty()) {
                Toast.makeText(AddressActivity.this, "Please select an address", Toast.LENGTH_SHORT).show();
            } else {
                double totalAmount = calculateTotalAmount();
                placeOrder(totalAmount, selectedAddress);
            }
        });
    }

    private void fetchAddresses() {
        firestore.collection("CurrentUser")
                .document(auth.getCurrentUser().getUid())
                .collection("Address").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            AddressModel addressModel = doc.toObject(AddressModel.class);
                            if (addressModel != null) {
                                addressModelList.add(addressModel);
                            }
                        }
                        addressAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AddressActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Implement the selected address method
    @Override
    public void setAddress(String address) {
        selectedAddress = address;
    }

    private double calculateTotalAmount() {
        double totalAmount = 0.0;
        for (MyCartModel myCartModel : cartModelList) {
            totalAmount += myCartModel.getTotalPrice();
        }
        return totalAmount;
    }

    private void placeOrder(double totalAmount, String address) {
        String saveOrderTime, saveOrderDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveOrderDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveOrderTime = currentTime.format(calForDate.getTime());

        String userId = auth.getCurrentUser().getUid();
        String orderId = firestore.collection("Orders").document().getId(); // Generate a unique order ID

        Timestamp orderTimestamp = Timestamp.now();

        // Fetch user details
        firestore.collection("Customer").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot userDoc = task.getResult();
                String userName = userDoc.getString("name"); // Assuming your field for name is "name"
                String userMobile = userDoc.getString("phone"); // Assuming your field for mobile is "mobile"

                // Create an order data map
                Map<String, Object> orderData = new HashMap<>();
                orderData.put("orderId", orderId);
                orderData.put("userId", userId);
                orderData.put("userName", userName); // Add user's name
                orderData.put("userMobile", userMobile); // Add user's mobile number
                orderData.put("address", address); // Store the selected address
                orderData.put("totalAmount", totalAmount);
                orderData.put("items", cartModelList); // Store cart items
                orderData.put("orderTime", saveOrderTime);
                orderData.put("orderDate", saveOrderDate);
                orderData.put("timestamp", orderTimestamp); // Add the timestamp for the order
                
                // Save order to Firestore
                firestore.collection("Orders").document(orderId).set(orderData).addOnCompleteListener(orderTask -> {
                    if (orderTask.isSuccessful()) {
                        Toast.makeText(AddressActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        clearCart(); // Optionally, clear the cart here
                        Intent intent = new Intent(AddressActivity.this, TrackOrderActivity.class);
                        intent.putExtra("orderId", orderId); // Pass the order ID to TrackOrderActivity
                        startActivity(intent);
                        finish(); // Optionally close AddressActivity
                    } else {
                        Toast.makeText(AddressActivity.this, "Failed to place order", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AddressActivity.this, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void clearCart() {
        // Clear cart logic, if needed
        // You can also implement the same clearing logic as in your CartActivity
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                                    .collection("User").document(doc.getId()).delete();
                        }
                    }
                });
    }
}
