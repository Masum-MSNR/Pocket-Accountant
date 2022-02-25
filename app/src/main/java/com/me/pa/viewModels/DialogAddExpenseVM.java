package com.me.pa.viewModels;

import static com.me.pa.others.Constants.TAG_TABLE;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.me.pa.helpers.DBHelper;
import com.me.pa.models.CEA;
import com.me.pa.repos.DataRepo;

import java.util.ArrayList;
import java.util.Objects;

public class DialogAddExpenseVM extends ViewModel {

    ArrayList<String> names;
    CEA caAccount;
    String tableName = "";
    DBHelper dbHelper;

    public void init(Context context, String title) {
        names = new ArrayList<>();
        dbHelper = new DBHelper(context);
        tableName = TAG_TABLE + title;
        caAccount = Objects.requireNonNull(DataRepo.getInstance(context).getMutableCEAAccountList().getValue()).get(title);
        names = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(DataRepo.getInstance(context).getMutableCeaPersonNamesList().getValue()).get(title)));
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3) {
        return v1 && v2 && v3;
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3, boolean v4) {
        return v1 && v2 && v3 && v4;
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3, String s1) {
        return v1 && v2 && v3 && !s1.equals("");
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3, String s1, String s2) {
        return v1 && v2 && v3 && !s1.equals("") && !s2.equals("");
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3, boolean v4, String s1) {
        return v1 && v2 && v3 && v4 && !s1.equals("");
    }

    public boolean isFormValid(boolean v1, boolean v2, boolean v3, boolean v4, boolean v5) {
        return v1 && v2 && v3 && v4 && v5;
    }

    public void saveExpense(int date, String title, double totalCost) {
        ArrayList<Double> amounts = new ArrayList<>();
        for (int i = 0; i < caAccount.getNoOfPerson(); i++) {
            amounts.add(totalCost / caAccount.getNoOfPerson());
        }
        dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, amounts, amounts);
    }


    public void saveExpense(int date, String title, double totalCost, String singleName, int type) {
        if (type == 1) {
            ArrayList<Double> paidAmounts = new ArrayList<>();
            for (int i = 0; i < caAccount.getNoOfPerson(); i++) {
                paidAmounts.add(totalCost / caAccount.getNoOfPerson());
            }
            ArrayList<Double> costAmounts = new ArrayList<>();
            for (String name : names) {
                if (name.equals(singleName)) {
                    costAmounts.add(totalCost);
                } else {
                    costAmounts.add(0.0);
                }
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, paidAmounts);
        } else if (type == 2) {
            ArrayList<Double> costAmounts = new ArrayList<>();
            for (int i = 0; i < caAccount.getNoOfPerson(); i++) {
                costAmounts.add(totalCost / caAccount.getNoOfPerson());
            }
            ArrayList<Double> paidAmounts = new ArrayList<>();
            for (String name : names) {
                if (name.equals(singleName)) {
                    paidAmounts.add(totalCost);
                } else {
                    paidAmounts.add(0.0);
                }
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, paidAmounts);
        }
    }

    public void saveExpense(int date, String title, double totalCost, String singlePaidName, String singleCostName) {
        ArrayList<Double> costAmounts = new ArrayList<>();
        for (String name : names) {
            if (name.equals(singleCostName)) {
                costAmounts.add(totalCost);
            } else {
                costAmounts.add(0.0);
            }
        }
        ArrayList<Double> paidAmounts = new ArrayList<>();
        for (String name : names) {
            if (name.equals(singlePaidName)) {
                paidAmounts.add(totalCost);
            } else {
                paidAmounts.add(0.0);
            }
        }
        dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, paidAmounts);
    }

    public void saveExpense(int date, String title, double totalCost, ArrayList<Double> amounts, int type) {
        if (type == 1) {
            ArrayList<Double> paidAmounts = new ArrayList<>();
            for (int i = 0; i < caAccount.getNoOfPerson(); i++) {
                paidAmounts.add(totalCost / caAccount.getNoOfPerson());
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, amounts, paidAmounts);
        } else if (type == 2) {
            ArrayList<Double> costAmounts = new ArrayList<>();
            for (int i = 0; i < caAccount.getNoOfPerson(); i++) {
                costAmounts.add(totalCost / caAccount.getNoOfPerson());
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, amounts);
        }
    }

    public void saveExpense(int date, String title, double totalCost, String singleName, ArrayList<Double> amounts, int type) {
        if (type == 1) {
            ArrayList<Double> paidAmounts = new ArrayList<>();
            for (String name : names) {
                if (name.equals(singleName)) {
                    paidAmounts.add(totalCost);
                } else {
                    paidAmounts.add(0.0);
                }
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, amounts, paidAmounts);
        } else if (type == 2) {
            ArrayList<Double> costAmounts = new ArrayList<>();
            for (String name : names) {
                if (name.equals(singleName)) {
                    costAmounts.add(totalCost);
                } else {
                    costAmounts.add(0.0);
                }
            }
            dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, amounts);
        }
    }

    public void saveExpense(int date, String title, double totalCost, ArrayList<Double> paidAmounts, ArrayList<Double> costAmounts) {
        dbHelper.saveCEAExpense(tableName, date, title, totalCost, names, costAmounts, paidAmounts);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        dbHelper.close();
    }
}
