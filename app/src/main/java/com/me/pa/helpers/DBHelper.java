package com.me.pa.helpers;

import static com.me.pa.others.Constants.CN_TOTAL_COST;
import static com.me.pa.others.Constants.SQLITE_DATABASE_NAME;
import static com.me.pa.others.Constants.TAG_CEA;
import static com.me.pa.others.Constants.TAG_EXPENSE_ACCOUNT;
import static com.me.pa.others.Constants.TAG_PHONE_NUMBER;
import static com.me.pa.others.Constants.TAG_TABLE;
import static com.me.pa.others.Constants.TAG_TABLE_NAME;
import static com.me.pa.others.Constants.TAG_TYPE;
import static com.me.pa.others.Constants.TYPE_ACCOUNT;
import static com.me.pa.others.Constants.TYPE_CEA;
import static com.me.pa.others.Constants.TYPE_CEA_TABLE;
import static com.me.pa.others.Constants.TYPE_ONLINE;
import static com.me.pa.others.Constants.TYPE_PEA;
import static com.me.pa.others.Functions.cursorToString;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.me.pa.models.CEA;
import com.me.pa.models.CEARow;
import com.me.pa.models.ExpenseAccount;
import com.me.pa.models.PersonalExpense;
import com.me.pa.models.SingleCost;
import com.me.pa.others.Functions;
import com.me.pa.repos.DataRepo;
import com.me.pa.repos.UserRepo;
import com.me.pa.services.DataLoaderServiceB;
import com.me.pa.services.DataLoaderServiceF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {

    private final UserRepo repo;
    Context context;

    public DBHelper(@Nullable Context context) {
        super(context, SQLITE_DATABASE_NAME, null, 1);
        this.context = context;
        repo = UserRepo.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table accounts(id INTEGER Primary key AUTOINCREMENT,accountTitle TEXT,type TEXT,tableId TEXT)");
        db.execSQL("Create Table collectiveAccounts(id INTEGER Primary key AUTOINCREMENT,noOfPerson INTEGER,rowCount INTEGER,tableId TEXT,pn_1 TEXT,pn_2 TEXT,pn_3 TEXT,pn_4 TEXT,pn_5 TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists accounts");
        db.execSQL("drop Table if exists collectiveAccounts");
    }


    public void insertIntoAccounts(ExpenseAccount expenseAccount, boolean runService) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into accounts(accountTitle,type,tableId) Values(\"" + expenseAccount.getAccountTitle() + "\",\"" + expenseAccount.getType() + "\",\"" + expenseAccount.getTableId() + "\")");

        if (repo.getAccountType().equals(TYPE_ONLINE) && runService) {
            Intent intent;
            expenseAccount.setId(Objects.requireNonNull(DataRepo.getInstance(context).getMutableExpenseAccountList().getValue()).size() + 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = new Intent(context, DataLoaderServiceF.class);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_ACCOUNT);
                intent.putExtra(TAG_EXPENSE_ACCOUNT, expenseAccount);
                context.startForegroundService(intent);
            } else {
                intent = new Intent(context, DataLoaderServiceB.class);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_ACCOUNT);
                intent.putExtra(TAG_EXPENSE_ACCOUNT, expenseAccount);
                context.startService(intent);
            }
        }
        db.close();
    }

    public void insertIntoCollectiveAccounts(CEA cea, boolean runService) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder nameList = new StringBuilder();
        StringBuilder th = new StringBuilder();
        for (int i = 0; i < cea.getNoOfPerson(); i++) {
            th.append(",pn_").append(i + 1);
            nameList.append(",'").append(cea.getNames().get(i)).append("'");
        }
        String query = "insert into collectiveAccounts(noOfPerson,rowCount,tableId" + th + ") " +
                "Values(" + cea.getNoOfPerson() + "," + cea.getRowCount() + ",'" + cea.getTableId() + "'" + nameList + ")";
        db.execSQL(query);

        if (repo.getAccountType().equals(TYPE_ONLINE) && runService) {
            Intent intent;
            cea.setId(Objects.requireNonNull(DataRepo.getInstance(context).getMutableCEAList().getValue()).size() + 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = new Intent(context, DataLoaderServiceF.class);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_CEA);
                intent.putExtra(TAG_CEA, cea);
                context.startForegroundService(intent);
            } else {
                intent = new Intent(context, DataLoaderServiceB.class);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_CEA);
                intent.putExtra(TAG_CEA, cea);
                context.startService(intent);
            }
        }
        db.close();
    }

    public boolean isTableExists(String tableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select id from accounts where tableId='" + tableId + "'";
        Cursor cursor = db.rawQuery(query, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    public boolean isRowExists(String tableName, String time, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select id from "
                + tableName + " where time='"
                + time + "' and number='" + number + "'";
        Cursor cursor = db.rawQuery(query, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }


    public int getRowCount(String tableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = TAG_TABLE + tableId;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        cursor.moveToNext();
        int rowCount = cursor.getCount();
        cursor.close();
        db.close();
        return rowCount;
    }

    public int getOldRowCount(String tableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select rowCount from collectiveAccounts WHERE tableId=" + tableId, null);
        cursor.moveToNext();
        int rowCount = cursor.getInt(0);
        cursor.close();
        db.close();
        return rowCount;
    }


    //All About Personal Expense Account

    public void createNewPEA(String accountTitle, boolean runService) {
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault());
        String dateTime = ft.format(new Date());

        insertIntoAccounts(new ExpenseAccount(accountTitle, TYPE_PEA, dateTime), runService);


        createNewPEATable(TAG_TABLE + dateTime);
    }

    public void createNewPEATable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Create Table " + tableName + "(id INTEGER Primary key AUTOINCREMENT,date INT,monthYear INT,time TEXT,description TEXT,income DOUBLE,expense DOUBLE,type INT)");
        db.close();
    }

    public void insertIntoPE(String tableName,
                             int date,
                             String description,
                             double amount,
                             int type) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssSSSa", Locale.getDefault());
        String time = ft.format(new Date());
        String query = "insert into " + tableName + "(date,monthYear,time,description,income,expense,type) " +
                "Values(" + date + "," + Integer.parseInt(String.valueOf(date).substring(0, 6)) + ",'" + time + "','" + description + "'," + (type == 1 ? amount : 0) + "," + (type == 2 ? amount : 0) + "," + type + ")";
        db.execSQL(query);
        db.close();
    }

    public void getPE(String tableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = TAG_TABLE + tableId;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        cursor.moveToNext();
        cursorToString(cursor);
        db.close();
    }


    public ArrayList<Integer> getMonths(String tableId) {
        ArrayList<Integer> months = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = TAG_TABLE + tableId;
        Cursor cursor = db.rawQuery("Select distinct monthYear from " + tableName + " ORDER BY monthYear DESC", null);
        while (cursor.moveToNext()) {
            months.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return months;
    }

    public ArrayList<PersonalExpense> getExpenseByMonth(String tableId, int month) {
        ArrayList<PersonalExpense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = TAG_TABLE + tableId;
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE monthYear=" + month + " ORDER BY date DESC,time DESC, id DESC", null);
        while (cursor.moveToNext()) {
            PersonalExpense personalExpense = new PersonalExpense(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getInt(7));
            expenses.add(personalExpense);
        }
        cursor.close();
        db.close();
        return expenses;
    }


    //All About Personal Expense Account


    //All About Collective Expense Account
    public void createNewCollectiveAccount(String accountTitle, int noOfPerson, ArrayList<String> names, boolean runService) {
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault());
        String dateTime = ft.format(new Date());

        insertIntoAccounts(new ExpenseAccount(accountTitle, TYPE_CEA, dateTime), runService);

        insertIntoCollectiveAccounts(new CEA(noOfPerson, 0, dateTime, names), runService);

        createNewCEATable(TAG_TABLE + dateTime, names);
    }

    public void createNewCEATable(String tableName, ArrayList<String> names) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder costHeader = new StringBuilder();
        for (String name : names) {
            costHeader.append(",").append(name).append("_cost").append(" DOUBLE");
        }
        StringBuilder paidHeader = new StringBuilder();
        for (String name : names) {
            paidHeader.append(",").append(name).append("_paid").append(" DOUBLE");
        }
        db.execSQL("Create Table " + tableName + "(id INTEGER Primary key AUTOINCREMENT,date INT,monthYear INT,time TEXT,enteredBy TEXT,number TEXT,synced TEXT,description TEXT,totalCost DOUBLE" + costHeader + paidHeader + ")");
        db.close();
    }

    public void insertIntoCEA(String tableName,
                              int date,
                              String description,
                              double totalCost,
                              ArrayList<String> names,
                              ArrayList<Double> costs,
                              ArrayList<Double> paids) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssSSSa", Locale.getDefault());
        String time = ft.format(new Date());
        StringBuilder costHeader = new StringBuilder();
        StringBuilder paidHeader = new StringBuilder();
        StringBuilder costList = new StringBuilder();
        StringBuilder paidList = new StringBuilder();
        String number = repo.getNumber() == null ? "0" : repo.getNumber();
        for (int i = 0; i < names.size(); i++) {
            costHeader.append(",").append(names.get(i).replaceAll(" ", "_")).append("_cost");
            paidHeader.append(",").append(names.get(i).replaceAll(" ", "_")).append("_paid");
            costList.append(",").append(costs.get(i));
            paidList.append(",").append(paids.get(i));
        }
        String query = "insert into " + tableName
                + "(date,monthYear,description,totalCost"
                + costHeader + paidHeader
                + ",time,enteredBy,number,synced) Values("
                + date + "," + Integer.parseInt(String.valueOf(date).substring(0, 6))
                + ",'" + description + "'," + totalCost + costList + paidList
                + ",'" + time + "','" + repo.getName() + "','" + number + "','f')";
        db.execSQL(query);
        db.close();
        if (repo.getAccountType().equals(TYPE_ONLINE)) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent = new Intent(context, DataLoaderServiceF.class);
                intent.putExtra(TAG_TABLE_NAME, tableName);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                context.startForegroundService(intent);
            } else {
                intent = new Intent(context, DataLoaderServiceB.class);
                intent.putExtra(TAG_TABLE_NAME, tableName);
                intent.putExtra(TAG_PHONE_NUMBER, repo.getNumber());
                intent.putExtra(TAG_TYPE, TYPE_CEA_TABLE);
                context.startService(intent);
            }
        }
    }

    public void insertIntoCEA(String tableName,
                              int date,
                              int monthYear,
                              String time,
                              String enteredBy,
                              String number,
                              String synced,
                              String description,
                              double totalCost,
                              ArrayList<Double> costs,
                              ArrayList<Double> paids,
                              ArrayList<String> names) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder costHeader = new StringBuilder();
        StringBuilder paidHeader = new StringBuilder();
        StringBuilder costList = new StringBuilder();
        StringBuilder paidList = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            costHeader.append(",").append(names.get(i).replaceAll(" ", "_")).append("_cost");
            paidHeader.append(",").append(names.get(i).replaceAll(" ", "_")).append("_paid");
            costList.append(",").append(costs.get(i));
            paidList.append(",").append(paids.get(i));
        }
        String query = "insert into "
                + tableName
                + "(date,monthYear,description,totalCost"
                + costHeader + paidHeader
                + ",time,enteredBy,number,synced) Values("
                + date + "," + monthYear + ",'" + description
                + "'," + totalCost + costList + paidList
                + ",'" + time + "','" + enteredBy + "','"
                + number + "','" + synced + "')";
        db.execSQL(query);
        db.close();

    }

    public void updateCEAById(String tableName, String cName, String st, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + tableName + " SET " + cName + "='" + st + "' WHERE id=" + id;
        db.execSQL(query);
        db.close();
    }

