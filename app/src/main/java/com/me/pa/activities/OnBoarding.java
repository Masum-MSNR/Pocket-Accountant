package com.me.pa.activities;

import static com.me.pa.others.Functions.isConnected;
import static com.me.pa.others.Constants.KEY_FINISH_ACTIVITY;
import static com.me.pa.others.Constants.TYPE_OFFLINE;
import static com.me.pa.others.Constants.TYPE_ONLINE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.ViewModelProvider;

import com.me.pa.R;
import com.me.pa.databinding.ActivityOnBoardingBinding;
import com.me.pa.dialogs.ConnectionErrorDialog;
import com.me.pa.repos.UserRepo;
import com.me.pa.viewModels.OnBoardingViewModel;

public class OnBoarding extends AppCompatActivity {

    ActivityOnBoardingBinding binding;
    OnBoardingViewModel viewModel;
    UserRepo loggedInUserRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnBoardingViewModel.class);
        viewModel.init(this);

        loggedInUserRepo = UserRepo.getInstance();

        //text
        binding.titleTv.setText(R.string.splash_screen_title);
        binding.bottomTitleTv.setText(R.string.splash_screen_bottom_title);
        binding.onlineBt.setText(R.string.online);
        binding.offlineBt.setText(R.string.offline);
        //text

        viewModel.getLocalLanguage().observe(this, s -> refresh());

        if (viewModel.getLocal().equals("en")) {
            switchTo("en");
        } else if (viewModel.getLocal().equals("bn")) {
            switchTo("bn");
        }

        binding.englishTv.setOnClickListener(v -> {
            if (viewModel.getLocal().equals("bn")) {
                switchTo("en");
                viewModel.changeLanguage(this, "en");
            }

        });

        binding.banglaTv.setOnClickListener(v -> {
            if (viewModel.getLocal().equals("en")) {
                switchTo("bn");
                viewModel.changeLanguage(this, "bn");
            }
        });

        binding.offlineBt.setOnClickListener(v -> {
            loggedInUserRepo.setAccountType(TYPE_OFFLINE);
            loggedInUserRepo.setLanguage(viewModel.getLocal());
            Intent intent = new Intent(this, AccountInfo.class);
            startActivity(intent);
        });

        binding.onlineBt.setOnClickListener(v -> {

            if (!isConnected(this)) {
                ConnectionErrorDialog connectionErrorDialog = new ConnectionErrorDialog(this);
                connectionErrorDialog.show(getSupportFragmentManager(), connectionErrorDialog.getTag());
                return;
            }
            loggedInUserRepo.setAccountType(TYPE_ONLINE);
            startActivity(new Intent(this, Authentication.class));
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(KEY_FINISH_ACTIVITY)) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(KEY_FINISH_ACTIVITY));
    }

    private void switchTo(@NonNull String language) {
        loggedInUserRepo.setLanguage(language);
        binding.englishTv.setTextColor(getColor(language.equals("en") ? R.color.white : R.color.color_primary));
        binding.banglaTv.setTextColor(getColor(language.equals("bn") ? R.color.white : R.color.color_primary));
        binding.englishTv.setBackground(language.equals("en") ? AppCompatResources.getDrawable(this, R.drawable.bg_end_rounded_25) : null);
        binding.banglaTv.setBackground(language.equals("bn") ? AppCompatResources.getDrawable(this, R.drawable.bg_start_rounded_25) : null);
    }

    private void refresh() {
        binding.titleTv.setText(R.string.splash_screen_title);
        binding.bottomTitleTv.setText(R.string.splash_screen_bottom_title);
        binding.onlineBt.setText(R.string.online);
        binding.offlineBt.setText(R.string.offline);
    }


}