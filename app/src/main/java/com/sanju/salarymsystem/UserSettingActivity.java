package com.sanju.salarymsystem;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.appcompat.widget.Toolbar;

/**
 * Created by jmprathab on 15/11/15.
 */
public class UserSettingActivity extends PreferenceActivity {
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }
}
