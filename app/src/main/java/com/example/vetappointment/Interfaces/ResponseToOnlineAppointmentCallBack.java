package com.example.vetappointment.Interfaces;

import com.example.vetappointment.Models.OnlineAppointment;

public interface ResponseToOnlineAppointmentCallBack {
    void onResponseToOnlineAppointmentCallBack(String response, OnlineAppointment onlineAppointment, int position);
}
