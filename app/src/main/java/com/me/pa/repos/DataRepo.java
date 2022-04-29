package com.me.pa.repos;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.me.pa.helpers.DBHelper;
import com.me.pa.models.CEA;
import com.me.pa.models.CEARow;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.models.PersonExpense;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DataRepo {
    private static DataRepo instance;

    private MutableLiveData<ArrayList<ExpenseAccount>> mutableExpenseAccountList; //n
    private MutableLiveData<LinkedHashMap<String, CEA>> mutableCEAList; //n
    private MutableLiveData<LinkedHashMap<String, ArrayList<PersonExpense>>> mutableCEAPersonsExpenseList; //n
    private MutableLiveData<LinkedHashMap<String, Double>> mutableCEATotalCostList; //n
    private MutableLiveData<LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>>> mutableCEATableList; //n


    public DataRepo(Context context) {
        initiate(context);
    }


    public void initiate(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableExpenseAccountList = new MutableLiveData<>(dbHelper.getExpenseAccounts());
        mutableCEAList = new MutableLiveData<>(dbHelper.getCEAAccounts());
        mutableCEAPersonsExpenseList = new MutableLiveData<>(dbHelper.getCEAPersonExpenses());
        mutableCEATotalCostList = new MutableLiveData<>(dbHelper.getCEATotalCosts());
        mutableCEATableList = new MutableLiveData<>(dbHelper.getCEATables());
        dbHelper.close();
    }


    public void update(Context context) {
        loadMutableCEAList(context);
        loadMutableExpenseAccountList(context);
        loadMutableCEAPersonsExpenseList(context);
        loadMutableCEATotalCostList(context);
        loadMutableCEATables(context);
    }


    public static DataRepo getInstance(Context context) {
        if (instance == null) {
            instance = new DataRepo(context);
        }
        return instance;
    }

    private void loadMutableExpenseAccountList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableExpenseAccountList.setValue(dbHelper.getExpenseAccounts());
        dbHelper.close();
    }

    private void loadMutableCEAList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCEAList.setValue(dbHelper.getCEAAccounts());
        dbHelper.close();
    }


    private void loadMutableCEAPersonsExpenseList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCEAPersonsExpenseList.setValue(dbHelper.getCEAPersonExpenses());
        dbHelper.close();
    }

    private void loadMutableCEATotalCostList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCEATotalCostList.setValue(dbHelper.getCEATotalCosts());
        dbHelper.close();
    }

    private void loadMutableCEATables(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCEATableList.setValue(dbHelper.getCEATables());
        dbHelper.close();
    }

    public MutableLiveData<ArrayList<ExpenseAccount>> getMutableExpenseAccountList() {
        return mutableExpenseAccountList;
    }

    public MutableLiveData<LinkedHashMap<String, ArrayList<PersonExpense>>> getMutableCEAPersonsExpenseList() {
        return mutableCEAPersonsExpenseList;
    }

    public MutableLiveData<LinkedHashMap<String, Double>> getMutableCEATotalCostList() {
        return mutableCEATotalCostList;
    }

    public MutableLiveData<LinkedHashMap<String, CEA>> getMutableCEAList() {
        return mutableCEAList;
    }

    public MutableLiveData<LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>>> getMutableCEATableList() {
        return mutableCEATableList;
    }

    public void destroy() {
        instance = null;
    }
}
