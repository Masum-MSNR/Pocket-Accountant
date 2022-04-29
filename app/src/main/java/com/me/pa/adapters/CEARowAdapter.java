package com.me.pa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.databinding.AdapterCollectiveExpenseRowLayoutBinding;
import com.me.pa.models.CEARow;
import com.me.pa.repos.UserRepo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CEARowAdapter extends RecyclerView.Adapter<CEARowAdapter.ViewHolder> {

    Context context;
    ArrayList<CEARow> caRows;
    ArrayList<String> names;
    Locale language;


    public CEARowAdapter(Context context, ArrayList<CEARow> caRows, ArrayList<String> names) {
        this.context = context;
        this.caRows = caRows;
        this.names = names;
        language = Locale.forLanguageTag(UserRepo.getInstance().getLanguage());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_collective_expense_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int p = h.getAdapterPosition();
        h.binding.descriptionTv.setText(caRows.get(p).getDescription());
        String totalCost = context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", caRows.get(p).getTotalCost());
        h.binding.totalCostTv.setText(totalCost);
        h.binding.dateTv.setText(getDate(String.valueOf(caRows.get(p).getDate())));
        h.binding.detailsTv.setText(getDetails(p));
        switch (names.size()) {
            case 5:
                layoutMaker(h.binding.name5Tv, h.binding.paid5Tv, h.binding.cost5Tv, h.binding.constL5, p, 4);
            case 4:
                layoutMaker(h.binding.name4Tv, h.binding.paid4Tv, h.binding.cost4Tv, h.binding.constL4, p, 3);
            case 3:
                layoutMaker(h.binding.name3Tv, h.binding.paid3Tv, h.binding.cost3Tv, h.binding.constL3, p, 2);
            case 2:
                layoutMaker(h.binding.name1Tv, h.binding.paid1Tv, h.binding.cost1Tv, h.binding.constL1, p, 0);
                layoutMaker(h.binding.name2Tv, h.binding.paid2Tv, h.binding.cost2Tv, h.binding.constL2, p, 1);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return caRows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AdapterCollectiveExpenseRowLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterCollectiveExpenseRowLayoutBinding.bind(itemView);
        }
    }

    private void layoutMaker(TextView nameTv, TextView paidTv, TextView costTv, ConstraintLayout constL, int p, int index) {
        nameTv.setText(names.get(index));
        String paid = context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", caRows.get(p).getPaids()[index]);
        paidTv.setText(paid);
        String cost = context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", caRows.get(p).getCosts()[index]);
        costTv.setText(cost);
        constL.setVisibility(View.VISIBLE);
    }

    private String getDetails(int p) {
        String date = caRows.get(p).getTime().substring(0, 6);
        String _date = String.valueOf(caRows.get(p).getDate()).substring(2, 8);
        String details;
        if (_date.equals(date)) {
            details = "(" + caRows.get(p).getEnteredBy() + ") " + String.format(language,"%02d",Integer.parseInt(caRows.get(p).getTime().substring(6, 8))) + ":" + String.format(language,"%02d",Integer.parseInt(caRows.get(p).getTime().substring(8, 10))) + " " +caRows.get(p).getTime().substring(15, 17);
        } else {
            details = "(" + caRows.get(p).getEnteredBy() + ") " + getDate("20" + date) + "  " + String.format(language,"%02d",Integer.parseInt(caRows.get(p).getTime().substring(6, 8))) + ":" + String.format(language,"%02d",Integer.parseInt(caRows.get(p).getTime().substring(8, 10))) + " " + caRows.get(p).getTime().substring(15, 17);
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
