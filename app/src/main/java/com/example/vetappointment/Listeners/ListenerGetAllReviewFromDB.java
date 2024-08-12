package com.example.vetappointment.Listeners;


import com.example.vetappointment.Models.Review;

import java.util.ArrayList;

public interface ListenerGetAllReviewFromDB {

    void onReviewFromDBLoadSuccess(ArrayList<Review> appointments);
    void onReviewFromDBLoadFailed(String message);


}
