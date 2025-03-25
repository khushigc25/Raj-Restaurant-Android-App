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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.MyCartAdapter;
import com.example.rajrestaurant.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    Button buyNow;
    TextView subTotal, grandTotal;
    RecyclerView recyclerView;
    List<MyCartModel> cartModelList;
    MyCartAdapter cartAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Toolbar myToolbar = findViewById(R.id.my_cart_toolbar);
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

        buyNow = findViewById(R.id.buy_now);
        subTotal = findViewById(R.id.sub_total);
        grandTotal = findViewById(R.id.grand_total);

        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        // Check if the user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(CartActivity.this, "User not authenticated!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not authenticated
            return;
        }

        // Load cart items from Firestore
        loadCartItems();

        // Set up buy now button click listener
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModelList == null || cartModelList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Cart is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double placeOrderAmt = 0.0;
                for (MyCartModel myCartModel : cartModelList) {
                    placeOrderAmt += myCartModel.getTotalPrice();
                }

                Log.d("CartActivity", "Total Amount: " + placeOrderAmt);

                if (placeOrderAmt == 0) {
                    Toast.makeText(CartActivity.this, "Cart is Empty!", Toast.LENGTH_SHORT).show();
                } else if (placeOrderAmt < 100) {
                    Toast.makeText(CartActivity.this, "Minimum order value is Rs. 100", Toast.LENGTH_SHORT).show();
                } else {
                    // Navigate to AddressActivity and pass the cart items
                    Intent intent = new Intent(CartActivity.this, AddressActivity.class);
                    intent.putExtra("cartList", new ArrayList<>(cartModelList)); // Pass the cart list
                    startActivity(intent);
                }
            }
        });

    }

    private void loadCartItems() {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                String documentId = doc.getId();
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                if (myCartModel != null) {
                                    myCartModel.setDocumentId(documentId);
                                    cartModelList.add(myCartModel);
                                }
                            }
                            cartAdapter.notifyDataSetChanged();
                            calculateTotalAmount(cartModelList);
                        } else {
                            Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                            finish(); // Optionally, close the activity if data is crucial
                        }
                    }
                });
    }

    public void calculateTotalAmount(List<MyCartModel> cartModelList) {
        double totalAmount = 0.0;

        if (cartModelList.isEmpty()) {
            subTotal.setText("₹ 0");
            grandTotal.setText("₹ 0");
        } else {
            for (MyCartModel myCartModel : cartModelList) {
                totalAmount += myCartModel.getTotalPrice();
            }
            subTotal.setText("₹ " + totalAmount);
            grandTotal.setText("₹ " + totalAmount);
        }
    }

//    private void placeOrder(double totalAmount) {
//        // Get the user ID
//        String userId = auth.getCurrentUser().getUid();
//
//        // Create an order object (assuming you have a model class for orders, if not, you can use a Map)
//        final String orderId = firestore.collection("Orders").document().getId(); // Generate a unique order ID
//
//        // Create an order data map
//        Map<String, Object> orderData = new HashMap<>();
//        orderData.put("orderId", orderId);
//        orderData.put("userId", userId);
//        orderData.put("totalAmount", totalAmount);
//        orderData.put("status", "Placed");
//        orderData.put("orderTimestamp", System.currentTimeMillis());
//
//        // Add the order to the Orders collection
//        firestore.collection("Orders").document(orderId).set(orderData)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            // Now add each cart item to the order
//                            for (MyCartModel cartModel : cartModelList) {
//                                Map<String, Object> cartItemData = new HashMap<>();
//                                cartItemData.put("productId", cartModel.getDocumentId());
//                                cartItemData.put("productName", cartModel.getProductName());
//                                cartItemData.put("productPrice", cartModel.getProductPrice());
//                                cartItemData.put("quantity", cartModel.getTotalQuantity());
//                                cartItemData.put("totalPrice", cartModel.getTotalPrice());
//
//                                // Save each cart item under the order document
//                                firestore.collection("Orders").document(orderId)
//                                        .collection("OrderItems").add(cartItemData);
//                            }
//
//                            // Clear the cart after placing the order
//                            clearCart();
//
//                            // Notify the user
//                            Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
//
//                            // Redirect to another activity (e.g., MainActivity)
//                            startActivity(new Intent(CartActivity.this, MainActivity.class));
//                            finish();
//                        } else {
//                            Toast.makeText(CartActivity.this, "Failed to place the order", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//    private void clearCart() {
//        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
//                .collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
//                                // Delete each document in the user's cart
//                                firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
//                                        .collection("User").document(doc.getId()).delete();
//                            }
//
//                            // Clear the local cart model list
//                            cartModelList.clear();
//                            cartAdapter.notifyDataSetChanged();
//
//                            // Update UI
//                            calculateTotalAmount(cartModelList);
//                        }
//                    }
//                });
//    }

}
