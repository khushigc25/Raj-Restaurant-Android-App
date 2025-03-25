package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.OrderAdapter;
import com.example.rajrestaurant.models.Order; // Make sure this model class exists
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminViewOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private ProgressBar progressBar;
    private TextView noOrdersText;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orders);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up toolbar
        Toolbar myToolbar = findViewById(R.id.admin_view_order_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.back);
        myToolbar.setNavigationOnClickListener(v -> finish());

        // Initialize UI components
        recyclerViewOrders = findViewById(R.id.recycler_view_orders);
        progressBar = findViewById(R.id.admin_orders_progress_bar);
        noOrdersText = findViewById(R.id.admin_no_orders_text);

        // Initialize RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList();
        // Pass the class of this activity to the adapter
        orderAdapter = new OrderAdapter(orderList, AdminViewOrdersActivity.class);
        recyclerViewOrders.setAdapter(orderAdapter);

        // Fetch orders from Firestore
        fetchOrders();
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);

        // Fetch orders and order them by orderDate and orderTime in descending order
        firestore.collection("Orders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            orderList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String orderId = document.getId();
                                String address = document.getString("address");
                                double totalAmount = document.getDouble("totalAmount");
                                List<Map<String, Object>> items = (List<Map<String, Object>>) document.get("items");
                                String orderDate = document.getString("orderDate");
                                String orderTime = document.getString("orderTime");
                                String userName = document.getString("userName");
                                String userMobile = document.getString("userMobile");
                                String orderTimestamp = document.getString("orderTimestamp");

                                // Create an Order object and add to the list
                                Order order = new Order(orderId, items, totalAmount, address, orderDate, orderTime, userName, userMobile, orderTimestamp);
                                orderList.add(order);
                            }

                            if (orderList.isEmpty()) {
                                noOrdersText.setVisibility(View.VISIBLE);
                            } else {
                                noOrdersText.setVisibility(View.GONE);
                            }

                            orderAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AdminViewOrdersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
