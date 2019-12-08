package com.mahmoud.hospitalgo.pojo;

public class AmbulanceLocation {
    double log;
    double lot;
    String status;

    public AmbulanceLocation(double log, double lot, String status) {
        this.log = log;
        this.lot = lot;
        this.status = status;
    }

    public AmbulanceLocation() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
