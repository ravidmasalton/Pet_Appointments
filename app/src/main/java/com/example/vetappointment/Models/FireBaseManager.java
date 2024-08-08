package com.example.vetappointment.Models;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.vetappointment.Listeners.ListenerGetAllAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerBlockAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerGetAllOnlineAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerGetAllReviewFromDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;


public class FireBaseManager {
    private static FireBaseManager instance;
    private final DatabaseReference databaseReferenceAppointment; // Appointments
    private final DatabaseReference databaseReferenceUser; // Users
    private final DatabaseReference databaseReferenceBlockAppointment; // BlockAppointments
    private final DatabaseReference databaseReferenceOnlineAppointment; // OnlineAppointments
    private final DatabaseReference databaseReferenceReview; // Reviews
    private final DatabaseReference databaseReferenceVetManager; // VetManager

    // Private constructor to prevent instantiation
    private FireBaseManager() {

        FirebaseDatabase dmyDatabase = FirebaseDatabase.getInstance();
        databaseReferenceAppointment = dmyDatabase.getReference("appointments");
        databaseReferenceUser = dmyDatabase.getReference("users");
        databaseReferenceBlockAppointment = dmyDatabase.getReference("blockAppointments");
        databaseReferenceOnlineAppointment = dmyDatabase.getReference("onlineAppointments");
        databaseReferenceReview = dmyDatabase.getReference("reviews");
        databaseReferenceVetManager= dmyDatabase.getReference("vetManager");

    }

    // Public method to provide access to the instance
    public static synchronized FireBaseManager getInstance() {
        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }


    public interface CallBack<T> {
        void res(T res);
    }


    public void saveAppointment(Appointment appointment) { // Save appointment
        DatabaseReference ref = databaseReferenceAppointment.child(appointment.getAppointmentId());
        ref.setValue(appointment);
    }

    public void saveAppointmentForUser(User user, String appointmentId) { // Save appointment for user
        DatabaseReference ref = databaseReferenceUser.child(user.getId()).child("appointments");
        ref.push().setValue(appointmentId);
    }

