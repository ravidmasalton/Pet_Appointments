package com.example.vetappointment;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FireBaseManager {
    private static FireBaseManager instance;
    private FirebaseDatabase dmyDatabase;
    private DatabaseReference databaseReferenceAppointment; // Appointments
    private DatabaseReference databaseReferenceUser; // Users

    // Private constructor to prevent instantiation
    private FireBaseManager() {

        dmyDatabase = FirebaseDatabase.getInstance();
        databaseReferenceAppointment = FirebaseDatabase.getInstance().getReference("appointments");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("users");
        databaseReferenceAppointment.removeValue();

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




}