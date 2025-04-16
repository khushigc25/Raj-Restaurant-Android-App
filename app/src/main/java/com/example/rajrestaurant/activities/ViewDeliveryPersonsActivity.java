package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.DeliveryPersonAdapter;
import com.example.rajrestaurant.models.DeliveryPersonModel;
import com.example.rajrestaurant.models.DeliveryPersonModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewDeliveryPersonsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DeliveryPersonAdapter deliveryPersonAdapter;
    private List<DeliveryPersonModel> deliveryPersonList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_delivery_persons);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        deliveryPersonList = new ArrayList<>();
        deliveryPersonAdapter = new DeliveryPersonAdapter(deliveryPersonList);
        recyclerView.setAdapter(deliveryPersonAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDeliveryPersons();
    }

    private void fetchDeliveryPersons() {
        firestore.collection("DeliveryPerson")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ViewDeliveryPersonsActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        deliveryPersonList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            DeliveryPersonModel deliveryPerson = doc.toObject(DeliveryPersonModel.class);
                            deliveryPersonList.add(deliveryPerson);
                        }
                        deliveryPersonAdapter.notifyDataSetChanged();
                    }
                });
    }
}