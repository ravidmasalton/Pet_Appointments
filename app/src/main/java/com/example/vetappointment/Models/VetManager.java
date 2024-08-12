package com.example.vetappointment.Models;

public class VetManager {
    private String vetId;
    private  String vetName;
    private  String vetPhone;
    private  String vetEmail;
    private  String vetAddress;
    private  String vetDescription;
    private  String startTime;
    private  String endTime;




    public VetManager(String vetId, String vetName, String vetPhone, String vetEmail, String vetAddress, String vetDescription, String startTime, String endTime) {
        this.vetId = vetId;
        this.vetName = vetName;
        this.vetPhone = vetPhone;
        this.vetEmail = vetEmail;
        this.vetAddress = vetAddress;
        this.vetDescription = vetDescription;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public VetManager() {
    }

    public VetManager setVetId(String vetId) {
        this.vetId = vetId;
        return this;
    }

    public VetManager setVetName(String vetName) {
        this.vetName = vetName;
        return this;

    }

    public VetManager setVetPhone(String vetPhone) {
        this.vetPhone = vetPhone;
        return this;

    }

    public VetManager setVetEmail(String vetEmail) {
        this.vetEmail = vetEmail;
        return this;

    }

    public VetManager setVetAddress(String vetAddress) {
        this.vetAddress = vetAddress;
        return this;

    }

    public VetManager setVetDescription(String vetDescription) {
        this.vetDescription = vetDescription;
        return this;

    }

    public VetManager setStartTime(String startTime) {
        this.startTime = startTime;
        return this;

    }

    public VetManager setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getVetId() {
        return vetId;
    }

    public String getVetName() {
        return vetName;
    }

    public String getVetPhone() {
        return vetPhone;
    }

    public String getVetEmail() {
        return vetEmail;
    }

    public String getVetDescription() {
        return vetDescription;
    }

    public String getVetAddress() {
        return vetAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
