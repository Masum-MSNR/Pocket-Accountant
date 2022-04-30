package com.me.pa.dialogs;

import static com.me.pa.others.Constants.TAG_TABLE;

import android.app.Dialog;
import android.content.Context;
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
import androidx.lifecycle.ViewModelProvider;

import com.me.pa.R;
import com.me.pa.databinding.DialogAddPersonalExpenseBinding;
import com.me.pa.others.DialogCommunicator;
import com.me.pa.utils.DatePicker;
import com.me.pa.viewModels.DialogAddPEVM;

public class AddPEDialog extends AppCompatDialogFragment {

    DialogAddPersonalExpenseBinding binding;
    Context context;
    DialogAddPEVM viewModel;
    DatePicker datePicker;
    String tableId;
    DialogCommunicator communicator;

    int type = 1;

    boolean isDateAdded = false, isDescriptionValid = false, isAmountValid = false;
    double amount = 0;

    public AddPEDialog(Context context, String tableId, DialogCommunicator communicator) {
        this.context = context;
        this.tableId = tableId;
        this.communicator = communicator;
        datePicker = new DatePicker();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_personal_expense, null);
        binding = DialogAddPersonalExpenseBinding.bind(view);

        viewModel = new ViewModelProvider(this).get(DialogAddPEVM.class);

        datePicker.initDialog(context, binding.dateEt);


        binding.incomeTv.setOnClickListener(v -> {
            binding.incomeTv.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
            binding.expenseTv.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
            type = 1;
        });

        binding.expenseTv.setOnClickListener(v -> {
            binding.expenseTv.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.light_gray));
            binding.incomeTv.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.white));
            type = 2;
        });

        binding.dateEt.setOnClickListener(v -> datePicker.showDialog());

        binding.dateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isDateAdded = true;
                updateButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.descriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 2) {
                    binding.descriptionEt.setError(context.getString(R.string.title_error));
                    isDescriptionValid = false;
                } else {
                    isDescriptionValid = true;
                }
                updateButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.amountEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() < 1) {
                    binding.amountEt.setError(context.getString(R.string.total_cost_error));
                    amount = 0;
                    isAmountValid = false;
                } else {
                    amount = Double.parseDouble(charSequence.toString());
                    isAmountValid = true;
                }
                updateButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.addBt.setOnClickListener(v -> {
            if (!isFormValid()) {
                return;
            }
            savePE();
            dismiss();
        });

        binding.cancelBt.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        return dialog;
    }

    private void updateButton() {
        binding.addBt.setTextColor(isFormValid() ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
        binding.addBt.setBackgroundTintList(isFormValid() ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
    }

    private boolean isFormValid() {
        return viewModel.isFormValid(isDateAdded, isDescriptionValid, isAmountValid);
    }

    private void savePE() {
        int date = datePicker.getNewIntDate();
        String description = binding.descriptionEt.getText().toString();
        viewModel.savePE(context, TAG_TABLE + tableId, date, description, amount, type);
        communicator.send(Integer.parseInt(String.valueOf(date).substring(0, 6)));
    }
}