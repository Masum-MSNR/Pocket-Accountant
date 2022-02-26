package com.me.pa.adapters;

import static com.me.pa.others.Constants.FDR_CEA_TABLES;
import static com.me.pa.others.Constants.TAG_ACCOUNT_NAME;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_CURRENT_RC;
import static com.me.pa.others.Constants.TAG_OLD_RC;
import static com.me.pa.others.Constants.TAG_PHONE_NUMBER;
import static com.me.pa.others.Constants.TAG_ROW_COUNT;
import static com.me.pa.others.Constants.TAG_TABLE;
import static com.me.pa.others.Constants.TAG_TABLE_NAME;
import static com.me.pa.others.Constants.TAG_TYPE;
import static com.me.pa.others.Constants.TYPE_CEA_TABLE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.pa.R;
import com.me.pa.activities.ViewCollectiveAccount;
import com.me.pa.databinding.AdapterCollectiveExpenseAccountLayoutBinding;
import com.me.pa.helpers.DBHelper;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.models.PersonExpense;
import com.me.pa.others.RVClickListener;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;
import com.me.pa.services.DataLoaderServiceB;
import com.me.pa.services.DataLoaderServiceF;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ExpenseAccountAdapter extends RecyclerView.Adapter<ExpenseAccountAdapter.ViewHolder> {

    Context context;
    ArrayList<ExpenseAccount> accounts;
    ArrayList<ValueEventListener> listeners;
    ArrayList<DatabaseReference> references;
    DataRepo dataRepo;
    UserRepo userRepo;
    RVClickListener listener;
    Locale language;

    int calledListenersSize = 0;


    public ExpenseAccountAdapter(Context context, ArrayList<ExpenseAccount> accounts, RVClickListener listener) {
        this.context = context;
        this.accounts = accounts;
        this.listener = listener;
        dataRepo = DataRepo.getInstance(context);
        userRepo = UserRepo.getInstance();
        references = new ArrayList<>();
        listeners = new ArrayList<>();
        language = Locale.forLanguageTag(UserRepo.getInstance().getLanguage());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_collective_expense_account_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int p = h.getAdapterPosition();
        h.binding.accountNameTv.setText(accounts.get(p).getAccountTitle());
        double totalCostD = Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEATotalCostList().getValue()).get(accounts.get(p).getTableId()));

        String totalCost = (totalCostD != 0 ? context.getString(R.string.tkSign) + " " + String.format(language, "%.2f", totalCostD) : " - ");
        h.binding.totalCostTv.setText(totalCost);

        ArrayList<PersonExpense> personExpenses = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEAPersonsExpenseList().getValue()).get(accounts.get(p).getTableId())));
        int noOfPerson = personExpenses.size();


        switch (noOfPerson) {
            case 5:
                layoutMaker(h.binding.name5Tv, h.binding.paid5Tv, h.binding.cost5Tv, h.binding.receive5Tv, h.binding.due5Tv, h.binding.ll5, personExpenses.get(4));
            case 4:
                layoutMaker(h.binding.name4Tv, h.binding.paid4Tv, h.binding.cost4Tv, h.binding.receive4Tv, h.binding.due4Tv, h.binding.ll4, personExpenses.get(3));
            case 3:
                layoutMaker(h.binding.name3Tv, h.binding.paid3Tv, h.binding.cost3Tv, h.binding.receive3Tv, h.binding.due3Tv, h.binding.ll3, personExpenses.get(2));
            case 2:
                layoutMaker(h.binding.name1Tv, h.binding.paid1Tv, h.binding.cost1Tv, h.binding.receive1Tv, h.binding.due1Tv, h.binding.ll1, personExpenses.get(0));
                layoutMaker(h.binding.name2Tv, h.binding.paid2Tv, h.binding.cost2Tv, h.binding.receive2Tv, h.binding.due2Tv, h.binding.ll2, personExpenses.get(1));
                break;
        }

        h.itemView.setOnClickListener(v -> {
            listener.onRvClicked(p);
            activityLauncher(accounts.get(p));
        });
        h.itemView.setOnLongClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            String id = accounts.get(p).getTableId() + userRepo.getNumber().substring(4);
            ClipData clip = ClipData.newPlainText("Copied Text", id);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Id copied.", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        AdapterCollectiveExpenseAccountLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AdapterCollectiveExpenseAccountLayoutBinding.bind(itemView);
        }
    }

    private void activityLauncher(ExpenseAccount expenseAccount) {
        Intent intent;
        switch (expenseAccount.getType()) {
            case "cea":
                intent = new Intent(context, ViewCollectiveAccount.class);
                intent.putExtra(TAG_CEA, Objects.requireNonNull(dataRepo.getMutableCEAList().getValue()).get(expenseAccount.getTableId()));
                intent.putExtra(TAG_ACCOUNT_NAME, expenseAccount.getAccountTitle());
                context.startActivity(intent);
                break;
//            case "pea":
//                break;
//            case "hea":
//                break;
        }
    }

    private void layoutMaker(TextView nameTv, TextView paidTv, TextView costTv, TextView owingTv, TextView dueTv, LinearLayout ll, PersonExpense personExpense) {
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
        for (int i = calledListenersSize; i < accounts.size(); i++) {
            initiateListeners(accounts.get(i - calledListenersSize));
        }
        calledListenersSize = accounts.size();
    }

    public void destroyListeners() {
        for (int i = 0; i < calledListenersSize; i++) {
            references.get(i).removeEventListener(listeners.get(i));
        }
    }

    private void initiateListeners(ExpenseAccount expenseAccount) {
        FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(TAG_TABLE + expenseAccount.getTableId()).child(TAG_ROW_COUNT).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DBHelper dbHelper = new DBHelper(context);
                Integer _rowCount = Objects.requireNonNull(task.getResult()).getValue(Integer.class);
                if (_rowCount == null)
                    return;
                int rowCount = dbHelper.getRowCount(expenseAccount.getTableId());
                dbHelper.close();
                if (_rowCount < rowCount) {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent = new Intent(context, DataLoaderServiceF.class);
                        intent.putExtra(TAG_CURRENT_RC, rowCount);
                        intent.putExtra(TAG_OLD_RC, _rowCount + 1);
                        intent.putExtra(TAG_TABLE_NAME, TAG_TABLE + expenseAccount.getTableId());
                        intent.putExtra(TAG_PHONE_NUMBER, userRepo.getNumber());
                        intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                        context.startForegroundService(intent);
                    } else {
                        intent = new Intent(context, DataLoaderServiceB.class);
                        intent.putExtra(TAG_CURRENT_RC, rowCount);
                        intent.putExtra(TAG_OLD_RC, _rowCount + 1);
                        intent.putExtra(TAG_TABLE_NAME, TAG_TABLE + expenseAccount.getTableId());
                        intent.putExtra(TAG_PHONE_NUMBER, userRepo.getNumber());
                        intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                        context.startService(intent);
                    }
                }
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(TAG_TABLE + expenseAccount.getTableId()).child(TAG_ROW_COUNT);
        ValueEventListener listener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DBHelper dbHelper = new DBHelper(context);
                    Integer _rowCount = snapshot.getValue(Integer.class);
                    if (_rowCount == null)
                        return;
                    int rowCount = dbHelper.getRowCount(expenseAccount.getTableId());
                    dbHelper.close();
                    if (_rowCount > rowCount) {
                        FirebaseDatabase.getInstance().getReference(FDR_CEA_TABLES).child(TAG_TABLE + expenseAccount.getTableId()).child(TAG_TABLE).orderByKey().limitToLast(_rowCount - rowCount).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DBHelper dbHelper1 = new DBHelper(context);
                                for (DataSnapshot ds : Objects.requireNonNull(task.getResult()).getChildren()) {
                                    String s = ds.getValue(String.class);
                                    ArrayList<String> results = new ArrayList<>();
                                    int startIndex = 0, endIndex = 0;
                                    for (char c : Objects.requireNonNull(s).toCharArray()) {
                                        if (c == '|') {
                                            results.add(s.substring(startIndex, endIndex));
                                            startIndex = endIndex + 1;
                                        }
                                        endIndex++;
                                    }
                                    ArrayList<Double> costAmounts = new ArrayList<>();
                                    ArrayList<Double> paidAmounts = new ArrayList<>();
                                    int noOfPerson = Objects.requireNonNull(Objects.requireNonNull(dataRepo.getMutableCEAList().getValue()).get(expenseAccount.getTableId())).getNames().size();
                                    for (int i = 7; i < (7 + noOfPerson); i++) {
                                        costAmounts.add(Double.parseDouble(results.get(i)));
                                    }
                                    for (int i = (7 + noOfPerson); i < 7 + (2 * noOfPerson); i++) {
                                        paidAmounts.add(Double.parseDouble(results.get(i)));
                                    }
                                    dbHelper1.saveCEAExpense(TAG_TABLE + expenseAccount.getTableId(),
                                            Integer.parseInt(results.get(1)),
                                            Integer.parseInt(results.get(2)),
                                            results.get(3), results.get(4),
                                            results.get(5), Double.parseDouble(results.get(6)),
                                            costAmounts, paidAmounts, Objects.requireNonNull(dataRepo.getMutableCEAList().getValue().get(expenseAccount.getTableId())).getNames());
                                }
                                dbHelper1.updateCEARowCount(expenseAccount.getTableId(), _rowCount);
                                dbHelper1.close();
                                DataRepo.getInstance(context).update(context);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        references.add(reference);
        listeners.add(listener);
    }
}
