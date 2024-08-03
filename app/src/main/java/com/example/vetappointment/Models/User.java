package com.example.vetappointment.Models;

import java.util.List;

public class User {

    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> appointments;
    private boolean isDoctor;


    public User() {}


    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }



    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }
    public List<String> getAppointments() {
        return appointments;
    }

    public User setAppointments(List<String> appointments) {
        this.appointments = appointments;
        return this;
    }


    public Boolean getIsDoctor() {
        return isDoctor;
    }

    public User setIsDoctor(Boolean isDoctor) {
        this.isDoctor = isDoctor;
        return this;
    }

}
