package com.me.pa.activities;

import static com.me.pa.others.Functions.isConnected;
import static com.me.pa.others.Constants.KEY_FINISH_ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.me.pa.R;
import com.me.pa.databinding.ActivityAuthenticationBinding;
import com.me.pa.dialogs.OtpDialog;
import com.me.pa.others.DismissListener;
import com.me.pa.others.TriggerListener;
import com.me.pa.utils.ConnectionErrorSheetController;

import java.util.Objects;

public class Authentication extends AppCompatActivity implements DismissListener, TriggerListener {

    ActivityAuthenticationBinding binding;
    ConnectionErrorSheetController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new ConnectionErrorSheetController(findViewById(R.id.connection_error_bottom_sheet));

        binding.mobileNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("0")) {
                    binding.mobileNumberEt.setText("");
                }
                binding.continueBt.setEnabled(Objects.requireNonNull(binding.mobileNumberEt.getText()).toString().trim().length() >= 10 && charSequence.charAt(0) == '1');
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.continueBt.setOnClickListener(v -> {
            if (!isConnected(this)) {
                onTrigger();
                return;
            }
            controller.forceCollapse();
            OtpDialog otpDialog = new OtpDialog(this, "+880" + Objects.requireNonNull(binding.mobileNumberEt.getText()).toString().trim(), this, this);
            otpDialog.setCancelable(false);
            otpDialog.show(getSupportFragmentManager(), otpDialog.getTag());
        });

        findViewById(R.id.close_ibt).setOnClickListener(v -> controller.forceCollapse());
    }


    @Override
    public void onDismiss() {
        sendBroadcast(new Intent(KEY_FINISH_ACTIVITY));
        finish();
    }

    @Override
    public void onTrigger() {
        controller.expande();
    }
}