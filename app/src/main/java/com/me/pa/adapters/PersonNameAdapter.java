package com.me.pa.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.databinding.AdapterPersonDetailsLayoutBinding;
import com.me.pa.others.Functions;
import com.me.pa.others.RVValidityListener;

import java.util.ArrayList;

public class PersonNameAdapter extends RecyclerView.Adapter<PersonNameAdapter.ViewHolder> {

    Context context;
    int size;
    ArrayList<String> names;
    RVValidityListener listener;
    Drawable errorIcon;


    public PersonNameAdapter(Context context, RVValidityListener listener) {
        this.context = context;
        this.listener = listener;
        size = 0;
        names = new ArrayList<>();
        errorIcon = ContextCompat.getDrawable(context, R.drawable.ic_error);
        if (errorIcon != null) {
            errorIcon.setBounds(0, 0, 50, 50);
        }
    }

    public void setSize(int size) {
        this.size = size;
        names.clear();
        for (int i = 0; i < size; i++) {
            names.add("");
        }
        listener.isValid(isNamesValid());
        notifyItemRangeChanged(0, size);
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public boolean isNamesValid() {
        for (String name : names) {
            int count = 0;
            for (String _name : names) {
                if (_name.length() < 5) {
                    return false;
                }
                if (name.equalsIgnoreCase(_name)) {
                    count++;
                    if (count > 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_person_details_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.personNameEt.setHint(Functions.convertNumberDOL(String.valueOf((position + 1))) + context.getString(R.string.name));
        holder.binding.personNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() < 5) {
                    holder.binding.personNameEt.setError(context.getString(R.string.person_name_error), errorIcon);
                }
                names.set(holder.getAdapterPosition(), charSequence.toString().replaceAll(" ", "_"));
                listener.isValid(isNamesValid());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AdapterPersonDetailsLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterPersonDetailsLayoutBinding.bind(itemView);
        }
    }

}
