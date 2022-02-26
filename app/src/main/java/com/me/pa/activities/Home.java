package com.me.pa.activities;

import static com.me.pa.others.Constants.IS_DATA_LOADED;
import static com.me.pa.others.Constants.TYPE_OFFLINE;
import static com.me.pa.others.Constants.TYPE_ONLINE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.me.pa.R;
import com.me.pa.adapters.ExpenseAccountAdapter;
import com.me.pa.application.AppRepo;
import com.me.pa.databinding.ActivityHomeBinding;
import com.me.pa.dialogs.AddAccountDialog;
import com.me.pa.dialogs.ConnectCEADialog;
import com.me.pa.helpers.OnlineDBHelper;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.others.Functions;
import com.me.pa.others.RVClickListener;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Home extends AppCompatActivity implements RVClickListener {

    ActivityHomeBinding binding;
    UserRepo userRepo;
    DataRepo dataRepo;
    ActionBarDrawerToggle drawerToggle;
    DrawerLayout drawerLayout;
    ExpenseAccountAdapter adapter;
    View navHeader;
    OnlineDBHelper onlineDBHelper;
    ArrayList<ExpenseAccount> accounts;
    int oldAccountListSize = 0, clickedRvChild = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepo = UserRepo.getInstance();
        userRepo.init(this);
        dataRepo = DataRepo.getInstance(this);

        onlineDBHelper = new OnlineDBHelper(this);

        if (getIntent().getBooleanExtra(IS_DATA_LOADED, false)) {
            onlineDBHelper.getBackUpData();
        }


        setSupportActionBar(binding.homeToolbar);

        navHeader = binding.drawer.getHeaderView(0);
        ((ImageView) navHeader.findViewById(R.id.image_iv)).setImageBitmap(Functions.decodeBase64ToBitmap(userRepo.getImage()));
        ((TextView) navHeader.findViewById(R.id.full_name_tv)).setText(userRepo.getName());
        binding.nameTv.setText(userRepo.getName());


        SimpleDateFormat ft = new SimpleDateFormat("EE, MMM dd", Locale.getDefault());
        String dateTime = ft.format(new Date());
        binding.dateEt.setText(dateTime);


        if (userRepo.getAccountType().equals(TYPE_OFFLINE)) {
            navHeader.findViewById(R.id.number_tv).setVisibility(View.GONE);
        } else {
            ((TextView) navHeader.findViewById(R.id.number_tv)).setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber());
            AppRepo.getInstance().getNetworkAvailable().observe(this, aBoolean -> ((CardView) navHeader.findViewById(R.id.status_indicator)).setCardBackgroundColor(aBoolean ? getColor(R.color.green) : getColor(R.color.gray)));
        }

        drawerLayout = binding.drawerLayout;
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, binding.homeToolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        accounts = new ArrayList<>(Objects.requireNonNull(DataRepo.getInstance(Home.this).getMutableExpenseAccountList().getValue()));
        Collections.reverse(accounts);
        oldAccountListSize = accounts.size();
        adapter = new ExpenseAccountAdapter(Home.this, accounts, this);

        binding.accountsRv.setLayoutManager(new LinearLayoutManager(Home.this));
        binding.accountsRv.setAdapter(adapter);

        dataRepo.getMutableExpenseAccountList().observe(Home.this, expenseAccounts -> {
            accounts.clear();
            accounts.addAll(expenseAccounts);
            Collections.reverse(accounts);

            if (clickedRvChild != -1) {
                adapter.notifyItemChanged(clickedRvChild);
            } else {
                adapter.notifyItemRangeChanged(0, accounts.size());
            }

            if (userRepo.getAccountType().equals(TYPE_ONLINE)) {
                adapter.refreshListeners();
            }
            binding.cv.setVisibility(accounts.size() == 0 ? View.VISIBLE : View.GONE);
        });

        binding.addBt.setOnClickListener(v -> {
            AddAccountDialog addAccountDialog = new AddAccountDialog(this);
            addAccountDialog.setCancelable(false);
            addAccountDialog.show(getSupportFragmentManager(), addAccountDialog.getTag());
        });

        binding.drawer.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.log_out) {
                userRepo.logOut(this);
                dataRepo.initiate(this);
                startActivity(new Intent(this, OnBoarding.class));
                Toast.makeText(this, "Log Out Successful.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (item.getItemId() == R.id.old_cea) {
                ConnectCEADialog connectCEADialog = new ConnectCEADialog(this);
                connectCEADialog.show(getSupportFragmentManager(), connectCEADialog.getTag());
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.add_account) {
            AddAccountDialog addAccountDialog = new AddAccountDialog(this);
            addAccountDialog.setCancelable(false);
            addAccountDialog.show(getSupportFragmentManager(), addAccountDialog.getTag());
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        clickedRvChild = -1;
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destroyListeners();
    }

    @Override
    public void onRvClicked(int inT) {
        clickedRvChild = inT;
    }
}