
package com.me.pa.repos;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.me.pa.helpers.DBHelper;

import java.util.ArrayList;

public class ViewCollectiveAccountRepo {

    private static ViewCollectiveAccountRepo instance;
    MutableLiveData<ArrayList<Integer>> months;
    MutableLiveData<ArrayList<String>> names;
    MutableLiveData<ArrayList<Integer>> dates;


    private ViewCollectiveAccountRepo() {
        months = new MutableLiveData<>();
        dates = new MutableLiveData<>();
        names = new MutableLiveData<>();
    }

    public static ViewCollectiveAccountRepo getInstance() {
        if (instance == null) {
            instance = new ViewCollectiveAccountRepo();
        }
        return instance;
    }

    public void loadMonths(Context context, String tableName) {
        DBHelper dbHelper = new DBHelper(context);
        months.setValue(dbHelper.getCEATableMonths(tableName));
        dbHelper.close();
    }

    public void loadDates(Context context, String tableName, int month) {
        if (month==0) {
            dates.setValue(new ArrayList<>());
            return;
        }
        DBHelper dbHelper = new DBHelper(context);
        dbHelper.close();
    }

    public void loadNames(Context context, String tableName) {
        DBHelper dbHelper = new DBHelper(context);
        names.setValue(dbHelper.getCEAPersonNamesByTable(tableName));
        dbHelper.close();
    }


    public void clear(){
        months = new MutableLiveData<>();
        dates = new MutableLiveData<>();
        names = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Integer>> getMonths() {
        return months;
    }

    public MutableLiveData<ArrayList<String>> getNames() {
        return names;
    }

    public MutableLiveData<ArrayList<Integer>> getDates() {
        return dates;
    }

}
