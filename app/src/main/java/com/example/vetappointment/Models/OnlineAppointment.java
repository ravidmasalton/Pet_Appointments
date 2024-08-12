package com.example.vetappointment.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OnlineAppointment implements Comparable<OnlineAppointment> {
    private String onlineAppointmentId;
    private String message="";
    private String response="";
    private String userId;
    private String date;
    private String time;
    private String imageUrl="";
    private Boolean active;
    private boolean isCollapsed = true;

    public OnlineAppointment() {
    }


    // Getters and setters

    public String getOnlineAppointmentId() {
        return onlineAppointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }


    public OnlineAppointment setOnlineAppointmentId(String onlineAppointmentId) {
        this.onlineAppointmentId = onlineAppointmentId;
        return this;
    }

    public OnlineAppointment setTime(String time) {
        this.time = time;
        return this;
    }

    public OnlineAppointment setDate(String date) {
        this.date = date;
        return this;
    }

    public OnlineAppointment setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public OnlineAppointment setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }


    public OnlineAppointment setActive(Boolean active) {
        this.active = active;
        return this;
    }

    public Boolean getActive() {
        return active;
    }


    public String getMessage() {
        return message;
    }

    public OnlineAppointment setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public OnlineAppointment setResponse(String response) {
        this.response = response;
        return this;
    }

    public boolean isCollapsed() {
        return isCollapsed;
    }

    public OnlineAppointment setCollapsed(boolean collapsed) {
        isCollapsed = collapsed;
        return this;
    }


    @Override
    public int compareTo(OnlineAppointment other) {
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


    @Override
    public String toString() {
        return "OnlineAppointment{" +
                "onlineAppointmentId='" + onlineAppointmentId + '\'' +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isActive=" + active +
                ", isCollapsed=" + isCollapsed +
                '}';
    }
}
