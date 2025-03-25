package com.example.rajrestaurant.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.rajrestaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminMainActivity extends AppCompatActivity {

    // Declare ImageView variables
    ImageView viewOrders;
    ImageView addDeliveryPerson;
    ImageView addFoodItems;
    ImageView viewSummary;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Set up toolbar
        Toolbar myToolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.logout);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(AdminMainActivity.this, RegistrationActivity.class));
            }
        });

//        myToolbar.setNavigationIcon(R.drawable.menu);

        // Initialize ImageViews
        viewOrders = findViewById(R.id.view_orders);
        addDeliveryPerson = findViewById(R.id.add_delivery_person);
        addFoodItems = findViewById(R.id.add_food_items);
        viewSummary = findViewById(R.id.view_summary);
    }

    // Method to navigate to ViewOrdersActivity
    public void navigateToViewOrders(View view) {
        Intent intent = new Intent(this, AdminViewOrdersActivity.class);
        startActivity(intent);
    }

    // Method to navigate to AddDeliveryPersonActivity
    public void navigateToAddDeliveryPerson(View view) {
        Intent intent = new Intent(this, RegisterDelPerActivity.class);
        startActivity(intent);
    }

    // Method to navigate to AddFoodItemActivity
    public void navigateToAddFoodItems(View view) {
        Intent intent = new Intent(this, AddFoodItemActivity.class);
        startActivity(intent);
    }

    // Method to navigate to ViewSummaryActivity
    public void navigateToViewSummary(View view) {
        Intent intent = new Intent(this, AdminSummaryActivity.class);
        startActivity(intent);
    }

    public void navigateToViewDeliveryPersons(View view) {
        Intent intent = new Intent(this, ViewDeliveryPersonsActivity.class);
        startActivity(intent);
    }


    public void navigateToViewFeedback(View view) {
        Intent intent = new Intent(this, AdminViewFeedbackActivity.class);
        startActivity(intent);
    }

}
