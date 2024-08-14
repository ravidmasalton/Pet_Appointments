package com.example.vetappointment.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetappointment.Adapter.ReviewAdapter;
import com.example.vetappointment.Listeners.ListenerGetAllReviewFromDB;
import com.example.vetappointment.Models.FireBaseManager;
import com.example.vetappointment.Models.Review;
import com.example.vetappointment.Models.User;
import com.example.vetappointment.databinding.FragmentReviewBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class ReviewFragment extends Fragment {

    private FragmentReviewBinding binding;
    private RecyclerView recycler_view_reviews;
    private ArrayList<Review> reviews;
    private MaterialTextView titleTextView;
    private RatingBar ratingBar;
    private MaterialAutoCompleteTextView reviewTextView;
    private MaterialTextView AllReviewsTextView;
    private ExtendedFloatingActionButton addReviewButton;
    private MaterialTextView overallRatingTextView;
    private FireBaseManager fireBaseManager = FireBaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ReviewAdapter adapterReview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        reviews = new ArrayList<>();
        findView();
        isVetLoggedIn();
        setupUI(root); // Setup touch listener to hide the keyboard
        getAllReviews();
        getUserAndInitView();
        return root;
    }

    private void findView() {
        recycler_view_reviews = binding.reviewsRecyclerView;
        ratingBar = binding.ratingBar;
        reviewTextView = binding.reviewText;
        addReviewButton = binding.submitReviewButton;
        overallRatingTextView = binding.overallRatingTextView;
        AllReviewsTextView = binding.AllReviewsTextView;
        titleTextView = binding.titleTextView;
    }

    private void updateAdapterAndRecyclerView() { // Update the adapter and recycler view with the new data from the database
        adapterReview = new ReviewAdapter(getContext(), reviews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler_view_reviews.setLayoutManager(linearLayoutManager);
        recycler_view_reviews.setAdapter(adapterReview);
    }

    private void calculateAverageRating() { // Calculate and display the average rating from the reviews
        float totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }
        float averageRating = totalRating / reviews.size();
        overallRatingTextView.setText("Overall Rating: " + String.format("%.1f", averageRating));
    }

    private void getAllReviews() { // Get all reviews from the database and update the adapter and recycler view
        fireBaseManager.getAllReviews(new ListenerGetAllReviewFromDB() {
            @Override
            public void onReviewFromDBLoadSuccess(ArrayList<Review> appointments) {
                if (appointments != null) {
                    reviews.clear();
                    reviews.addAll(appointments);
                    updateAdapterAndRecyclerView();
                    calculateAverageRating();
                }
            }

            @Override
            public void onReviewFromDBLoadFailed(String message) {
                // Handle error
            }
        });
    }

    private void initViews(User user) { // Initialize the views and set up the click listener for the submit button
        addReviewButton.setOnClickListener(v -> {
            String userName = user.getName();
            String reviewText = reviewTextView.getText().toString();
            float rating = ratingBar.getRating();

            if (reviewText.isEmpty() || rating == 0) {
                Toast.makeText(getContext(), "Please fill all fields and provide a rating.", Toast.LENGTH_LONG).show();
                return;
            }

            String DateKey = String.format("%d/%d/%d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
            Review review = new Review()
                    .setReviewId(UUID.randomUUID().toString())
                    .setName(userName)
                    .setOverview(reviewText)
                    .setRating(rating)
                    .setDate(DateKey);
            fireBaseManager.saveReview(review);

            // Show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Thank You!")
                    .setMessage("Your review has been submitted successfully.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        // Clear fields after saving
                        reviewTextView.setText("");
                        ratingBar.setRating(0);
                    })
                    .show();

            getAllReviews();
            reviews.add(review);
            adapterReview.updateReviews(reviews);
        });
    }


    public void getUserAndInitView() { // Get the user from the database and initialize the views
        fireBaseManager.getUser(auth.getCurrentUser().getUid(), new FireBaseManager.CallBack<User>() {
            @Override
            public void res(User user) {
                if (user != null) {
                    initViews(user);
                } else {
                    Toast.makeText(getContext(), "Error fetching user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void isVetLoggedIn() { // Check if the user is a veterinarian and hide/show the appropriate views
        fireBaseManager.isVeterinarian(auth.getCurrentUser().getUid(), new FireBaseManager.CallBack<Boolean>() {
            @Override
            public void res(Boolean isVet) {
                if (isVet) {
                    addReviewButton.setVisibility(View.GONE);
                    ratingBar.setVisibility(View.GONE);
                    reviewTextView.setVisibility(View.GONE);
                    titleTextView.setText("Reviews");
                } else {
                    AllReviewsTextView.setVisibility(View.GONE);
                    overallRatingTextView.setVisibility(View.GONE);
                    recycler_view_reviews.setVisibility(View.GONE);
                    titleTextView.setText("write a Review");


                }
            }
        });
    }

    private void setupUI(View view) { // Set up a touch listener to hide the keyboard when the user taps outside the text boxes
        // Set up a touch listener to close the keyboard when the user taps outside the text boxes
        if (!(view instanceof MaterialAutoCompleteTextView)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(v);
                return false;
            });
        }

        // If a layout container, iterate over children and apply the touch listener
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideKeyboard(View view) { // Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
