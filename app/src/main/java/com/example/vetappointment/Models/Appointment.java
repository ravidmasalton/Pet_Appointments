package com.example.vetappointment.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appointment implements Comparable<Appointment> {

    private String appointmentId;
    private String date;
    private String time;
    private String service;
    private String costumerPhone;
    private String idCustomer;
    private String customerName;
    private String petType;

    public Appointment() {
    }

    public Appointment(String appointmentId, String date, String time, String service, String costumerPhone, String idCustomer, String customerName) {
        this.appointmentId = appointmentId;
        this.date = date;
        this.time = time;
        this.service = service;
        this.costumerPhone = costumerPhone;
        this.idCustomer = idCustomer;
        this.customerName = customerName;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getCostumerPhone() {
        return costumerPhone;
    }

    public String getService() {
        return service;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Appointment setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public Appointment setDate(String date) {
        this.date = date;
        return this;
    }

    public Appointment setTime(String time) {
        this.time = time;
        return this;
    }

    public Appointment setService(String service) {
        this.service = service;
        return this;
    }

    public Appointment setCostumerPhone(String costumerPhone) {
        this.costumerPhone = costumerPhone;
        return this;
    }

    public Appointment setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
        return this;
    }

    public Appointment setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getPetType() {
        return petType;
    }

    public Appointment setPetType(String petType) {
        this.petType = petType;
        return this;
    }

    @Override
    public int compareTo(Appointment other) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date thisDateTime = dateFormat.parse(this.date + " " + this.time);
            Date otherDateTime = dateFormat.parse(other.date + " " + other.time);
            return thisDateTime.compareTo(otherDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
