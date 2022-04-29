package com.me.pa.services;

import static com.me.pa.others.Constants.FDR_CEA;
import static com.me.pa.others.Constants.FDR_CEA_TABLES;
import static com.me.pa.others.Constants.FDR_EXPENSE_ACCOUNTS;
import static com.me.pa.others.Constants.SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_EXPENSE_ACCOUNT;
import static com.me.pa.others.Constants.TAG_PHONE_NUMBER;
import static com.me.pa.others.Constants.TAG_TABLE;
import static com.me.pa.others.Constants.TAG_TABLE_NAME;
import static com.me.pa.others.Constants.TAG_TYPE;
import static com.me.pa.others.Constants.TYPE_ACCOUNT;
import static com.me.pa.others.Constants.TYPE_CEA;
import static com.me.pa.others.Constants.TYPE_CEA_TABLE;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.FirebaseDatabase;
import com.me.pa.R;
import com.me.pa.helpers.DBHelper;
import com.me.pa.models.CEA;
import com.me.pa.models.ExpenseAccount;

import java.util.LinkedHashMap;

public class DataLoaderServiceF extends Service {

    int count;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_icon)
                .setContentText("Sending backup")
                .build();
        startForeground(69, notification);

        String tableName = intent.getStringExtra(TAG_TABLE_NAME);
        String phoneNumber = intent.getStringExtra(TAG_PHONE_NUMBER);
        String type = intent.getStringExtra(TAG_TYPE);
        ExpenseAccount expenseAccount = (ExpenseAccount) intent.getSerializableExtra(TAG_EXPENSE_ACCOUNT);
        CEA caAccount = (CEA) intent.getSerializableExtra(TAG_CEA);
        new Thread(() -> {
            switch (type) {
                case TYPE_ACCOUNT: {
                    startProcess(phoneNumber, expenseAccount);
                    break;
                }
                case TYPE_CEA: {
                    startProcess(phoneNumber, caAccount);
                    break;
                }
                case TYPE_CEA_TABLE:
                    startProcess(tableName, phoneNumber);
                    break;
            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    private void startProcess(String phoneNumber, ExpenseAccount expenseAccount) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        FirebaseDatabase.getInstance().getReference(FDR_EXPENSE_ACCOUNTS).child(phoneNumber).child(expenseAccount.getTableId()).setValue(expenseAccount).addOnCompleteListener(t -> {
            if (t.isSuccessful()) {
                stopSelf();
            }
        });
        dbHelper.close();
    }

    private void startProcess(String phoneNumber, CEA caAccount) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        FirebaseDatabase.getInstance().getReference(FDR_CEA).child(phoneNumber).child(caAccount.getTableId()).setValue(caAccount).addOnCompleteListener(t -> {
            if (!t.isSuccessful()) {
                stopSelf();
            }
        });
        dbHelper.close();
    }

    private void startProcess(String tableName, String number) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        LinkedHashMap<Integer, String> map = dbHelper.getRowByUnSynced(tableName);
        dbHelper.close();
        if (map.size() == 0) {
            stopSelf();
        }
        count = 0;
        for (Integer id : map.keySet()) {
            FirebaseDatabase.getInstance()
                    .getReference(FDR_CEA_TABLES)
                    .child(tableName)
                    .child(TAG_TABLE).child(number)
                    .child(String.valueOf(id))
                    .setValue(map.get(id))
                    .addOnCompleteListener(t -> {
                        if (t.isSuccessful()) {
                            count++;
                            DBHelper db = new DBHelper(getApplicationContext());
                            db.updateCEAById(tableName, "synced", "t", id);
                            db.close();
                            if (count == map.size()) {
                                stopSelf();
                            }
                        }
                    });
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
