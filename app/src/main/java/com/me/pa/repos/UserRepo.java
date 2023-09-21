package com.me.pa.repos;

import static android.content.Context.MODE_PRIVATE;
import static com.me.pa.others.Constants.FDR_USER_ACCOUNTS;
import static com.me.pa.others.Constants.MY_PREFS_NAME;
import static com.me.pa.others.Constants.SQLITE_DATABASE_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.me.pa.models.UserAccount;

public class UserRepo {
    public static UserRepo instance;
    String number, name, language, accountType;
    boolean completeAccount;

    public UserRepo() {

    }

    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    public void init(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        name = preferences.getString("fullName", "");
        language = preferences.getString("local", "en");
        accountType = preferences.getString("accountType", "");
        completeAccount = preferences.getBoolean("completeAccount", false);
    }

    public void initialCommit(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("local", language);
        editor.putString("accountType", accountType);
        editor.putBoolean("loggedIn", true);
        editor.putBoolean("completeAccount", completeAccount);
        editor.apply();
        if (accountType.equals("online") && !completeAccount) {
            UserAccount account = new UserAccount(number, "", language, false);
            FirebaseDatabase.getInstance().getReference(FDR_USER_ACCOUNTS).child(number).setValue(account);
        }
    }

    public void finalCommit(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fullName", name);
        editor.putBoolean("completeAccount", completeAccount);
        editor.apply();
        if (accountType.equals("online") && !completeAccount) {
            UserAccount account = new UserAccount(number, name, language, true);
            FirebaseDatabase.getInstance().getReference(FDR_USER_ACCOUNTS).child(number).setValue(account);
        }
    }

    public void logOut(Context context) {
        context.deleteDatabase(SQLITE_DATABASE_NAME);
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (preferences.getString("accountType", "online").equals("online")) {
            FirebaseAuth.getInstance().signOut();
        }
        editor.putString("fullName", "");
        editor.putString("profileImage", "");
        editor.putString("accountType", "");
        editor.putBoolean("loggedIn", false);
        editor.putBoolean("completeAccount", false);
        editor.apply();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isCompleteAccount() {
        return completeAccount;
    }

    public void setCompleteAccount(boolean completeAccount) {
        this.completeAccount = completeAccount;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

}
