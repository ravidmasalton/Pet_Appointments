package com.example.vetappointment.Interfaces;

import com.example.vetappointment.Models.Appointment;

public interface CancelAppointmentCallback {
    void cancelAppointment(Appointment appointment, int position);

}
