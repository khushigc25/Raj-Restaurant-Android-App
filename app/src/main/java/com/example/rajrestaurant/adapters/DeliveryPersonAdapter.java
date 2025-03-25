package com.example.rajrestaurant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.models.DeliveryPersonModel;

import java.util.List;

public class DeliveryPersonAdapter extends RecyclerView.Adapter<DeliveryPersonAdapter.ViewHolder> {
    private List<DeliveryPersonModel> deliveryPersons;

    public DeliveryPersonAdapter(List<DeliveryPersonModel> deliveryPersons) {
        this.deliveryPersons = deliveryPersons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_person_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryPersonModel deliveryPerson = deliveryPersons.get(position);
        holder.nameTextView.setText(deliveryPerson.getName());
        holder.emailTextView.setText(deliveryPerson.getEmail());
        holder.phoneTextView.setText(deliveryPerson.getPhone());
        holder.ageTextView.setText(deliveryPerson.getAge());
    }

    @Override
    public int getItemCount() {
        return deliveryPersons.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView phoneTextView;
        TextView ageTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.delivery_person_name);
            emailTextView = itemView.findViewById(R.id.delivery_person_email);
            phoneTextView = itemView.findViewById(R.id.delivery_person_phone);
            ageTextView = itemView.findViewById(R.id.delivery_person_age);
        }
    }
}
