package com.me.pa.repos;

import static com.me.pa.others.Constants.MY_PREFS_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class OnBoardingRepo {

    private static OnBoardingRepo instance;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private OnBoardingRepo() {

    }

    public static OnBoardingRepo getInstance() {
        if (instance == null) {
            instance = new OnBoardingRepo();
        }
        return instance;
    }

    public void initPref(Context context) {
        preferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public String getLocal() {
        return preferences.getString("local", Locale.getDefault().getLanguage());
    }
}