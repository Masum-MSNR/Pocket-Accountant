package com.me.pa.application;

import androidx.lifecycle.MutableLiveData;

public class AppRepo {
    private static AppRepo instance;

    private MutableLiveData<Boolean> networkAvailable;

    public AppRepo() {
        networkAvailable=new MutableLiveData<>(false);
    }

    public static AppRepo getInstance() {
        if (instance == null) {
            instance = new AppRepo();
        }
        return instance;
    }

    public MutableLiveData<Boolean> getNetworkAvailable() {
        return networkAvailable;
    }

    public void setNetworkAvailable(boolean networkAvailable) {
        this.networkAvailable.setValue(networkAvailable);
    }
}
