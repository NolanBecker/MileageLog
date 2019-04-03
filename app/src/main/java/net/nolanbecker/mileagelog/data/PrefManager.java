package net.nolanbecker.mileagelog.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void saveLoginInfo(String email, String password, int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Email", email);
        editor.putString("Password", password);
        editor.putInt("ID", id);
        editor.commit();
    }

    public void saveSortType(String type) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sorting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("type", type);
        editor.apply();
    }

    public boolean isLoggedOut() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isPassEmpty = sharedPreferences.getString("Password", "").isEmpty();
        boolean isIDEmpty = sharedPreferences.getInt("ID", 0) == 0;
        return isEmailEmpty || isPassEmpty || isIDEmpty;
    }

    public int getId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("ID", 0);
    }

    public String getSort() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sorting", Context.MODE_PRIVATE);
        return sharedPreferences.getString("type", "month");
    }

}
