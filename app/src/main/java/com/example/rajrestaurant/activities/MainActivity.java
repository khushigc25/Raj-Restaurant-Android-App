package com.example.rajrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Fragment homeFragment;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();  // Initialize Firestore

        Toolbar myToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        myToolbar.setNavigationIcon(R.drawable.menu);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);

        // Drawer layout
        drawerLayout = findViewById(R.id.drawerLayout);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        // Navigation view
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navMenu) {
                    startActivity(new Intent(MainActivity.this, ShowAllActivity.class));
                } else if (itemId == R.id.navCart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                } else if (itemId == R.id.navAddress) {
                    startActivity(new Intent(MainActivity.this, ViewAddressActivity.class));
                } else if (itemId == R.id.navAddAddress) {
                    startActivity(new Intent(MainActivity.this, AddAddressActivity.class));
                } else if (itemId == R.id.navFeedback) {
                    startActivity(new Intent(MainActivity.this, AddFeedbackActivity.class));
                } else if (itemId == R.id.navOrderHistory) {
                    startActivity(new Intent(MainActivity.this, OrderHistoryActivity.class));
                } else if (itemId == R.id.navTerms) {
                    startActivity(new Intent(MainActivity.this, TermsAndConditionsActivity.class));
                } else if (itemId == R.id.navLogout) {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                }

                return false;
            }
        });

        // Setting name and email
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = headerView.findViewById(R.id.navUserName);
        TextView textUserEmail = headerView.findViewById(R.id.navUserEmail);

        // Fetch and display user data
        fetchUserData(textUserName, textUserEmail);
    }

    private void fetchUserData(TextView textUserName, TextView textUserEmail) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Set the user's email from Firebase Authentication
            textUserEmail.setText(currentUser.getEmail());

            // Fetch the user's name from Firestore using the user's UID
            String userId = currentUser.getUid();
            firestore.collection("Customer").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("name");  // Assuming 'name' is stored in Firestore
                                textUserName.setText(userName);
                            } else {
                                Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadFragment(Fragment homeFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, homeFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
        } else if (id == R.id.menu_my_cart) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        }
        return true;
    }
}
