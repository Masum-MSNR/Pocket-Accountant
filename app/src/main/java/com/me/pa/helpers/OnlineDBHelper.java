package com.me.pa.helpers;

import static com.me.pa.others.Constants.FDR_CEA;
import static com.me.pa.others.Constants.FDR_CEA_TABLES;
import static com.me.pa.others.Constants.FDR_EXPENSE_ACCOUNTS;
import static com.me.pa.others.Constants.TAG_TABLE;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.me.pa.models.CEA;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;

import java.util.ArrayList;

public class OnlineDBHelper {

    UserRepo userRepo;
    Context context;

    public OnlineDBHelper(Context context) {
        this.context = context;
        userRepo = UserRepo.getInstance();
    }

    public void getBackUpData() {
        writeExpenseAccounts();
        createCEA();
    }

    private void writeExpenseAccounts() {
        FirebaseDatabase.getInstance().getReference(FDR_EXPENSE_ACCOUNTS).child(userRepo.getNumber()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DBHelper dbHelper = new DBHelper(context);
                for (DataSnapshot ss : task.getResult().getChildren()) {
                    ExpenseAccount expenseAccount = ss.getValue(ExpenseAccount.class);
                    dbHelper.insertIntoAccounts(expenseAccount, false);
                }
                dbHelper.close();
            }
        });

    }

    public void getExistingCEA(String id) {
        String tableId = id.substring(0, 15);
        DBHelper dbHelperP = new DBHelper(context);
        if (dbHelperP.isTableExists(tableId)) {
            Toast.makeText(context, "Already exists!", Toast.LENGTH_SHORT).show();
            return;
        }
        String phoneNumber = "+880" + id.substring(15, 25);
        FirebaseDatabase.getInstance().getReference(FDR_EXPENSE_ACCOUNTS).child(phoneNumber).child(tableId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DBHelper dbHelper = new DBHelper(context);
                ExpenseAccount expenseAccount = task.getResult().getValue(ExpenseAccount.class);
                dbHelper.insertIntoAccounts(expenseAccount, true);
                dbHelper.close();
                FirebaseDatabase.getInstance().getReference(FDR_CEA).child(phoneNumber).child(tableId).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DBHelper dbHelper1 = new DBHelper(context);
                        CEA cea = task1.getResult().getValue(CEA.class);
                        dbHelper1.insertIntoCollectiveAccounts(cea, true);
                        dbHelper1.createNewCEATable(TAG_TABLE + tableId, cea.getNames());
                        DataRepo.getInstance(context).update(context);
                        dbHelper1.close();
                    }
                });
            } else {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createCEA() {
        FirebaseDatabase.getInstance().getReference(FDR_CEA).child(userRepo.getNumber()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DBHelper dbHelper = new DBHelper(context);
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    CEA cea = ds.getValue(CEA.class);
                    dbHelper.insertIntoCollectiveAccounts(cea, false);
                    String tableName = TAG_TABLE + cea.getTableId();
                    dbHelper.createNewCEATable(tableName, cea.getNames());
                }
                dbHelper.close();
                DataRepo.getInstance(context).update(context);
            }
        });
    }
}
