package com.example.vetappointment.Listeners;

import com.example.vetappointment.Models.Appointment;

import java.util.ArrayList;

public interface ListenerGetAllAppointmentFromDB {
    void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments);
    void onAppointmentFromDBLoadFailed(String message);
}
