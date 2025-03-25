package com.example.rajrestaurant.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rajrestaurant.R;
import com.example.rajrestaurant.models.FeedbackModel;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {
    private List<FeedbackModel> feedbackList;

    public FeedbackAdapter(List<FeedbackModel> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        FeedbackModel feedback = feedbackList.get(position);

        // Make sure feedback is not null and has data
        if (feedback != null) {
            holder.commentsTextView.setText(feedback.getComments());
            holder.dateTextView.setText(feedback.getDate());
            holder.ratingTextView.setText(String.valueOf(feedback.getRating()));
            holder.timeTextView.setText(feedback.getTime());
            holder.emailTextView.setText(feedback.getUserEmail());
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0; // Check for null
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentsTextView, dateTextView, ratingTextView, timeTextView, emailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentsTextView = itemView.findViewById(R.id.feedback_comments);
            dateTextView = itemView.findViewById(R.id.feedback_date);
            ratingTextView = itemView.findViewById(R.id.feedback_rating);
            timeTextView = itemView.findViewById(R.id.feedback_time);
            emailTextView = itemView.findViewById(R.id.feedback_user_email);
        }
    }
}
