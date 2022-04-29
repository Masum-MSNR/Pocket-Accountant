package com.me.pa.helpers;

import static com.me.pa.others.Constants.FDR_CEA_TABLES;
import static com.me.pa.others.Constants.TAG_PHONE_NUMBER;
import static com.me.pa.others.Constants.TAG_TABLE;
import static com.me.pa.others.Constants.TAG_TABLE_NAME;
import static com.me.pa.others.Constants.TAG_TYPE;
import static com.me.pa.others.Constants.TYPE_CEA_TABLE;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;
import com.me.pa.services.DataLoaderServiceB;
import com.me.pa.services.DataLoaderServiceF;

import java.util.ArrayList;
import java.util.Objects;

public class DBSynchronizer {

    DatabaseReference reference;
    ValueEventListener listener;
    Context context;

    public DBSynchronizer(Context context) {
        this.context = context;
    }

    public void start(String tableId, ArrayList<String> names) {
        String tableName = TAG_TABLE + tableId;
        int noOfPerson = names.size();
        reference = FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(tableName).child(TAG_TABLE);
        listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String key = ds.getKey();
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            String row = ds2.getValue(String.class);
                            ArrayList<String> results = new ArrayList<>();
                            int startIndex = 0, endIndex = 0;
                            for (char c : Objects.requireNonNull(row).toCharArray()) {
                                if (c == '|') {
                                    results.add(row.substring(startIndex, endIndex));
                                    startIndex = endIndex + 1;
                                }
                                endIndex++;
                            }
                            DBHelper dbHelper = new DBHelper(context);
                            if (dbHelper.isRowExists(tableName, results.get(3), key)) {
                                dbHelper.close();
                                continue;
                            }

                            ArrayList<Double> costAmounts = new ArrayList<>();
                            ArrayList<Double> paidAmounts = new ArrayList<>();

                            for (int i = 9; i < (9 + noOfPerson); i++) {
                                costAmounts.add(Double.parseDouble(results.get(i)));
                            }
                            for (int i = (9 + noOfPerson); i < 9 + (2 * noOfPerson); i++) {
                                paidAmounts.add(Double.parseDouble(results.get(i)));
                            }
                            dbHelper.insertIntoCEA(tableName,
                                    Integer.parseInt(results.get(1)),
                                    Integer.parseInt(results.get(2)),
                                    results.get(3), results.get(4),
                                    results.get(5), "t",
                                    results.get(7),
                                    Double.parseDouble(results.get(8)),
                                    costAmounts, paidAmounts, names);
                            dbHelper.close();
                            DataRepo.getInstance(context).update(context);
                        }
                    }
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent = new Intent(context, DataLoaderServiceF.class);
                        intent.putExtra(TAG_TABLE_NAME, tableName);
                        intent.putExtra(TAG_PHONE_NUMBER, UserRepo.getInstance().getNumber());
                        intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                        context.startForegroundService(intent);
                    } else {
                        intent = new Intent(context, DataLoaderServiceB.class);
                        intent.putExtra(TAG_TABLE_NAME, tableName);
                        intent.putExtra(TAG_PHONE_NUMBER, UserRepo.getInstance().getNumber());
                        intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                        context.startService(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void stop() {
        reference.removeEventListener(listener);
    }
}
