package com.me.pa.models;

public class CEARow {
    String description,time,enteredBy;
    int date;
    double totalCost;
    double[] paids, costs;

    public CEARow() {
    }

    public CEARow(String description, String time, String enteredBy, int date, double totalCost, double[] paids, double[] costs) {
        this.description = description;
        this.time = time;
        this.enteredBy = enteredBy;
        this.date = date;
        this.totalCost = totalCost;
        this.paids = paids;
        this.costs = costs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double[] getPaids() {
        return paids;
    }

    public void setPaids(double[] paids) {
        this.paids = paids;
    }

    public double[] getCosts() {
        return costs;
    }

    public void setCosts(double[] costs) {
        this.costs = costs;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }
}