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

    private MutableLiveData<ArrayList<ExpenseAccount>> mutableExpenseAccountList;
    private MutableLiveData<LinkedHashMap<String, CEA>> mutableCEAAccountList;
    private MutableLiveData<LinkedHashMap<String, ArrayList<PersonExpense>>> mutableCEAPersonsExpenseList;
    private MutableLiveData<LinkedHashMap<String, Double>> mutableCEATotalCostList;
    private MutableLiveData<LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>>> mutableTableList;
    private MutableLiveData<LinkedHashMap<String, ArrayList<String>>> mutableCeaPersonNamesList;


    public DataRepo(Context context) {
        initiate(context);
    }

    public void initiate(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableExpenseAccountList = new MutableLiveData<>(dbHelper.getExpenseAccounts());
        mutableCEAAccountList = new MutableLiveData<>(dbHelper.getCEAAccounts());
        mutableCEAPersonsExpenseList = new MutableLiveData<>(dbHelper.getCEAPersonExpenses());
        mutableCEATotalCostList = new MutableLiveData<>(dbHelper.getCEATotalCosts());
        mutableTableList = new MutableLiveData<>(dbHelper.getCEATables());
        mutableCeaPersonNamesList = new MutableLiveData<>(dbHelper.getCEAPersonNames());
        dbHelper.close();
    }

    public void update(Context context) {
        loadMutableExpenseAccountList(context);
        loadMutableCEAAccountList(context);
        loadMutableCEAPersonsExpenseList(context);
        loadMutableCEATotalCostList(context);
        loadCEATables(context);
        loadMutableCeaPersonNamesList(context);
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

    private void loadMutableCEAAccountList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCEAAccountList.setValue(dbHelper.getCEAAccounts());
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

    private void loadCEATables(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableTableList.setValue(dbHelper.getCEATables());
        dbHelper.close();
    }

    private void loadMutableCeaPersonNamesList(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        mutableCeaPersonNamesList.setValue(dbHelper.getCEAPersonNames());
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

    public MutableLiveData<LinkedHashMap<String, CEA>> getMutableCEAAccountList() {
        return mutableCEAAccountList;
    }

    public MutableLiveData<LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>>> getMutableTableList() {
        return mutableTableList;
    }

    public MutableLiveData<LinkedHashMap<String, ArrayList<String>>> getMutableCeaPersonNamesList() {
        return mutableCeaPersonNamesList;
    }
}