    public void getAllAppointments(ListenerGetAllAppointmentFromDB listener) { // Get all appointments
        ArrayList<Appointment> appointments = new ArrayList<>();
        databaseReferenceAppointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Appointment appointment = snap.getValue(Appointment.class);
                    appointments.add(appointment);
                }
                listener.onAppointmentFromDBLoadSuccess(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onAppointmentFromDBLoadFailed(error.getMessage());
            }
        });
    }

    public void getUser(String userId, CallBack<User> callback) { // Get user
        DatabaseReference ref = databaseReferenceUser.child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = new User();
                    user.setId(snapshot.child("id").getValue(String.class));
                    user.setName(snapshot.child("name").getValue(String.class));
                    user.setEmail(snapshot.child("email").getValue(String.class));
                    user.setPhone(snapshot.child("phone").getValue(String.class));
                    user.setIsDoctor(snapshot.child("isDoctor").getValue(Boolean.class));
                    callback.res(user);
                } else {
                    callback.res(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void saveBlockAppointment(BlockAppointment blockAppointment) { // Save block appointment
        DatabaseReference ref = databaseReferenceBlockAppointment.child(blockAppointment.getBlockDayId());
        ref.setValue(blockAppointment);

    }

    public void getAllBlockAppointments(ListenerBlockAppointmentFromDB listener) { // Get block appointments
        ArrayList<BlockAppointment> blockAppointments = new ArrayList<>();
        databaseReferenceBlockAppointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    BlockAppointment blockAppointment = snap.getValue(BlockAppointment.class);
                    blockAppointments.add(blockAppointment);
                }
                listener.onBlockAppointmentLoadSuccess(blockAppointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onBlockAppointmentLoadFailed(error.getMessage());
            }
        });
    }

    public void removeBlockAppointment(Appointment appointment) { // Remove block appointment
        getAllBlockAppointments(new ListenerBlockAppointmentFromDB() {
            @Override
            public void onBlockAppointmentLoadSuccess(ArrayList<BlockAppointment> blockAppointment) {
                for (BlockAppointment blockAppointment1 : blockAppointment) {
                    if (blockAppointment1.getAppointmentId().equals(appointment.getAppointmentId())) {
                        DatabaseReference ref = databaseReferenceBlockAppointment.child(blockAppointment1.getBlockDayId());
                        ref.removeValue();
                    }
                }
            }

            @Override
            public void onBlockAppointmentLoadFailed(String message) {
            }
        });


    }

    public void removeAppointmentFromUser(String userId, String appointmentId) { // Remove appointment for user
        DatabaseReference userRef = databaseReferenceUser.child(userId).child("appointments");
        userRef.orderByValue().equalTo(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // onDataChange is called when the data is found
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    childSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential error
            }
        });
    }

    public void removeAppointment(Appointment appointment) { // Remove appointment
        DatabaseReference ref = databaseReferenceAppointment.child(appointment.getAppointmentId());
        ref.removeValue();
    }


    public void uploadImage(Uri uri, String userUid, CallBack<String> callback) { // Upload image
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("USERS").child(userUid + ".jpg");

        if (uri == null) {
            callback.res("");
            return;
        }
        UploadTask uploadTask = imageRef.putFile(uri);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.res("");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String imageUrl = task.getResult().toString();
                        callback.res(imageUrl);
                    }
                });
            }
        });


    }

    public void saveOnlineAppointment(OnlineAppointment onlineAppointment, Uri uri) { // Save online appointment


        uploadImage(uri, onlineAppointment.getUserId(), new CallBack<String>() {
            @Override
            public void res(String res) {
                onlineAppointment.setImageUrl(res);
                DatabaseReference ref = databaseReferenceOnlineAppointment.child(onlineAppointment.getOnlineAppointmentId());
                ref.setValue(onlineAppointment);
                saveOnlineAppointmentForUser(onlineAppointment.getUserId(), onlineAppointment.getOnlineAppointmentId());
            }
        });


    }


    public void getAllOnlineAppointmentsForUser(String userId, ListenerGetAllOnlineAppointmentFromDB listener) { // Get all online appointments for user
        ArrayList<OnlineAppointment> onlineAppointments = new ArrayList<>();
        DatabaseReference ref = databaseReferenceUser.child(userId).child("onlineAppointments");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long totalAppointments = snapshot.getChildrenCount();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String onlineAppointmentId = snap.getValue(String.class);
                        databaseReferenceOnlineAppointment.child(onlineAppointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                OnlineAppointment onlineAppointment = snapshot.getValue(OnlineAppointment.class);
                                onlineAppointments.add(onlineAppointment);
                                if (onlineAppointments.size() == totalAppointments) {
                                    listener.onOnlineAppointmentFromDBLoadSuccess(onlineAppointments);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                listener.onOnlineAppointmentFromDBLoadFailed(error.getMessage());
                            }
                        });
                    }
                } else {
                    listener.onOnlineAppointmentFromDBLoadSuccess(onlineAppointments); // No appointments found, return empty list
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onOnlineAppointmentFromDBLoadFailed(error.getMessage());
            }
        });
    }


    public void getAllOnlineAppointments(ListenerGetAllOnlineAppointmentFromDB listener) { // Get all online appointments
        ArrayList<OnlineAppointment> onlineAppointments = new ArrayList<>();
        databaseReferenceOnlineAppointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    OnlineAppointment onlineAppointment = snap.getValue(OnlineAppointment.class);
                    onlineAppointments.add(onlineAppointment);
                }
                listener.onOnlineAppointmentFromDBLoadSuccess(onlineAppointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onOnlineAppointmentFromDBLoadFailed(error.getMessage());
            }
        });
    }


    public void saveOnlineAppointmentForUser(String userId, String onlineAppointmentId) { // Save online appointment for user
        DatabaseReference ref = databaseReferenceUser.child(userId).child("onlineAppointments");
        ref.push().setValue(onlineAppointmentId);
    }


    public void saveReview(Review review) { // Save review
        DatabaseReference ref = databaseReferenceReview.child(review.getReviewId());
        ref.setValue(review);
    }

    public void getAllReviews(ListenerGetAllReviewFromDB listener) { // Get all reviews
        ArrayList<Review> reviews = new ArrayList<>();
        databaseReferenceReview.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Review review = snap.getValue(Review.class);
                    reviews.add(review);
                }
                listener.onReviewFromDBLoadSuccess(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onReviewFromDBLoadFailed(error.getMessage());
            }
        });


    }

    public void isVeterinarian(String userId, CallBack<Boolean> callback) { // Check if user is veterinarian
        DatabaseReference ref = databaseReferenceUser.child(userId).child("isDoctor");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.res(snapshot.getValue(Boolean.class));
                } else {
                    callback.res(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getVetManager(CallBack<VetManager> callback) { // Get veterinarian
        databaseReferenceVetManager.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VetManager vetManager = snapshot.getValue(VetManager.class);
                callback.res(vetManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}