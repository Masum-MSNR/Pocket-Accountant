package com.me.pa.dialogs;

import static com.me.pa.others.Functions.hideKeyBoard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.me.pa.R;
import com.me.pa.adapters.CostAmountPickerAdapter;
import com.me.pa.adapters.NothingSelectedSpinnerAdapter;
import com.me.pa.adapters.PaidAmountPickerAdapter;
import com.me.pa.databinding.DialogAddCollectiveExpenseBinding;
import com.me.pa.models.CEA;
import com.me.pa.others.DialogCommunicator;
import com.me.pa.others.TotalAmountSender;
import com.me.pa.repos.DataRepo;
import com.me.pa.utils.DatePicker;
import com.me.pa.viewModels.DialogAddCEVM;

public class AddCEDialog extends AppCompatDialogFragment implements TotalAmountSender {

    Context context;
    CEA account;
    DialogAddCollectiveExpenseBinding binding;
    DialogAddCEVM viewModel;
    ArrayAdapter<String> paidNameAdapter, costNameAdapter;
    PaidAmountPickerAdapter paidAmountPickerAdapter;
    CostAmountPickerAdapter costAmountPickerAdapter;
    DatePicker datePicker;
    DialogCommunicator communicator;

    String singlePaidName = "", singleCostName = "", paidType, costType;
    String TYPE_EQUAL, TYPE_SINGLE, TYPE_MULTIPLE;

    boolean isDateAdded = false, isTitleValid = false, isTotalCostValid = false, isPaidRvValid = false, isCostRvValid = false;
    double totalCost = 0;

    public AddCEDialog(Context context, CEA account, DialogCommunicator communicator) {
        this.context = context;
        this.account = account;
        this.communicator = communicator;
        TYPE_EQUAL = context.getString(R.string.equal);
        TYPE_SINGLE = context.getString(R.string.single);
        TYPE_MULTIPLE = context.getString(R.string.multiple);
        paidType = costType = TYPE_EQUAL;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_collective_expense, null);
        binding = DialogAddCollectiveExpenseBinding.bind(view);

        datePicker = new DatePicker();
        datePicker.initDialog(context, binding.dateEt);

        viewModel = new ViewModelProvider(this).get(DialogAddCEVM.class);
        viewModel.init(context, account);


        paidNameAdapter = new ArrayAdapter<>(
                context,
                R.layout.spinner_layout,
                viewModel.getNames());
        paidNameAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        costNameAdapter = new ArrayAdapter<>(
                context,
                R.layout.spinner_layout,
                viewModel.getNames());
        costNameAdapter.setDropDownViewResource(R.layout.spinner_dropdown);

        binding.paidNameSp.setAdapter(new NothingSelectedSpinnerAdapter(
                paidNameAdapter,
                R.layout.spinner_layout_nothing_selected,
                context));
        binding.costNameSp.setAdapter(new NothingSelectedSpinnerAdapter(
                costNameAdapter,
                R.layout.spinner_layout_nothing_selected,
                context));

        paidAmountPickerAdapter = new PaidAmountPickerAdapter(context, viewModel.getNames(), this);
        binding.paidRv.setLayoutManager(new GridLayoutManager(context, 2));
        binding.paidRv.setAdapter(paidAmountPickerAdapter);

        costAmountPickerAdapter = new CostAmountPickerAdapter(context, viewModel.getNames(), this);
        binding.costRv.setLayoutManager(new GridLayoutManager(context, 2));
        binding.costRv.setAdapter(costAmountPickerAdapter);

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

