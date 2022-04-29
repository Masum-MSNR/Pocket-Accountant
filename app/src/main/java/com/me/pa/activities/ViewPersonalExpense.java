package com.me.pa.activities;

import static com.me.pa.others.Constants.TAG_ACCOUNT_NAME;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_PEA;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.me.pa.R;
import com.me.pa.databinding.ActivityViewPersonalExpenseBinding;
import com.me.pa.dialogs.AddPEDialog;

import java.util.Objects;

public class ViewPersonalExpense extends AppCompatActivity {

    ActivityViewPersonalExpenseBinding binding;
    AddPEDialog addPEDialog;
    String tableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPersonalExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tableId = getIntent().getStringExtra(TAG_PEA);

        binding.toolbar.setTitle(getIntent().getStringExtra(TAG_ACCOUNT_NAME));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.add_transaction) {
            addPEDialog = new AddPEDialog(this, tableId);
            addPEDialog.setCancelable(false);
            addPEDialog.show(getSupportFragmentManager(),addPEDialog.getTag());
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
    protected void onPause() {
        super.onPause();
        if(addPEDialog!= null){
            addPEDialog.dismiss();
        }
    }
}