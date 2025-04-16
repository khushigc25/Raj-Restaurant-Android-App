package com.example.rajrestaurant.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.rajrestaurant.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddFoodItemActivity extends AppCompatActivity {

    private EditText foodNameEditText;
    private EditText descriptionEditText;
    private EditText imgUrlEditText;
    private EditText priceEditText;
    private EditText typeEditText;
    private EditText vnEditText;
    private Button addFoodItemButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        foodNameEditText = findViewById(R.id.food_name);
        descriptionEditText = findViewById(R.id.description);
        imgUrlEditText = findViewById(R.id.img_url);
        priceEditText = findViewById(R.id.price);
        typeEditText = findViewById(R.id.type);
        vnEditText = findViewById(R.id.vn);
        addFoodItemButton = findViewById(R.id.add_food_item_button);

        // Set click listener for the button
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodItem();
            }
        });
    }

    private void addFoodItem() {
        // Get data from EditText fields
        String foodName = foodNameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String imgUrl = imgUrlEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String type = typeEditText.getText().toString().trim();
        String vn = vnEditText.getText().toString().trim();

        // Validate input
        if (foodName.isEmpty() || description.isEmpty() || imgUrl.isEmpty() || priceString.isEmpty() || type.isEmpty() || vn.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse price as a number
        double price;
        try {
            price = Double.parseDouble(priceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a food item map
        Map<String, Object> foodItem = new HashMap<>();
        foodItem.put("name", foodName);
        foodItem.put("description", description);
        foodItem.put("img_url", imgUrl);
        foodItem.put("price", price); // Store price as a number
        foodItem.put("type", type);
        foodItem.put("vn", vn);

        // Add the food item to the Firestore database
        db.collection("AllProducts").add(foodItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Food item added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields(); // Optionally clear the fields after submission
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding food item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        foodNameEditText.setText("");
        descriptionEditText.setText("");
        imgUrlEditText.setText("");
        priceEditText.setText("");
        typeEditText.setText("");
        vnEditText.setText("");
    }
}
