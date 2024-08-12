package com.example.vetappointment.Models;

public class BlockAppointment {


    private String blockDayId;
    private String date;
    private String reason;



    public BlockAppointment() {
    }

    public BlockAppointment(String date, String reason) {
        this.date = date;
        this.reason = reason;
    }



    public BlockAppointment setBlockDayId(String blockDayId) {
        this.blockDayId = blockDayId;
        return this;
    }

    public BlockAppointment setDate(String date) {
        this.date = date;
        return this;

    }



    public String getDate() {
        return date;
    }

    public String getBlockDayId() {
        return blockDayId;
    }

    public String getReason() {
        return reason;
    }
    public BlockAppointment setReason(String reason) {
        this.reason = reason;
        return this;
    }




}
