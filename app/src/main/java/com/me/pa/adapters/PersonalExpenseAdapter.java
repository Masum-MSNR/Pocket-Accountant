package com.me.pa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.databinding.AdapterPersonalExpenseBinding;
import com.me.pa.models.PersonalExpense;
import com.me.pa.repos.UserRepo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PersonalExpenseAdapter extends RecyclerView.Adapter<PersonalExpenseAdapter.ViewHolder> {

    Context context;
    ArrayList<PersonalExpense> expenses;
    Locale language;

    public PersonalExpenseAdapter(ArrayList<PersonalExpense> expenses) {
        this.expenses = expenses;
        language = Locale.forLanguageTag(UserRepo.getInstance().getLanguage());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_personal_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int p = h.getAdapterPosition();
        h.binding.descriptionTv.setText(expenses.get(p).getDescription());
        if (expenses.get(p).getType() == 1) {
            h.binding.iOrETv.setText(context.getString(R.string.income));
            h.binding.iOrETv.setTextColor(context.getColor(R.color.green));
            h.binding.amountTv.setText(String.valueOf(expenses.get(p).getIncome()));
        } else {
            h.binding.iOrETv.setText(context.getString(R.string.expense));
            h.binding.iOrETv.setTextColor(context.getColor(R.color.red));
            h.binding.amountTv.setText(String.valueOf(expenses.get(p).getExpense()));
        }
        h.binding.dateTv.setText(getDate(String.valueOf(expenses.get(p).getDate())));
        h.binding.detailsTv.setText(getDetails(p));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AdapterPersonalExpenseBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterPersonalExpenseBinding.bind(itemView);
        }
    }

    private String getDetails(int p) {
        String date = expenses.get(p).getTime().substring(0, 6);
        String _date = String.valueOf(expenses.get(p).getDate()).substring(2, 8);
        String details;
        if (_date.equals(date)) {
            details = String.format(language, "%02d", Integer.parseInt(expenses.get(p).getTime().substring(6, 8))) + ":" + String.format(language, "%02d", Integer.parseInt(expenses.get(p).getTime().substring(8, 10))) + " " + expenses.get(p).getTime().substring(15, 17);
        } else {
            details = getDate("20" + date) + "  " + String.format(language, "%02d", Integer.parseInt(expenses.get(p).getTime().substring(6, 8))) + ":" + String.format(language, "%02d", Integer.parseInt(expenses.get(p).getTime().substring(8, 10))) + " " + expenses.get(p).getTime().substring(15, 17);
        }
        return details;
    }

    private String getDate(String date) {
        String sDate;
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        sDate = day + "/" + month + "/" + year;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", language);
        Date dt = null;
        try {
            dt = simpleDateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dayF = new SimpleDateFormat("EE", language);
        DateFormat monthF = new SimpleDateFormat("MMM", language);
        sDate = dayF.format(Objects.requireNonNull(dt)) + ", " + monthF.format(dt) + " " + String.format(language, "%d", Integer.valueOf(day));
        return sDate;
    }
}