        binding.titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 2) {
                    binding.titleEt.setError(context.getString(R.string.title_error));
                    isTitleValid = false;
                } else {
                    isTitleValid = true;
                }
                updateButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.totalCostEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1) {
                    binding.totalCostEt.setError(context.getString(R.string.total_cost_error));
                    totalCost = 0;
                    isTotalCostValid = false;
                } else {
                    totalCost = Double.parseDouble(charSequence.toString());
                    isTotalCostValid = true;
                }
                updateButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.paidNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                singlePaidName = adapterView.getItemAtPosition(i).toString();
                updateButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.costNameSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                singleCostName = adapterView.getItemAtPosition(i).toString();
                updateButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.radioGroupPaid.setOnCheckedChangeListener((radioGroup, i) -> {
            paidType = ((RadioButton) view.findViewById(i)).getText().toString();
            layoutChanger(paidType, binding.paidRvLl, binding.paidLl);
            updateButton();
        });

        binding.radioGroupCost.setOnCheckedChangeListener((radioGroup, i) -> {
            costType = ((RadioButton) view.findViewById(i)).getText().toString();
            layoutChanger(costType, binding.costRvLl, binding.costLl);
            updateButton();
        });

        binding.dateEt.setOnClickListener(v -> datePicker.showDialog());

        binding.addBt.setOnClickListener(v -> {
            if (!isFormValid()) {
                return;
            }
            saveExpense();
            communicator.send(Integer.parseInt(String.valueOf(datePicker.getNewIntDate()).substring(0, 6)));
            DataRepo.getInstance(context).update(context);
            hideKeyBoard(context, binding.addBt);
            dismiss();
        });

        binding.cancelBt.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        return dialog;
    }

    private void layoutChanger(String type, LinearLayout ll1, LinearLayout ll2) {
        if (type.equals(TYPE_EQUAL)) {
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
        } else if (type.equals(TYPE_SINGLE)) {
            ll2.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
        } else if (type.equals(TYPE_MULTIPLE)) {
            ll2.setVisibility(View.GONE);
            ll1.setVisibility(View.VISIBLE);
        }
    }


    public boolean isFormValid() {
        if (paidType.equals(TYPE_EQUAL)) {
            if (costType.equals(TYPE_EQUAL)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid);
            } else if (costType.equals(TYPE_SINGLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, singleCostName);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, isCostRvValid);
            }
        } else if (paidType.equals(TYPE_SINGLE)) {
            if (costType.equals(TYPE_EQUAL)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, singlePaidName);
            } else if (costType.equals(TYPE_SINGLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, singlePaidName, singleCostName);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, isCostRvValid, singlePaidName);
            }
        } else if (paidType.equals(TYPE_MULTIPLE)) {
            if (costType.equals(TYPE_EQUAL)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, isPaidRvValid);
            } else if (costType.equals(TYPE_SINGLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, isPaidRvValid, singleCostName);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                return viewModel.isFormValid(isDateAdded, isTitleValid, isTotalCostValid, isPaidRvValid, isCostRvValid);
            }
        }
        return false;
    }

    public void saveExpense() {
        int _date = datePicker.getNewIntDate();
        String _title = binding.titleEt.getText().toString();
        double _totalCost = Double.parseDouble(binding.totalCostEt.getText().toString());
        if (paidType.equals(TYPE_EQUAL)) {
            if (costType.equals(TYPE_EQUAL)) {
                viewModel.saveExpense(_date, _title, _totalCost);
            } else if (costType.equals(TYPE_SINGLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, singleCostName, 1);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, costAmountPickerAdapter.getCostAmounts(), 1);
            }
        } else if (paidType.equals(TYPE_SINGLE)) {
            if (costType.equals(TYPE_EQUAL)) {
                viewModel.saveExpense(_date, _title, _totalCost, singlePaidName, 2);
            } else if (costType.equals(TYPE_SINGLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, singlePaidName, singleCostName);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, singlePaidName, costAmountPickerAdapter.getCostAmounts(), 1);
            }
        } else if (paidType.equals(TYPE_MULTIPLE)) {
            if (costType.equals(TYPE_EQUAL)) {
                viewModel.saveExpense(_date, _title, _totalCost, paidAmountPickerAdapter.getPaidAmounts(), 2);
            } else if (costType.equals(TYPE_SINGLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, singleCostName, paidAmountPickerAdapter.getPaidAmounts(), 2);
            } else if (costType.equals(TYPE_MULTIPLE)) {
                viewModel.saveExpense(_date, _title, _totalCost, paidAmountPickerAdapter.getPaidAmounts(), costAmountPickerAdapter.getCostAmounts());
            }
        }
    }

    private void updateButton() {
        binding.addBt.setTextColor(isFormValid() ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
        binding.addBt.setBackgroundTintList(isFormValid() ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
    }

    @Override
    public void getAmounts(double totalAmount, boolean isPaid) {
        if (isPaid) {
            isPaidRvValid = totalAmount == totalCost;
            binding.paidRvUpdateTv.setVisibility(isPaidRvValid ? View.GONE : View.VISIBLE);
            binding.paidRvUpdateTv.setText(String.valueOf((totalCost - totalAmount)));
        } else {
            isCostRvValid = totalAmount == totalCost;
            binding.costRvUpdateTv.setVisibility(isCostRvValid ? View.GONE : View.VISIBLE);
            binding.costRvUpdateTv.setText(String.valueOf((totalCost - totalAmount)));
        }
        updateButton();
    }
}
