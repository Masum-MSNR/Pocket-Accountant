package com.me.pa.activities;

import static com.me.pa.others.Constants.TAG_ACCOUNT_NAME;
import static com.me.pa.others.Constants.TAG_CEA;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.me.pa.R;
import com.me.pa.adapters.CEARowAdapter;
import com.me.pa.adapters.MonthOfYearAdapter;
import com.me.pa.databinding.ActivityViewCollectiveAccountBinding;
import com.me.pa.dialogs.AddCEDialog;
import com.me.pa.models.CEA;
import com.me.pa.models.CEARow;
import com.me.pa.others.DialogCommunicator;
import com.me.pa.others.RVClickListener;
import com.me.pa.repos.DataRepo;

import java.util.ArrayList;
import java.util.Objects;

public class ViewCollectiveAccount extends AppCompatActivity implements RVClickListener, DialogCommunicator {

    ActivityViewCollectiveAccountBinding binding;
    CEA account;
    MonthOfYearAdapter monthOfYearAdapter;
    ArrayList<Integer> months;
    ArrayList<CEARow> caRows;
    CEARowAdapter adapter;

    DataRepo dataRepo;

    int month;
    int oldCeaRowRange = 0, oldMonthRange = 0;
    boolean isNewInserted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCollectiveAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        account = (CEA) getIntent().getSerializableExtra(TAG_CEA);
        dataRepo = DataRepo.getInstance(this);

        binding.toolbar.setTitle(getIntent().getStringExtra(TAG_ACCOUNT_NAME));

        months = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATableList().getValue()).get(account.getTableId())).keySet());
        oldMonthRange = months.size();

        monthOfYearAdapter = new MonthOfYearAdapter(this, months, this);
        binding.monthRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.monthRv.setAdapter(monthOfYearAdapter);

        if (months.size() > 0) {
            month = months.get(0);
            caRows = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATableList().getValue().get(account.getTableId())).get(months.get(0))));
            oldCeaRowRange = caRows.size();
        } else {
            caRows = new ArrayList<>();
        }

        adapter = new CEARowAdapter(this, caRows, account.getNames());
        binding.tableRv.setLayoutManager(new LinearLayoutManager(this));
        binding.tableRv.setAdapter(adapter);


        dataRepo.getMutableCEATableList().observe(ViewCollectiveAccount.this, stringLinkedHashMapLinkedHashMap -> {
            if (Objects.requireNonNull(stringLinkedHashMapLinkedHashMap.get(account.getTableId())).size() != oldMonthRange) {
                months.clear();
                months.addAll(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATableList().getValue()).get(account.getTableId())).keySet());
                monthOfYearAdapter.notifyItemRangeRemoved(0, oldMonthRange);
                monthOfYearAdapter.notifyItemRangeChanged(0, months.size());
                oldMonthRange = months.size();
            }

            if (isNewInserted) {
                isNewInserted = false;
                monthOfYearAdapter.update(month);
            }

            if (Objects.requireNonNull(stringLinkedHashMapLinkedHashMap.get(account.getTableId())).get(month) != null) {
                caRows.clear();
                caRows.addAll(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATableList().getValue().get(account.getTableId())).get(month)));
                adapter.notifyItemRangeRemoved(0, oldCeaRowRange);
                adapter.notifyItemRangeChanged(0, caRows.size());
                oldCeaRowRange = caRows.size();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.add_expense) {
            AddCEDialog addExpenseDialog = new AddCEDialog(this, account, this);
            addExpenseDialog.setCancelable(false);
            addExpenseDialog.show(getSupportFragmentManager(), addExpenseDialog.getTag());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.collective_expense_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRvClicked(int inT) {
        month = inT;
        caRows.clear();
        caRows.addAll(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATableList().getValue()).get(account.getTableId())).get(month)));

        adapter.notifyItemRangeRemoved(0, oldCeaRowRange);
        adapter.notifyItemRangeChanged(0, caRows.size());
        oldCeaRowRange = caRows.size();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void send(int iNt) {
        month = iNt;
        isNewInserted = true;
    }
}