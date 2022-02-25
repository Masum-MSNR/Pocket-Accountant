package com.me.pa.models;

import java.io.Serializable;

public class ExpenseAccount implements Serializable {
    int id;
    String accountTitle,type,tableId;

    public ExpenseAccount() {
    }

    public ExpenseAccount(int id, String accountTitle, String type, String tableId) {
        this.id = id;
        this.accountTitle = accountTitle;
        this.type = type;
        this.tableId = tableId;
    }

    public ExpenseAccount(String accountTitle, String type, String tableId) {
        this.accountTitle = accountTitle;
        this.type = type;
        this.tableId = tableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
