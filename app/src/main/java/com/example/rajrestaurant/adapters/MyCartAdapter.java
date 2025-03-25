package com.example.rajrestaurant.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rajrestaurant.R;
import com.example.rajrestaurant.activities.CartActivity;
import com.example.rajrestaurant.models.MyCartModel;
import com.example.rajrestaurant.models.ShowAllModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private Context context;
    private List<MyCartModel> list;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel currentItem = list.get(position);

        //holder.date.setText(currentItem.getCurrentDate());
        //holder.time.setText(currentItem.getCurrentTime());
        holder.price.setText("Rs. " + currentItem.getProductPrice());
        holder.name.setText(currentItem.getProductName());
        holder.totalPrice.setText("Rs. " + currentItem.getTotalPrice());
        holder.totalQuantity.setText(String.valueOf(currentItem.getTotalQuantity())); // Corrected this
        Glide.with(context).load(currentItem.getProduct_image()).into(holder.product_image);

        holder.deleteItem.setOnClickListener(v -> deleteItemFromCart(currentItem.getDocumentId(), position));

        holder.addButton.setOnClickListener(v -> {
            int quantity = Integer.parseInt(currentItem.getTotalQuantity()) + 1;
            updateCartItem(currentItem, quantity);
            holder.totalQuantity.setText(String.valueOf(currentItem.getTotalQuantity())); // Corrected this
        });

        holder.removeButton.setOnClickListener(v -> {
            if (Integer.parseInt(currentItem.getTotalQuantity()) > 1) {
                int quantity = Integer.parseInt(currentItem.getTotalQuantity()) - 1;
                updateCartItem(currentItem, quantity);
                holder.totalQuantity.setText(String.valueOf(currentItem.getTotalQuantity())); // Corrected this
            }else{
                deleteItemFromCart(currentItem.getDocumentId(), position);
            }
        });
    }

    private void updateCartItem(MyCartModel item, int newQuantity) {
        // Convert newQuantity to String for Firestore
        String newQuantityString = String.valueOf(newQuantity);
        // Calculate new total price
        int newTotalPrice = newQuantity * Integer.parseInt(item.getProductPrice());

        FirebaseFirestore.getInstance().collection("AddToCart")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("User")
                .document(item.getDocumentId())
                .update("TotalQuantity", newQuantityString, "totalPrice", newTotalPrice)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the model with the new values
                        item.setTotalQuantity(newQuantityString); // Update the quantity in the model
                        item.setTotalPrice(newTotalPrice); // Update the total price in the model
                        notifyDataSetChanged(); // Notify the adapter to refresh the UI

                        // Notify the activity to recalculate the total amount
                        if (context instanceof CartActivity) {
                            ((CartActivity) context).calculateTotalAmount(list);
                        }
                    } else {
                        // Log or handle error
                        Log.e("CartAdapter", "Error updating cart item", task.getException());
                        Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, date, time, totalQuantity, totalPrice;
        ImageView deleteItem, removeButton, addButton, product_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            product_image = itemView.findViewById(R.id.product_image);
            //date = itemView.findViewById(R.id.current_date);
            //time = itemView.findViewById(R.id.current_time);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            removeButton = itemView.findViewById(R.id.remove_item);
            addButton = itemView.findViewById(R.id.add_item);
            deleteItem = itemView.findViewById(R.id.delete);
        }
    }

    private void deleteItemFromCart(String documentId, int position) {
        firestore.collection("AddToCart")
                .document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(documentId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                        ((CartActivity) context).calculateTotalAmount(list);
                    } else {
                        Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
