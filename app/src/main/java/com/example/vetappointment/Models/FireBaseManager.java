package com.example.vetappointment.Models;


import androidx.annotation.NonNull;

import com.example.vetappointment.Listeners.ListenerAppointmentFromDB;
import com.example.vetappointment.Listeners.ListenerBlockAppointmentFromDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FireBaseManager {
    private static FireBaseManager instance;
    private final DatabaseReference databaseReferenceAppointment; // Appointments
    private final DatabaseReference databaseReferenceUser; // Users
    private final DatabaseReference databaseReferenceBlockAppointment; // BlockAppointments

    // Private constructor to prevent instantiation
    private FireBaseManager() {

        FirebaseDatabase dmyDatabase = FirebaseDatabase.getInstance();
        databaseReferenceAppointment = dmyDatabase.getReference("appointments");
        databaseReferenceUser = dmyDatabase.getReference("users");
        databaseReferenceBlockAppointment = dmyDatabase.getReference("blockAppointments");

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

    public void getAllAppointments(ListenerAppointmentFromDB listener) { // Get all appointments
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

    public void removeAppointment(Appointment appointment) { // Remove appointment
        DatabaseReference ref = databaseReferenceAppointment.child(appointment.getAppointmentId());
        ref.removeValue();
    }




}