package com.example.vetappointment.Listeners;

import com.example.vetappointment.Models.BlockAppointment;

import java.util.ArrayList;

public interface ListenerBlockAppointmentFromDB {
    void onBlockAppointmentLoadSuccess(ArrayList<BlockAppointment> blockAppointment);
    void onBlockAppointmentLoadFailed(String message);
}
