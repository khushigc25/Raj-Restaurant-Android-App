package com.example.rajrestaurant.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddFeedbackActivity extends AppCompatActivity {
    private Toolbar feedbackToolbar;
    private TextView feedbackTitle;
    private RatingBar ratingBar;
    private EditText commentBox;
    private Button submitFeedbackButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);

        // Initialize Firestore and Auth
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        feedbackToolbar = findViewById(R.id.feedback_toolbar);
        feedbackTitle = findViewById(R.id.feedback_title);
        ratingBar = findViewById(R.id.rating_bar);
        commentBox = findViewById(R.id.comment_box);
        submitFeedbackButton = findViewById(R.id.submit_feedback_button);

        // Set up the toolbar
        setSupportActionBar(feedbackToolbar);
        feedbackToolbar.setNavigationOnClickListener(v -> finish());

        // Submit Feedback on Button Click
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        float rating = ratingBar.getRating();
        String comments = commentBox.getText().toString().trim();

        // Ensure user is authenticated
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to submit feedback.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String userEmail = auth.getCurrentUser().getEmail() != null ? auth.getCurrentUser().getEmail() : "No Email";

        // Get current date and time
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // Create feedback data map
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("userId", userId);
        feedbackData.put("userEmail", userEmail);
        feedbackData.put("rating", rating);
        feedbackData.put("comments", comments);
        feedbackData.put("date", currentDate);
        feedbackData.put("time", currentTime);

        // Store feedback in Firestore
        firestore.collection("Feedback")
                .add(feedbackData) // Use add() to automatically generate a unique ID for the feedback
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddFeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                    // Clear fields after submission
                    ratingBar.setRating(0);
                    commentBox.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddFeedbackActivity.this, "Failed to submit feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
