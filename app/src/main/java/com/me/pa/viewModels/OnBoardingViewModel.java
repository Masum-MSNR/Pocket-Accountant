package com.me.pa.viewModels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.me.pa.others.Functions;
import com.me.pa.repos.OnBoardingRepo;

public class OnBoardingViewModel extends ViewModel {

    private OnBoardingRepo repository;
    private String local;
    private MutableLiveData<String> localLanguage;

    public void init(Context context) {
        repository = OnBoardingRepo.getInstance();
        repository.initPref(context);
        local = repository.getLocal();
        localLanguage = new MutableLiveData<>(local);
        changeLanguage(context, local);
    }

    public String getLocal() {
        return local;
    }


    public MutableLiveData<String> getLocalLanguage() {
        return localLanguage;
    }

    public void changeLanguage(Context context, String language) {
        Functions.changeLanguage(context, language);
        this.local = language;
        this.localLanguage.setValue(language);
    }
}