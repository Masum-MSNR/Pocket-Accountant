package com.me.pa.viewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.me.pa.helpers.DBHelper;

public class DialogAddPEVM extends ViewModel {

    public boolean isFormValid(boolean v1, boolean v2, boolean v3) {
        return v1 && v2 && v3;
    }

    public void savePE(Context context, String tableName, int date, String description, double amount, int type) {
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.insertIntoPE(tableName, date, description, amount, type);
    }

}
