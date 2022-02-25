package com.me.pa.models;

public class PersonExpense {
    String name;
    double paid,cost, due, receive;

    public PersonExpense() {
    }

    public PersonExpense(String name, double paid, double cost, double due, double receive) {
        this.name = name;
        this.paid = paid;
        this.cost = cost;
        this.due = due;
        this.receive = receive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDue() {
        return due;
    }

    public void setDue(double due) {
        this.due = due;
    }

    public double getReceive() {
        return receive;
    }

    public void setReceive(double receive) {
        this.receive = receive;
    }
}
