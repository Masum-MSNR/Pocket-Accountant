package com.me.pa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.me.pa.R;
import com.me.pa.databinding.DialogConnectExistingCeaBinding;
import com.me.pa.helpers.OnlineDBHelper;

public class ConnectCEADialog extends AppCompatDialogFragment {

    Context context;
    Drawable errorIcon;
    DialogConnectExistingCeaBinding binding;
    boolean isValid = false;


    public ConnectCEADialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_connect_existing_cea, null);

        errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_error);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, 50, 50);
        }

        binding = DialogConnectExistingCeaBinding.bind(view);


        binding.accountIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isValid = charSequence.length() != 0;
                binding.connectBt.setTextColor(isValid ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
                binding.connectBt.setBackgroundTintList(isValid ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.connectBt.setOnClickListener(v -> {
            if (!isValid)
                return;
            OnlineDBHelper onlineDBHelper = new OnlineDBHelper(context);
            onlineDBHelper.getExistingCEA(binding.accountIdEt.getText().toString());
            dismiss();
        });


        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        return dialog;
    }
}
