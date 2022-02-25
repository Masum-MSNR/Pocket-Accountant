package com.me.pa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.databinding.AdapterMonthOfYearLayoutBinding;
import com.me.pa.others.RVClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MonthOfYearAdapter extends RecyclerView.Adapter<MonthOfYearAdapter.ViewHolder> {

    Context context;
    ArrayList<Integer> months;
    RVClickListener listener;
    int selectedIndex = 0;

    public MonthOfYearAdapter(Context context, ArrayList<Integer> months, RVClickListener listener) {
        this.context = context;
        this.months = months;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_month_of_year_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int p = h.getAdapterPosition();
        h.binding.monthYearTv.setText(getMonth(String.valueOf(months.get(p))));
        if (selectedIndex == position) {
            h.binding.getRoot().setBackground(ContextCompat.getDrawable(context, R.drawable.bg_solid_rounded_10));
            h.binding.monthYearTv.setTextColor(ContextCompat.getColor(context, R.color.color_primary));
        } else {
            h.binding.getRoot().setBackground(ContextCompat.getDrawable(context, R.drawable.bg_stocked_rounded_10));
            h.binding.monthYearTv.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        h.binding.getRoot().setOnClickListener(v -> {
            listener.onRvClicked(months.get(p));
            notifyItemChanged(selectedIndex);
            selectedIndex = p;
            notifyItemChanged(selectedIndex);
        });
    }

    public void update(int month) {
        for (int i = 0; i < months.size(); i++) {
            if (months.get(i) == month) {
                if (i == selectedIndex)
                    break;
                int oldIndex = selectedIndex;
                selectedIndex = i;
                notifyItemChanged(selectedIndex);
                notifyItemChanged(oldIndex);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return months.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        AdapterMonthOfYearLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterMonthOfYearLayoutBinding.bind(itemView);
        }
    }

    private String getMonth(String date) {
        String sDate;

        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        sDate = month + "/" + year;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        Date dt = null;
        try {
            dt = simpleDateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat monthF = new SimpleDateFormat("MMM", Locale.getDefault());
        sDate = monthF.format(Objects.requireNonNull(dt)) + ", " + year;
        return sDate;
    }
}

