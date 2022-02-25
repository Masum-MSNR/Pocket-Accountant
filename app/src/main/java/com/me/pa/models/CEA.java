package com.me.pa.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CEA implements Serializable {
    int id, noOfPerson, rowCount;
    String tableId;
    ArrayList<String> names;

    public CEA() {
    }

    public CEA(int id, int noOfPerson, int rowCount, String tableId, ArrayList<String> names) {
        this.id = id;
        this.noOfPerson = noOfPerson;
        this.rowCount = rowCount;
        this.tableId = tableId;
        this.names = names;
    }

    public CEA(int noOfPerson, int rowCount, String tableId, ArrayList<String> names) {
        this.noOfPerson = noOfPerson;
        this.rowCount = rowCount;
        this.tableId = tableId;
        this.names = names;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoOfPerson() {
        return noOfPerson;
    }

    public void setNoOfPerson(int noOfPerson) {
        this.noOfPerson = noOfPerson;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
}
