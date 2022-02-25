package com.me.pa.activities;

import static com.me.pa.others.Constants.FDR_USER_ACCOUNTS;
import static com.me.pa.others.Constants.IS_DATA_LOADED;
import static com.me.pa.others.Constants.KEY_FINISH_ACTIVITY;
import static com.me.pa.others.Constants.TYPE_OFFLINE;
import static com.me.pa.others.Constants.TYPE_ONLINE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.me.pa.R;
import com.me.pa.databinding.ActivityAccountInfoBinding;
import com.me.pa.dialogs.ConnectionErrorDialog;
import com.me.pa.dialogs.ImageCropperDialog;
import com.me.pa.dialogs.LoadingDialog;
import com.me.pa.models.UserAccount;
import com.me.pa.others.Functions;
import com.me.pa.repos.UserRepo;

import java.util.Objects;

public class AccountInfo extends AppCompatActivity implements ImageCropperDialog.BitmapListener {


    ActivityAccountInfoBinding binding;
    ActivityResultLauncher<String> mGetContent;
    UserRepo loggedInUserRepo;
    String base64Image;
    boolean isNameValid = false, isImageLoaded = false;


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


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ImageCropperDialog dialog = new ImageCropperDialog(this, this, uri);
                        dialog.setCancelable(false);
                        dialog.show(getSupportFragmentManager(), dialog.getTag());
                    }
                });

        binding.fullNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 4) {
                    isNameValid = true;
                    binding.startUsingBt.setEnabled(isImageLoaded);
                } else {
                    isNameValid = false;
                    binding.startUsingBt.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.startUsingBt.setOnClickListener(view -> {
            LoadingDialog dialog = new LoadingDialog();
            dialog.show(getSupportFragmentManager(), dialog.getTag());
            if (!isImageLoaded && !isNameValid) {
                return;
            }
            if (loggedInUserRepo.getAccountType().equals(TYPE_ONLINE)) {
                UserAccount account = new UserAccount(loggedInUserRepo.getNumber(), Objects.requireNonNull(binding.fullNameEt.getText()).toString(), loggedInUserRepo.getLanguage(), base64Image, true);
                FirebaseDatabase.getInstance().getReference(FDR_USER_ACCOUNTS).child(loggedInUserRepo.getNumber()).setValue(account).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loggedInUserRepo.setName(binding.fullNameEt.getText().toString());
                        loggedInUserRepo.setImage(base64Image);
                        loggedInUserRepo.setCompleteAccount(true);
                        loggedInUserRepo.finalCommit(this);
                        Intent intent = new Intent(this, Home.class);
                        intent.putExtra(IS_DATA_LOADED,true);
                        startActivity(intent);
                        this.finish();
                    } else {
                        ConnectionErrorDialog connectionErrorDialog = new ConnectionErrorDialog(this);
                        connectionErrorDialog.show(getSupportFragmentManager(), connectionErrorDialog.getTag());
                    }
                });
            } else {
                loggedInUserRepo.setName(Objects.requireNonNull(binding.fullNameEt.getText()).toString());
                loggedInUserRepo.setImage(base64Image);
                loggedInUserRepo.setCompleteAccount(true);
                loggedInUserRepo.initialCommit(this);
                loggedInUserRepo.finalCommit(this);
                Intent intent = new Intent(this, Home.class);
                intent.putExtra(IS_DATA_LOADED,false);
                startActivity(intent);
                sendBroadcast(new Intent(KEY_FINISH_ACTIVITY));
                this.finish();
            }
        });

        binding.choiceImageBt.setOnClickListener(v -> mGetContent.launch("image/*"));
    }

    @Override
    public void getBitmap(Bitmap bitmap) {
        base64Image = Functions.encodeBitmapToBase64(bitmap);
        binding.imageIv.setImageBitmap(bitmap);
        isImageLoaded = true;
        binding.startUsingBt.setEnabled(isNameValid);
    }
}