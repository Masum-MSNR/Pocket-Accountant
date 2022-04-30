package com.me.pa.adapters;

import static com.me.pa.others.Constants.TAG_ACCOUNT_NAME;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_PEA;
import static com.me.pa.others.Constants.TAG_TABLE;
import static com.me.pa.others.Constants.TYPE_CEA;
import static com.me.pa.others.Constants.TYPE_OFFLINE;
import static com.me.pa.others.Constants.TYPE_PEA;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.me.pa.R;
import com.me.pa.activities.ViewCollectiveAccount;
import com.me.pa.activities.ViewPersonalExpense;
import com.me.pa.databinding.AdapterCollectiveExpenseAccountLayoutBinding;
import com.me.pa.databinding.AdapterSingleExpenseLayoutBinding;
import com.me.pa.helpers.DBHelper;
import com.me.pa.helpers.DBSynchronizer;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.models.SingleCost;
import com.me.pa.others.RVClickListener;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ExpenseAccountAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<ExpenseAccount> accounts;
    ArrayList<DBSynchronizer> synchronizers;
    ArrayList<ExpenseAccount> ceas;
    DataRepo dataRepo;
    UserRepo userRepo;
    RVClickListener listener;
    Locale language;


    public ExpenseAccountAdapter(Context context, ArrayList<ExpenseAccount> accounts, RVClickListener listener) {
        this.context = context;
        this.accounts = accounts;
        this.listener = listener;
        synchronizers = new ArrayList<>();
        ceas = new ArrayList<>();
        dataRepo = DataRepo.getInstance(context);
        userRepo = UserRepo.getInstance();
        language = Locale.forLanguageTag(UserRepo.getInstance().getLanguage());

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case 1:
                View pea = LayoutInflater.from(context).inflate(R.layout.adapter_single_expense_layout, parent, false);
                viewHolder = new PEAViewHolder(pea);
                break;
            case 2:
                View cea = LayoutInflater.from(context).inflate(R.layout.adapter_collective_expense_account_layout, parent, false);
                viewHolder = new CEAViewHolder(cea);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int p = holder.getAdapterPosition();
        switch (holder.getItemViewType()) {
            case 1:
                DBHelper dbHelper = new DBHelper(context);
                PEAViewHolder ph = (PEAViewHolder) holder;
                ph.binding.expenseTitleTv.setText(accounts.get(p).getAccountTitle());
                double expenseD = dbHelper.sumColumn(TAG_TABLE + accounts.get(p).getTableId(), "expense");
                String expense = context.getString(R.string._expense) + (expenseD != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", expenseD) : " - ");
                ph.binding.expenseTv.setText(Html.fromHtml(expense));
                double incomeD = dbHelper.sumColumn(TAG_TABLE + accounts.get(p).getTableId(), "income");
                String income = context.getString(R.string._income) + (incomeD != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", incomeD) : " - ");
                ph.binding.incomeTv.setText(Html.fromHtml(income));
                ph.itemView.setOnClickListener(v -> {
                    listener.onRvClicked(p);
                    activityLauncher(accounts.get(p));
                });
                break;
            case 2:
                CEAViewHolder ch = (CEAViewHolder) holder;
                ch.binding.accountNameTv.setText(accounts.get(p).getAccountTitle());
                double totalCostD = Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATotalCostList().getValue()).get(accounts.get(p).getTableId()));

                String totalCost = (totalCostD != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", totalCostD) : " - ");
                ch.binding.totalCostTv.setText(totalCost);

                ArrayList<SingleCost> personExpenses = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEAPersonsExpenseList().getValue()).get(accounts.get(p).getTableId())));
                int noOfPerson = personExpenses.size();

                switch (noOfPerson) {
                    case 5:
                        layoutMaker(ch.binding.name5Tv, ch.binding.paid5Tv, ch.binding.cost5Tv, ch.binding.receive5Tv, ch.binding.due5Tv, ch.binding.ll5, personExpenses.get(4));
                    case 4:
                        layoutMaker(ch.binding.name4Tv, ch.binding.paid4Tv, ch.binding.cost4Tv, ch.binding.receive4Tv, ch.binding.due4Tv, ch.binding.ll4, personExpenses.get(3));
                        if (noOfPerson == 4) {
                            ch.binding.ll5.setVisibility(View.GONE);
                        }
                    case 3:
                        layoutMaker(ch.binding.name3Tv, ch.binding.paid3Tv, ch.binding.cost3Tv, ch.binding.receive3Tv, ch.binding.due3Tv, ch.binding.ll3, personExpenses.get(2));
                        if (noOfPerson == 3) {
                            ch.binding.ll5.setVisibility(View.GONE);
                            ch.binding.ll4.setVisibility(View.GONE);
                        }
                    case 2:
                        layoutMaker(ch.binding.name1Tv, ch.binding.paid1Tv, ch.binding.cost1Tv, ch.binding.receive1Tv, ch.binding.due1Tv, ch.binding.ll1, personExpenses.get(0));
                        layoutMaker(ch.binding.name2Tv, ch.binding.paid2Tv, ch.binding.cost2Tv, ch.binding.receive2Tv, ch.binding.due2Tv, ch.binding.ll2, personExpenses.get(1));
                        if (noOfPerson == 2) {
                            ch.binding.ll5.setVisibility(View.GONE);
                            ch.binding.ll4.setVisibility(View.GONE);
                            ch.binding.ll3.setVisibility(View.GONE);
                        }
                        break;
                }

                ch.itemView.setOnClickListener(v -> {
                    listener.onRvClicked(p);
                    activityLauncher(accounts.get(p));
                });
                ch.itemView.setOnLongClickListener(view -> {
                    if (userRepo.getAccountType().equals(TYPE_OFFLINE))
                        return false;
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    String id = accounts.get(p).getTableId() + userRepo.getNumber().substring(4);
                    ClipData clip = ClipData.newPlainText("Copied Text", id);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Id copied.", Toast.LENGTH_SHORT).show();
                    return true;
                });
                break;
        }
    }


    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (accounts.get(position).getType().equals(TYPE_PEA))
            return 1;
        else if (accounts.get(position).getType().equals(TYPE_CEA))
            return 2;
        return -1;
    }


    public static class PEAViewHolder extends RecyclerView.ViewHolder {

        AdapterSingleExpenseLayoutBinding binding;

        public PEAViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterSingleExpenseLayoutBinding.bind(itemView);
        }
    }

    public static class CEAViewHolder extends RecyclerView.ViewHolder {

        AdapterCollectiveExpenseAccountLayoutBinding binding;

        public CEAViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterCollectiveExpenseAccountLayoutBinding.bind(itemView);
        }
    }

    private void activityLauncher(ExpenseAccount expenseAccount) {
        Intent intent;
        switch (expenseAccount.getType()) {
            case "pea":
                intent = new Intent(context, ViewPersonalExpense.class);
                intent.putExtra(TAG_PEA, expenseAccount.getTableId());
                intent.putExtra(TAG_ACCOUNT_NAME, expenseAccount.getAccountTitle());
                context.startActivity(intent);
                break;
            case "cea":
                intent = new Intent(context, ViewCollectiveAccount.class);
                intent.putExtra(TAG_CEA, Objects.requireNonNull(dataRepo.getMutableCEAList().getValue()).get(expenseAccount.getTableId()));
                intent.putExtra(TAG_ACCOUNT_NAME, expenseAccount.getAccountTitle());
                context.startActivity(intent);
                break;
//            case "hea":
//                break;
        }
    }

    private void layoutMaker(TextView nameTv, TextView paidTv, TextView costTv, TextView owingTv, TextView dueTv, LinearLayout ll, SingleCost personExpense) {
        nameTv.setText(personExpense.getName());
        String paid = context.getString(R.string.paid) + (personExpense.getPaid() != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", personExpense.getPaid()) : " - ");
        paidTv.setText(Html.fromHtml(paid));
        String cost = context.getString(R.string.cost) + (personExpense.getCost() != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", personExpense.getCost()) : " - ");
        costTv.setText(Html.fromHtml(cost));
        String owing = context.getString(R.string.owing) + (personExpense.getReceive() != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", personExpense.getReceive()) : " - ");
        owingTv.setText(Html.fromHtml(owing));
        String due = context.getString(R.string.due) + (personExpense.getDue() != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", personExpense.getDue()) : " - ");
        dueTv.setText(Html.fromHtml(due));
        ll.setVisibility(View.VISIBLE);
    }

    public void refreshListeners() {
        for (ExpenseAccount expenseAccount : accounts) {
            if (expenseAccount.getType().equals(TYPE_CEA)) {
                if (!ceas.contains(expenseAccount)) {
                    ceas.add(expenseAccount);
                    DBSynchronizer synchronizer = new DBSynchronizer(context);
                    synchronizer.start(expenseAccount.getTableId(), Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEAList().getValue()).get(expenseAccount.getTableId())).getNames());
                    synchronizers.add(synchronizer);
                }
            }
        }
    }

    public void destroyListeners() {
        for (DBSynchronizer synchronizer : synchronizers) {
            synchronizer.stop();
        }
    }
}
