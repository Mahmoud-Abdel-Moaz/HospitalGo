package com.mahmoud.hospitalgo.pojo;

public class Emergency {
    String patient_id,ambulance_id,status;

    public Emergency(String patient_id, String ambulance_id, String status) {
        this.patient_id = patient_id;
        this.ambulance_id = ambulance_id;
        this.status = status;
    }

    public Emergency() {
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getAmbulance_id() {
        return ambulance_id;
    }

    public void setAmbulance_id(String ambulance_id) {
        this.ambulance_id = ambulance_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
