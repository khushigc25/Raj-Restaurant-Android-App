package com.example.rajrestaurant.adapters;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.activities.AdminViewOrdersActivity;
import com.example.rajrestaurant.activities.DeliveryPersonMainActivity;
import com.example.rajrestaurant.models.Order;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private final Class<?> activityClass; // Add a field to hold the activity class

    public OrderAdapter(List<Order> orderList, Class<?> activityClass) {
        this.orderList = orderList;
        this.activityClass = activityClass; // Initialize the activity class
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.orderAmount.setText("Total Amount: ₹" + order.getTotalAmount());
        holder.orderAddress.setText("Address: " + order.getAddress());
        holder.orderDate.setText("Order Date: " + order.getOrderDate());
        holder.orderTime.setText("Order Time: " + order.getOrderTime());
        holder.orderUserName.setText("Customer Name: " + order.getUserName());
        holder.orderUserMobileNo.setText("Customer Mobile Number: " + order.getUserMobile());

        // Format the order items nicely
        StringBuilder itemsBuilder = new StringBuilder("Items:\n");
        for (Map<String, Object> item : order.getItems()) {
            String productName = (String) item.get("productName");
            String quantity = (String) item.get("totalQuantity");
            Double totalPrice = ((Number) item.get("totalPrice")).doubleValue();

            itemsBuilder.append("- ").append(productName)
                    .append(" x ").append(quantity)
                    .append(": ₹").append(totalPrice)
                    .append("\n");
        }

        holder.orderItems.setText(itemsBuilder.toString());

        // Control visibility of the button based on the activity
        if (activityClass == DeliveryPersonMainActivity.class) {
            holder.assignDeliveryPersonButton.setVisibility(View.GONE); // Hide button for delivery person
        } else if (activityClass == AdminViewOrdersActivity.class) {
            holder.assignDeliveryPersonButton.setVisibility(View.VISIBLE); // Show button for admin
        }

        // Set OnClickListener for the button
        holder.assignDeliveryPersonButton.setOnClickListener(v -> {
            fetchDeliveryPersonsAndShowDialog(holder.assignDeliveryPersonButton, order);
        });
    }

    private void fetchDeliveryPersonsAndShowDialog(Button buttonAssignDelivery, Order order) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DeliveryPerson")
                .whereEqualTo("role", "delivery") // Fetch only delivery persons
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> deliveryPersons = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            if (name != null) {
                                deliveryPersons.add(name);
                            }
                        }

                        if (!deliveryPersons.isEmpty()) {
                            showDeliveryPersonDialog(buttonAssignDelivery, deliveryPersons.toArray(new String[0]), order);
                        } else {
                            Toast.makeText(buttonAssignDelivery.getContext(), "No delivery persons found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(buttonAssignDelivery.getContext(), "Error fetching delivery persons.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeliveryPersonDialog(Button buttonAssignDelivery, String[] deliveryPersons, Order order) {
        final boolean[] checkedItems = new boolean[deliveryPersons.length];

        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(buttonAssignDelivery.getContext());
        builder.setTitle("Select a Delivery Person");

        builder.setSingleChoiceItems(deliveryPersons, -1, (dialog, which) -> {
            // Optionally handle item selection
            for (int i = 0; i < checkedItems.length; i++) {
                checkedItems[i] = i == which; // Only the selected item is checked
            }
        });

        // Set up the Submit button
        builder.setPositiveButton("Submit", (dialogInterface, i) -> {
            int selectedPosition = -1; // Default to no selection

            for (int j = 0; j < checkedItems.length; j++) {
                if (checkedItems[j]) {
                    selectedPosition = j; // Find the selected position
                    break;
                }
            }

            if (selectedPosition != -1) {
                String selectedPerson = deliveryPersons[selectedPosition];
                // Save order details to Firestore
                saveOrderToDeliveryPerson(order, selectedPerson);

                // Notify the user
                Toast.makeText(buttonAssignDelivery.getContext(), "Assigned to: " + selectedPerson, Toast.LENGTH_SHORT).show();

                // Hide the Assign Delivery button after selection
                buttonAssignDelivery.setVisibility(View.GONE);
            }
        });

        // Set the negative button to cancel the dialog
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        // Show the dialog
        builder.show();
    }

    private void saveOrderToDeliveryPerson(Order order, String deliveryPerson) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map for the order details
        Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("totalAmount", order.getTotalAmount());
        orderDetails.put("address", order.getAddress());
        orderDetails.put("items", order.getItems()); // Assuming items is a list or map
        orderDetails.put("userName", order.getUserName());
        orderDetails.put("userMobile", order.getUserMobile());

        orderDetails.put("orderId", order.getOrderId());
        orderDetails.put("orderDate", order.getOrderDate());
        orderDetails.put("orderTime", order.getOrderTime());


        // Save to Firestore under the respective delivery person
        db.collection("DeliveryPerson")
                .document(deliveryPerson) // Document name for the delivery person
                .collection("assignedOrders")
                .document(order.getOrderId()) // Use order ID as document ID
                .set(orderDetails)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the order to the delivery person's document
                    Log.d("OrderAdapter", "Order assigned successfully to " + deliveryPerson);
                })
                .addOnFailureListener(e -> {
                    Log.w("OrderAdapter", "Error assigning order", e);
                });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderItems, orderAmount, orderAddress;
        TextView orderDate, orderTime;
        TextView orderUserName, orderUserMobileNo;
        Button assignDeliveryPersonButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.admin_order_id);
            orderItems = itemView.findViewById(R.id.admin_order_items);
            orderAmount = itemView.findViewById(R.id.admin_order_amount);
            orderAddress = itemView.findViewById(R.id.admin_order_address);
            orderDate = itemView.findViewById(R.id.admin_order_date);
            orderTime = itemView.findViewById(R.id.admin_order_time);
            orderUserName = itemView.findViewById(R.id.admin_user_name);
            orderUserMobileNo = itemView.findViewById(R.id.admin_user_mobile);
            assignDeliveryPersonButton = itemView.findViewById(R.id.assign_delivery_person);
        }
    }
}
