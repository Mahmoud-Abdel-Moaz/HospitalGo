package com.mahmoud.hospitalgo.pojo;

public class PatientLocation {
    double log;
    double lot;
    String status;

    public PatientLocation(double log, double lot, String status) {
        this.log = log;
        this.lot = lot;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PatientLocation() {
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public double getLot() {
        return lot;
    }

    public void setLot(double lot) {
        this.lot = lot;
    }
}
