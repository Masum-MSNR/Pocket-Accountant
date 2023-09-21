package com.me.pa.activities;

import static com.me.pa.others.Constants.FDR_USER_ACCOUNTS;
import static com.me.pa.others.Constants.IS_DATA_LOADED;
import static com.me.pa.others.Constants.KEY_FINISH_ACTIVITY;
import static com.me.pa.others.Constants.TYPE_OFFLINE;
import static com.me.pa.others.Constants.TYPE_ONLINE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.me.pa.R;
import com.me.pa.databinding.ActivityAccountInfoBinding;
import com.me.pa.dialogs.ConnectionErrorDialog;
import com.me.pa.dialogs.LoadingDialog;
import com.me.pa.models.UserAccount;
import com.me.pa.repos.UserRepo;

import java.util.Objects;

public class AccountInfo extends AppCompatActivity {


    ActivityAccountInfoBinding binding;
    UserRepo loggedInUserRepo;
    boolean isNameValid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loggedInUserRepo = UserRepo.getInstance();
        if (loggedInUserRepo.getAccountType().equals(TYPE_OFFLINE)) {
            binding.congratulationsTv.setVisibility(View.INVISIBLE);
            binding.commandTv.setText(getString(R.string.account_created_command_2));
        }


        binding.fullNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isNameValid = charSequence.length() > 4;
                binding.startUsingBt.setEnabled(isNameValid);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.startUsingBt.setOnClickListener(view -> {
            LoadingDialog dialog = new LoadingDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            if (!isNameValid) {
                return;
            }
            if (loggedInUserRepo.getAccountType().equals(TYPE_ONLINE)) {
                UserAccount account = new UserAccount(loggedInUserRepo.getNumber(), Objects.requireNonNull(binding.fullNameEt.getText()).toString(), loggedInUserRepo.getLanguage(), true);
                FirebaseDatabase.getInstance().getReference(FDR_USER_ACCOUNTS).child(loggedInUserRepo.getNumber()).setValue(account).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loggedInUserRepo.setName(binding.fullNameEt.getText().toString());
                        loggedInUserRepo.setCompleteAccount(true);
                        loggedInUserRepo.finalCommit(this);
                        Intent intent = new Intent(this, Home.class);
                        intent.putExtra(IS_DATA_LOADED, true);
                        startActivity(intent);
                        this.finish();
                    } else {
                        ConnectionErrorDialog connectionErrorDialog = new ConnectionErrorDialog(this);
                        connectionErrorDialog.show(getSupportFragmentManager(), connectionErrorDialog.getTag());
                    }
                });
            } else {
                loggedInUserRepo.setName(Objects.requireNonNull(binding.fullNameEt.getText()).toString());
                loggedInUserRepo.setCompleteAccount(true);
                loggedInUserRepo.initialCommit(this);
                loggedInUserRepo.finalCommit(this);
                Intent intent = new Intent(this, Home.class);
                intent.putExtra(IS_DATA_LOADED, false);
                startActivity(intent);
                sendBroadcast(new Intent(KEY_FINISH_ACTIVITY));
                this.finish();
            }
        });

    }
}