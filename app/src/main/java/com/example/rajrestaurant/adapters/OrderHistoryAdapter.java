package com.example.rajrestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.models.OrderModel;

import java.util.List;
import java.util.Map;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<OrderModel> orderList;

    public OrderHistoryAdapter(Context context, List<OrderModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_history_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        // Set order details
        holder.totalAmount.setText(String.format("₹%.2f", order.getTotalAmount()));
        holder.orderDate.setText(order.getOrderDate());
        holder.orderTime.setText(order.getOrderTime());

        // Format the order items nicely
        StringBuilder itemsBuilder = new StringBuilder("Items:\n");
        for (Map<String, Object> item : order.getItems()) {
            String productName = (String) item.get("productName");
            String quantity = (String) item.get("totalQuantity");
            Double totalPrice = ((Number) item.get("totalPrice")).doubleValue(); // Ensure price is in double

            itemsBuilder.append(String.format("- %s x %s: ₹%.2f%n", productName, quantity, totalPrice));
        }

        holder.orderItems.setText(itemsBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderItems, totalAmount, orderDate, orderTime;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItems = itemView.findViewById(R.id.order_items);
            totalAmount = itemView.findViewById(R.id.total_amount);
            orderDate = itemView.findViewById(R.id.order_date);
            orderTime = itemView.findViewById(R.id.order_time);
        }
    }
}
