package com.me.pa.activities;

import static com.me.pa.others.Constants.TAG_ACCOUNT_NAME;
import static com.me.pa.others.Constants.TAG_PEA;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.me.pa.R;
import com.me.pa.adapters.MonthOfYearAdapter;
import com.me.pa.adapters.PersonalExpenseAdapter;
import com.me.pa.databinding.ActivityViewPersonalExpenseBinding;
import com.me.pa.dialogs.AddPEDialog;
import com.me.pa.models.PersonalExpense;
import com.me.pa.others.DialogCommunicator;
import com.me.pa.others.RVClickListener;
import com.me.pa.viewModels.ViewPeVm;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPersonalExpense extends AppCompatActivity implements RVClickListener, DialogCommunicator {

    ActivityViewPersonalExpenseBinding binding;
    ViewPeVm viewModel;
    AddPEDialog addPEDialog;
    String tableId;

    MonthOfYearAdapter monthOfYearAdapter;
    ArrayList<Integer> months;
    PersonalExpenseAdapter personalExpenseAdapter;
    ArrayList<PersonalExpense> expenses;


    private final Observer<ArrayList<Integer>> monthsObserver = list -> {
        months.clear();
        months.addAll(list);
        monthOfYearAdapter.notifyItemRangeRemoved(0, viewModel.oldMonthRange);
        viewModel.oldMonthRange = list.size();
        monthOfYearAdapter.notifyItemRangeChanged(0, viewModel.oldMonthRange);
        monthOfYearAdapter.update(viewModel.currentMonth);
    };
    private final Observer<ArrayList<PersonalExpense>> expensesObserver = list -> {
        expenses.clear();
        expenses.addAll(list);
        personalExpenseAdapter.notifyItemRangeRemoved(0, viewModel.oldExpensesRange);
        viewModel.oldExpensesRange = list.size();
        personalExpenseAdapter.notifyItemRangeChanged(0, viewModel.oldExpensesRange);
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPersonalExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tableId = getIntent().getStringExtra(TAG_PEA);

        viewModel = new ViewModelProvider(this).get(ViewPeVm.class);
        viewModel.init(tableId);

        months = new ArrayList<>();
        expenses = new ArrayList<>();

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        binding.toolbar.setTitle(getIntent().getStringExtra(TAG_ACCOUNT_NAME));

        monthOfYearAdapter = new MonthOfYearAdapter(this, months, this);
        binding.monthRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.monthRv.setAdapter(monthOfYearAdapter);

        personalExpenseAdapter = new PersonalExpenseAdapter(expenses);
        binding.tableRv.setLayoutManager(new LinearLayoutManager(this));
        binding.tableRv.setAdapter(personalExpenseAdapter);

        viewModel.months.observe(this, monthsObserver);
        viewModel.expenses.observe(this, expensesObserver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.add_transaction) {
            addPEDialog = new AddPEDialog(this, tableId, this);
            addPEDialog.setCancelable(false);
            addPEDialog.show(getSupportFragmentManager(), addPEDialog.getTag());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.personal_expense_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRvClicked(int inT) {
        viewModel.currentMonth = inT;
        viewModel.refreshExpenses();
    }

    @Override
    public void send(int iNt) {
        viewModel.setCurrentMonth(iNt);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (addPEDialog != null) {
            addPEDialog.dismiss();
        }
    }
}