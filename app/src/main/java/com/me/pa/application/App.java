package com.me.pa.application;

import static com.me.pa.others.Constants.SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.me.pa.others.Constants.SERVICE_NOTIFICATION_CHANNEL_NAME;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.me.pa.broadcastReceivers.NetworkStateReceiver;
import com.me.pa.repos.DataRepo;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initiateNetworkReceiver();
        initDataRepo();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel service_channel = new NotificationChannel(
                    SERVICE_NOTIFICATION_CHANNEL_ID,
                    SERVICE_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            service_channel.setDescription("Foreground Service Indicator");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(service_channel);
        }
    }

    private void initiateNetworkReceiver() {
        NetworkStateReceiver receiver = new NetworkStateReceiver();
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void initDataRepo() {
        DataRepo.getInstance(getApplicationContext());
    }

}
