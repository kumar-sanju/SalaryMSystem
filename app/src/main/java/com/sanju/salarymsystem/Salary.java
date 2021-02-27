package com.sanju.salarymsystem;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jmprathab on 10/11/15.
 */
public class Salary extends Application {
    public static String name;
    public static int regularPay = 0;
    public static int otPay = 0;
    SharedPreferences sharedPreferences;

    public static int getRegularPay() {
        return regularPay;
    }

    public static int getOtPay() {
        return otPay;
    }

    public static String getUserName() {
        return name;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name = sharedPreferences.getString("prefUsername", "");
        regularPay = Integer.parseInt(sharedPreferences.getString("prefRegularPay", "269"));
        otPay = Integer.parseInt(sharedPreferences.getString("prefOtPay", "33"));
    }

}
