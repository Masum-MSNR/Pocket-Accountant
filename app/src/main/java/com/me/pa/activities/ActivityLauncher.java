package com.me.pa.activities;

import static com.me.pa.others.Constants.IS_DATA_LOADED;
import static com.me.pa.others.Constants.MY_PREFS_NAME;
import static com.me.pa.others.Constants.TYPE_ONLINE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.me.pa.repos.UserRepo;

import java.util.Locale;
import java.util.Objects;

public class ActivityLauncher extends Activity {

    SharedPreferences preferences;
    UserRepo repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repo = UserRepo.getInstance();
        preferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (preferences.getBoolean("loggedIn", false)) {
            repo.init(getApplicationContext());
            if (repo.getAccountType().equals(TYPE_ONLINE)) {
                repo.setNumber(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber());
            }
            Locale locale = new Locale(repo.getLanguage());
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            Intent intent;
            if (repo.isCompleteAccount()) {
                intent = new Intent(getApplicationContext(), Home.class);
                intent.putExtra(IS_DATA_LOADED,false);
            } else {
                intent = new Intent(getApplicationContext(), AccountInfo.class);
            }
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), OnBoarding.class);
            startActivity(intent);
        }
        finish();
    }
}
