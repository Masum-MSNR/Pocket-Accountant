package com.me.pa.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.others.TotalAmountSender;

import java.util.ArrayList;

public class PaidAmountPickerAdapter extends RecyclerView.Adapter<PaidAmountPickerAdapter.ViewHolder> {

    Context context;
    ArrayList<Double> paidAmounts;
    ArrayList<String> names;
    TotalAmountSender sender;

    public PaidAmountPickerAdapter(Context context, ArrayList<String> names, TotalAmountSender sender) {
        this.context = context;
        this.names = names;
        this.sender = sender;
        paidAmounts = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            paidAmounts.add(0.0);
        }
    }

    public double totalAmount() {
        double sum = 0;
        for (double amount : paidAmounts) {
            sum += amount;
        }
        return sum;
    }

    public ArrayList<Double> getPaidAmounts() {
        return paidAmounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_cost_input_layout_1, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_cost_input_layout_2, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int p = h.getAdapterPosition();
        h.singlePaidCostEt.setHint(names.get(p) + context.getString(R.string._s_amount));
        h.singlePaidCostEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 1) {
                    paidAmounts.set(p, 0.0);
                } else {
                    try {
                        paidAmounts.set(p, Double.valueOf(charSequence.toString()));
                    } catch (Exception e) {
                        paidAmounts.set(p, 0.0);
                    }
                }
                sender.getAmounts(totalAmount(), true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return 1;
        } else return 2;
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText singlePaidCostEt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singlePaidCostEt = itemView.findViewById(R.id.single_paid_cost_et);
        }
    }
}
