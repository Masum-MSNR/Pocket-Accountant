package com.me.pa.models;

public class PersonalExpense {

    int id, date, monthYear;
    String time, description;
    double income, expense;
    int type;

    public PersonalExpense() {
    }

    public PersonalExpense(int date, int monthYear, String time, String description, double income, double expense, int type) {
        this.date = date;
        this.monthYear = monthYear;
        this.time = time;
        this.description = description;
        this.income = income;
        this.expense = expense;
        this.type = type;
    }

    public PersonalExpense(int id, int date, int monthYear, String time, String description, double income, double expense, int type) {
        this.id = id;
        this.date = date;
        this.monthYear = monthYear;
        this.time = time;
        this.description = description;
        this.income = income;
        this.expense = expense;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(int monthYear) {
        this.monthYear = monthYear;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
