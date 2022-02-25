package com.me.pa.services;

import static com.me.pa.others.Constants.FDR_CEA;
import static com.me.pa.others.Constants.FDR_CEA_TABLES;
import static com.me.pa.others.Constants.FDR_EXPENSE_ACCOUNTS;
import static com.me.pa.others.Constants.SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_CURRENT_RC;
import static com.me.pa.others.Constants.TAG_EXPENSE_ACCOUNT;
import static com.me.pa.others.Constants.TAG_OLD_RC;
import static com.me.pa.others.Constants.TAG_PHONE_NUMBER;
import static com.me.pa.others.Constants.TAG_ROW_COUNT;
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

        int currentRc = intent.getIntExtra(TAG_CURRENT_RC, 0);
        int oldRc = intent.getIntExtra(TAG_OLD_RC, 0);
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
                    startProcess(tableName, oldRc, currentRc);
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

    private void startProcess(String tableName, int oldRC, int currentRC) {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        LinkedHashMap<String, String> rowsMap = new LinkedHashMap<>(dbHelper.getSpecificRow(tableName, oldRC, currentRC));
        dbHelper.updateCEARowCount(tableName.substring(1), currentRC);
        for (String key : rowsMap.keySet()) {
            FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(tableName).child(TAG_TABLE).child(key).setValue(rowsMap.get(key)).addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(tableName).child(TAG_ROW_COUNT).setValue(currentRC).addOnCompleteListener(t1 -> {
                        if (t1.isSuccessful()) {
                            if (Integer.parseInt(key) == currentRC) {
                                stopSelf();
                            }
                        }
                    });
                }
            });
        }
        dbHelper.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
