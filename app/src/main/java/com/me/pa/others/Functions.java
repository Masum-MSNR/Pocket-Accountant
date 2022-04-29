package com.me.pa.others;

import static com.me.pa.others.Constants.MY_PREFS_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.me.pa.repos.UserRepo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Functions {
    public static String encodeBitmapToBase64(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 25, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64ToBitmap(String input) {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    public static void toggleKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    public static void changeLanguage(Context context, String language) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        editor.putString("local", language);
        editor.apply();
    }

    public static void cursorToString(Cursor cursor) {
        String cursorString = "";
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String name : columnNames)
                cursorString += String.format("%s ][ ", name);
            cursorString += "\n";
            do {
                for (String name : columnNames) {
                    cursorString += String.format("%s ][ ",
                            cursor.getString(cursor.getColumnIndex(name)));
                }
                cursorString += "\n";
            } while (cursor.moveToNext());
        }
        Log.v("Table", cursorString);
    }

    @SuppressLint("Range")
    public static ArrayList<String> cursorToStringList(Cursor cursor) {
        ArrayList<String> table = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            do {
                StringBuilder cursorString = new StringBuilder();
                for (String name : columnNames) {
                    cursorString.append(String.format("%s|",
                            cursor.getString(cursor.getColumnIndex(name))));
                }
                table.add(String.valueOf(cursorString));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return table;
    }

    @SuppressLint("Range")
    public static LinkedHashMap<Integer,String> cursorToStringMap(Cursor cursor) {
        LinkedHashMap<Integer,String> linkedHashMap=new LinkedHashMap<>();
        if (cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            do {
                StringBuilder cursorString = new StringBuilder();
                cursorString.append(cursor.getInt(0)).append("|");
                cursorString.append(cursor.getInt(1)).append("|");
                cursorString.append(cursor.getInt(2)).append("|");
                cursorString.append(cursor.getString(3)).append("|");
                cursorString.append(cursor.getString(4)).append("|");
                cursorString.append(cursor.getString(5)).append("|");
                cursorString.append(cursor.getString(6)).append("|");
                cursorString.append(cursor.getString(7)).append("|");
                cursorString.append(cursor.getDouble(8)).append("|");
                for(int i=9;i<columnNames.length;i++){
                    cursorString.append(cursor.getDouble(i)).append("|");
                }
                linkedHashMap.put(cursor.getInt(0),String.valueOf(cursorString));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return linkedHashMap;
    }


    private static final char[] banglaDigits = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};
//    private static final char[] englishDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String convertNumberDOL(String number) {
        if (number == null)
            return "";
        if (UserRepo.getInstance().getLanguage().equals("en"))
            return number;
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < number.length(); i++) {
                if (Character.isDigit(number.charAt(i))) {
                    if (((int) (number.charAt(i)) - 48) <= 9) {
                        builder.append(banglaDigits[(int) (number.charAt(i)) - 48]);
                    } else {
                        builder.append(number.charAt(i));
                    }
                } else {
                    builder.append(number.charAt(i));
                }
            }
        } catch (Exception e) {
            return "";
        }
        return builder.toString();
    }

    //connectivity checker for sdk>=23
    public static boolean isConnected(Context context) {
        boolean isOnline = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            isOnline = capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOnline;
    }
}
