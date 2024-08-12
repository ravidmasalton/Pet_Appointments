package com.example.vetappointment.Listeners;

import com.example.vetappointment.Models.OnlineAppointment;

import java.util.ArrayList;

public interface ListenerGetAllOnlineAppointmentFromDB {
    void onOnlineAppointmentFromDBLoadSuccess(ArrayList<OnlineAppointment> appointments);
    void onOnlineAppointmentFromDBLoadFailed(String message);
}