//    public void updateCEAExpense(String tableName,
//                                 int id,
//                                 int date,
//                                 int monthYear,
//                                 String time,
//                                 String enteredBy,
//                                 String description,
//                                 double totalCost,
//                                 ArrayList<Double> costs,
//                                 ArrayList<Double> paids,
//                                 ArrayList<String> names
//    ) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        StringBuilder costList = new StringBuilder();
//        StringBuilder paidList = new StringBuilder();
//
//        for (int i = 0; i < names.size(); i++) {
//            costList.append(",").append(names.get(i).replaceAll(" ", "_")).append("_cost=").append(costs.get(i));
//            paidList.append(",").append(names.get(i).replaceAll(" ", "_")).append("_paid=").append(paids.get(i));
//
//        }
//
//        String query = "update " + tableName + " set date=" + date + ",monthYear=" + monthYear + ",description='" + description + "',totalCost=" + totalCost + ",time='" + time + "',enteredBy='" + enteredBy + "'" + paidList + costList + " where id=" + id;
//        db.execSQL(query);
//        db.close();
//    }

    public void updateCEARowCount(String tableId, int rowCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE collectiveAccounts SET rowCount=" + rowCount + " WHERE tableId=" + tableId;
        db.execSQL(query);
        db.close();
    }

    public LinkedHashMap<String, String> getSpecificRows(String tableName, int oldRC, int currentRC) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE id BETWEEN " + oldRC + " AND " + currentRC;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> rows = Functions.cursorToStringList(cursor);
        LinkedHashMap<String, String> rowMap = new LinkedHashMap<>();
        for (String row : rows) {
            rowMap.put(String.valueOf(oldRC), row);
            oldRC++;
        }
        db.close();
        return rowMap;
    }

    public String getSpecificRow(String tableName, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + tableName + " WHERE id=" + id;
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> rows = Functions.cursorToStringList(cursor);
        db.close();
        return rows.get(0);
    }

    public LinkedHashMap<Integer, String> getRowByUnSynced(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "
                + tableName
                + " WHERE number='"
                + repo.getNumber() + "' and synced='f'";
        Cursor cursor = db.rawQuery(query, null);
        LinkedHashMap<Integer, String> rows = Functions.cursorToStringMap(cursor);
        db.close();
        return rows;
    }

    public LinkedHashMap<String, CEA> getCEAAccounts() {
        LinkedHashMap<String, CEA> ceaMap = new LinkedHashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from collectiveAccounts", null);
        while (cursor.moveToNext()) {
            ArrayList<String> names = new ArrayList<>();
            for (int i = 0; i < cursor.getInt(1); i++) {
                names.add(cursor.getString(4 + i));
            }
            CEA cea = new CEA(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3), names);
            ceaMap.put(cursor.getString(3), cea);
        }
        cursor.close();
        db.close();
        return ceaMap;
    }

    public LinkedHashMap<String, ArrayList<SingleCost>> getCEAPersonExpenses() {
        LinkedHashMap<String, ArrayList<SingleCost>> ceaPersonExpensesMap = new LinkedHashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from collectiveAccounts", null);
        while (cursor.moveToNext()) {
            String tableId = cursor.getString(3);
            ArrayList<String> names = new ArrayList<>(getCEAPersonNamesByTable(tableId));
            ArrayList<SingleCost> personExpenses = new ArrayList<>();

            for (String name : names) {
                String nName = name.replaceAll(" ", "_");
                double paid = sumColumn(TAG_TABLE + tableId, nName + "_paid");
                double cost = sumColumn(TAG_TABLE + tableId, nName + "_cost");
                double due = cost - paid;
                if (due < 0) {
                    personExpenses.add(new SingleCost(name, paid, cost, 0, -due));
                } else if (due > 0) {
                    personExpenses.add(new SingleCost(name, paid, cost, due, 0));
                } else {
                    personExpenses.add(new SingleCost(name, paid, cost, 0, 0));
                }
            }
            ceaPersonExpensesMap.put(tableId, personExpenses);
        }
        cursor.close();
        db.close();
        return ceaPersonExpensesMap;
    }

    public LinkedHashMap<String, Double> getCEATotalCosts() {
        LinkedHashMap<String, Double> totalCostMap = new LinkedHashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from collectiveAccounts", null);
        while (cursor.moveToNext()) {
            String tableId = cursor.getString(3);
            totalCostMap.put(tableId, sumColumn(TAG_TABLE + tableId, CN_TOTAL_COST));
        }
        cursor.close();
        db.close();
        return totalCostMap;
    }

    public LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>> getCEATables() {
        LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<CEARow>>> a = new LinkedHashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from collectiveAccounts", null);
        while (cursor.moveToNext()) {
            LinkedHashMap<Integer, ArrayList<CEARow>> aaa = new LinkedHashMap<>();
            String tableId = cursor.getString(3);
            int noOfPerson = cursor.getInt(1);
            ArrayList<Integer> months = new ArrayList<>(getCEATableMonths(TAG_TABLE + tableId));
            for (Integer month : months) {
                ArrayList<CEARow> caRows = new ArrayList<>(getCEATableRows(TAG_TABLE + tableId, month, noOfPerson));
                aaa.put(month, caRows);
            }
            a.put(tableId, aaa);
        }
        cursor.close();
        db.close();
        return a;
    }

    public ArrayList<Integer> getCEATableMonths(String tableName) {
        ArrayList<Integer> months = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select distinct monthYear from " + tableName + " ORDER BY monthYear DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            months.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return months;
    }

    public ArrayList<CEARow> getCEATableRows(String tableName, int monthYear, int noOfPeople) {
        ArrayList<CEARow> caRows = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from " + tableName + " where monthYear=" + monthYear + " ORDER BY date DESC,time DESC, id DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int date = cursor.getInt(1);
            String time = cursor.getString(3);
            String enteredBy = cursor.getString(4);
            String number = cursor.getString(5);
            String synced = cursor.getString(6);
            String description = cursor.getString(7);
            double totalCost = cursor.getDouble(8);
            double[] costs = new double[noOfPeople];
            for (int i = 9; i < (9 + noOfPeople); i++) {
                costs[i - 9] = cursor.getDouble(i);
            }
            double[] paids = new double[noOfPeople];
            for (int i = (9 + noOfPeople); i < 9 + (2 * noOfPeople); i++) {
                paids[i - (9 + noOfPeople)] = cursor.getDouble(i);
            }
            caRows.add(new CEARow(description, time, enteredBy, number, synced, date, totalCost, paids, costs));
        }
        cursor.close();
        db.close();
        return caRows;
    }

    public LinkedHashMap<String, ArrayList<String>> getCEAPersonNames() {
        LinkedHashMap<String, ArrayList<String>> ceaPersonNamesMap = new LinkedHashMap<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from collectiveAccounts", null);
        while (cursor.moveToNext()) {
            String tableId = cursor.getString(3);
            ceaPersonNamesMap.put(tableId, getCEAPersonNamesByTable(tableId));
        }
        cursor.close();
        db.close();
        return ceaPersonNamesMap;
    }

    public ArrayList<String> getCEAPersonNamesByTable(String tableId) {
        ArrayList<String> names = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select * from collectiveAccounts where tableId=" + tableId;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        for (int i = 0; i < cursor.getInt(1); i++) {
            names.add(cursor.getString(4 + i).replaceAll("_", " "));
        }
        cursor.close();
        db.close();
        return names;
    }

    //All About Collective Expense Account

    public ArrayList<ExpenseAccount> getExpenseAccounts() {
        ArrayList<ExpenseAccount> expenseAccounts = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from accounts", null);
        while (cursor.moveToNext()) {
            expenseAccounts.add(new ExpenseAccount(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();
        db.close();
        return expenseAccounts;
    }


    public void deleteAccount(ExpenseAccount account) {
        SQLiteDatabase db = this.getWritableDatabase();
        String title = account.getAccountTitle() + account.getTableId();
        db.execSQL("DELETE FROM accounts where id=?", new String[]{String.valueOf(account.getId())});
        db.execSQL("DELETE FROM collectiveAccounts where tableId=?", new String[]{title});
        db.execSQL("drop Table if exists " + title);
        db.close();
    }

    public double sumColumn(String tableName, String columnName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT SUM(" + columnName + ") FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        double totalCost = cursor.getDouble(0);
        cursor.close();
        db.close();
        return totalCost;
    }

}

