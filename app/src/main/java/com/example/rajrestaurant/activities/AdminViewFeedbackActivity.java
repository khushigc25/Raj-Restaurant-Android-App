package com.example.rajrestaurant.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.adapters.FeedbackAdapter;
import com.example.rajrestaurant.models.FeedbackModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminViewFeedbackActivity extends AppCompatActivity {
    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<FeedbackModel> feedbackList;
    private FirebaseFirestore db;
    private TextView noFeedbackMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_feedback);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        feedbackRecyclerView = findViewById(R.id.feedback_recycler_view);
        noFeedbackMessage = findViewById(R.id.no_feedback_message);

        // Initialize feedback list and adapter
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);

        // Set up RecyclerView
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        // Fetch feedback from Firestore
        fetchFeedback();
    }

    private void fetchFeedback() {
        db.collection("Feedback")
                .get() // Fetching all documents in the Feedback collection
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        feedbackList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FeedbackModel feedback = document.toObject(FeedbackModel.class);
                            feedbackList.add(feedback);
                        }
                        feedbackAdapter.notifyDataSetChanged();
                        checkIfFeedbackListIsEmpty();
                    } else {
                        showErrorToast(task.getException());
                    }
                });
    }

    private void showErrorToast(Exception e) {
        Toast.makeText(AdminViewFeedbackActivity.this, "Error fetching feedback: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void checkIfFeedbackListIsEmpty() {
        if (feedbackList.isEmpty()) {
            noFeedbackMessage.setVisibility(View.VISIBLE);
            feedbackRecyclerView.setVisibility(View.GONE);
        } else {
            noFeedbackMessage.setVisibility(View.GONE);
            feedbackRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
