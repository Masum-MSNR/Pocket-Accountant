package com.me.pa.dialogs;

import static com.me.pa.others.Functions.hideKeyBoard;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.me.pa.R;
import com.me.pa.adapters.NothingSelectedSpinnerAdapter;
import com.me.pa.adapters.PersonNameAdapter;
import com.me.pa.databinding.DialogAddAccountBinding;
import com.me.pa.helpers.DBHelper;
import com.me.pa.others.RVValidityListener;
import com.me.pa.repos.DataRepo;
import com.me.pa.viewModels.DialogAddAccountVM;

public class AddAccountDialog extends AppCompatDialogFragment implements RVValidityListener {

    Context context;
    PersonNameAdapter personNameAdapter;
    DialogAddAccountBinding binding;
    DialogAddAccountVM viewModel;
    String accountType = "";
    boolean isFromValid = false, collectiveExpenseValidity = false;
    DBHelper dbHelper;
    Drawable errorIcon;
    String[] types;
    ArrayAdapter<String> accountTypes;

    public AddAccountDialog(Context context) {
        this.context = context;
        types = context.getResources().getStringArray(R.array.account_types);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_account, null);

        dbHelper = new DBHelper(context);

        viewModel = new ViewModelProvider(this).get(DialogAddAccountVM.class);

        errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_error);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, 50, 50);
        }

        binding = DialogAddAccountBinding.bind(view);
        binding.personDetailsRv.setLayoutManager(new LinearLayoutManager(context));
        personNameAdapter = new PersonNameAdapter(context, this);
        binding.personDetailsRv.setAdapter(personNameAdapter);

        accountTypes = new ArrayAdapter<>(
                context,
                R.layout.spinner_layout,
                getResources().getStringArray(R.array.account_types));
        accountTypes.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.accountTypeSp.setAdapter(new NothingSelectedSpinnerAdapter(
                accountTypes,
                R.layout.spinner_layout_nothing_selected_sat,
                context));

        binding.accountTitleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int no = 0;
                try {
                    no = Integer.parseInt(binding.noOfPeopleEt.getText().toString());
                } catch (Exception ignored) {

                }
                if (!viewModel.isAccountTitleValid(charSequence.toString())) {
                    binding.accountTitleEt.setError(getString(R.string.title_error), errorIcon);
                }
                if (accountType.equals(types[1])) {
                    isFromValid = viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), viewModel.isNoOfPeopleValid(no));
                    binding.nextBt.setTextColor(isFromValid ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
                    binding.nextBt.setBackgroundTintList(isFromValid ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
                } else {
                    isFromValid = viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), accountType, types);
                    binding.createBt.setTextColor(isFromValid ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
                    binding.createBt.setBackgroundTintList(isFromValid ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.noOfPeopleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int no = 0;
                try {
                    no = Integer.parseInt(charSequence.toString());
                } catch (Exception ignored) {

                }

                if (!viewModel.isNoOfPeopleValid(no)) {
                    binding.noOfPeopleEt.setError(no < 2 ? getString(R.string.nop_error_1) : getString(R.string.nop_error_2), errorIcon);
                }
                isFromValid = viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), viewModel.isNoOfPeopleValid(no));
                binding.nextBt.setTextColor(isFromValid ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
                binding.nextBt.setBackgroundTintList(isFromValid ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.accountTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                accountType = adapterView.getItemAtPosition(i).toString();
                layoutChanger();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.nextBt.setOnClickListener(v -> {
            if (!isFromValid)
                return;
            binding.accountTitleEt.setVisibility(View.GONE);
            binding.accountTypeSp.setVisibility(View.GONE);
            binding.view.setVisibility(View.GONE);
            binding.noOfPeopleEt.setVisibility(View.GONE);
            binding.nextBt.setVisibility(View.GONE);
            personNameAdapter.setSize(Integer.parseInt(binding.noOfPeopleEt.getText().toString()));
            binding.personDetailsRv.setVisibility(View.VISIBLE);
            binding.createBt.setVisibility(View.VISIBLE);
        });

        binding.createBt.setOnClickListener(v -> {
            if (accountType.equals(types[0])) {
                Toast.makeText(context, "Personal Expense", Toast.LENGTH_SHORT).show();
            } else if (accountType.equals(types[1])) {
                if (!collectiveExpenseValidity) {
                    return;
                }
                dbHelper.createNewCollectiveAccount(binding.accountTitleEt.getText().toString(), Integer.parseInt(binding.noOfPeopleEt.getText().toString()), personNameAdapter.getNames(), true);
                DataRepo.getInstance(context).update(context);
                dbHelper.close();
                hideKeyBoard(context, binding.createBt);
                dismiss();
            } else if (accountType.equals(types[2])) {
                Toast.makeText(context, "Hostel Expense", Toast.LENGTH_SHORT).show();
            }
        });

        binding.cancelBt.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        return dialog;
    }

    public void layoutChanger() {
        if (accountType.equals(types[0])) {
            binding.createBt.setTextColor(viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), accountType, types) ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
            binding.createBt.setBackgroundTintList(viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), accountType, types) ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
            binding.createBt.setVisibility(View.VISIBLE);
            binding.noOfPeopleEt.setVisibility(View.GONE);
            binding.nextBt.setVisibility(View.GONE);
        } else if (accountType.equals(types[1])) {
            binding.noOfPeopleEt.setText("");
            binding.noOfPeopleEt.setError(null);
            binding.nextBt.setTextColor(getResources().getColor(R.color.gray, null));
            binding.createBt.setVisibility(View.GONE);
            binding.noOfPeopleEt.setVisibility(View.VISIBLE);
            binding.nextBt.setVisibility(View.VISIBLE);
        } else if (accountType.equals(types[2])) {
            binding.createBt.setTextColor(viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), accountType, types) ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
            binding.createBt.setBackgroundTintList(viewModel.isFormValid(viewModel.isAccountTitleValid(binding.accountTitleEt.getText().toString()), accountType, types) ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
            binding.createBt.setVisibility(View.VISIBLE);
            binding.noOfPeopleEt.setVisibility(View.GONE);
            binding.nextBt.setVisibility(View.GONE);
        }
    }

    @Override
    public void isValid(boolean validity) {
        collectiveExpenseValidity = validity;
        binding.createBt.setTextColor(collectiveExpenseValidity ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
        binding.createBt.setBackgroundTintList(collectiveExpenseValidity ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
    }
}
