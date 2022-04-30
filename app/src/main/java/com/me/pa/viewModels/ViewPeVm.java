package com.me.pa.viewModels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.me.pa.helpers.DBHelper;
import com.me.pa.models.PersonalExpense;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewPeVm extends AndroidViewModel {

    DBHelper dbHelper;
    public int currentMonth,oldMonthRange=0,oldExpensesRange=0;
    public String tableId;

    public MutableLiveData<ArrayList<Integer>> months;
    public MutableLiveData<ArrayList<PersonalExpense>> expenses;

    public ViewPeVm(@NonNull Application application) {
        super(application);
        dbHelper = new DBHelper(application.getApplicationContext());
        months = new MutableLiveData<>();
        expenses = new MutableLiveData<>();
    }

    public void init(String tableId) {
        this.tableId = tableId;
        loadMonths().addOnSuccessListener(list -> {
            if (list == null)
                return;
            currentMonth = list.get(0);
            months.setValue(list);
            refreshExpenses();
        });
    }

    public void refreshMonths() {
        loadMonths().addOnSuccessListener(list -> {
            if (list == null)
                return;
            months.setValue(list);
            refreshExpenses();
        });
    }

    public void setCurrentMonth(int month) {
        currentMonth = month;
        refreshMonths();
    }

    public void refreshExpenses() {
        loadExpenseByMonth().addOnSuccessListener(list -> {
            expenses.setValue(list);
        });
    }

    private Task<ArrayList<Integer>> loadMonths() {
        TaskCompletionSource<ArrayList<Integer>> tcs = new TaskCompletionSource<>();
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(() -> {
            ArrayList<Integer> personalExpenses = dbHelper.getMonths(tableId);
            new Handler(Looper.getMainLooper()).post(() -> tcs.setResult(personalExpenses));
        });
        return tcs.getTask();
    }

    public Task<ArrayList<PersonalExpense>> loadExpenseByMonth() {
        TaskCompletionSource<ArrayList<PersonalExpense>> tcs = new TaskCompletionSource<>();
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(() -> {
            ArrayList<PersonalExpense> personalExpenses = dbHelper.getExpenseByMonth(tableId, currentMonth);
            new Handler(Looper.getMainLooper()).post(() -> tcs.setResult(personalExpenses));
        });
        return tcs.getTask();
    }
}
