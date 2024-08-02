package com.example.vetappointment;

import java.util.ArrayList;

public interface ListenerAppointmentFromDB {
    void onAppointmentFromDBLoadSuccess(ArrayList<Appointment> appointments);
    void onAppointmentFromDBLoadFailed(String message);
}
