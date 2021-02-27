package com.sanju.salarymsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Home extends AppCompatActivity {
    Toolbar toolbar;
    Button addDetails, viewDetails;
    TextView name, salary, minutesRemaining;
    Intent intent;
    SalaryDbHelper dbHelper;
    GregorianCalendar current, start, end;
    ArrayList<Data> data;
    double totalPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setToolbar();
        initialize();
    }

    public void decision(View v) {
        switch (v.getId()) {
            case R.id.add_details:
                intent = new Intent(this, AddDetails.class);
                break;
            case R.id.view_details:
                intent = new Intent(this, ViewDetails.class);
                break;
        }
        startActivity(intent);
        finish();

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }


    private void initialize() {
        addDetails = (Button) findViewById(R.id.add_details);
        viewDetails = (Button) findViewById(R.id.view_details);
        salary = (TextView) findViewById(R.id.salary);
        minutesRemaining = (TextView) findViewById(R.id.minutes_remaining);
        name = (TextView) findViewById(R.id.name);

        dbHelper = new SalaryDbHelper(Home.this);
        current = new GregorianCalendar();
        start = new GregorianCalendar(current.get(Calendar.YEAR), current.get(Calendar.MONTH) - 1, 1, 0, 0);
        end = new GregorianCalendar(current.get(Calendar.YEAR), current.get(Calendar.MONTH), 1, 0, 0);

        name.setText("Welcome " + Salary.getUserName());
        data = dbHelper.getDataBetweenDates(start.getTimeInMillis(), end.getTimeInMillis());
        int[] otHoursAndMinutes = new int[]{0, 0};
        totalPay = 0;
        for (int i = 0; i < data.size(); i++) {
            otHoursAndMinutes[0] += data.get(i).getRegularAndOtHours()[1][0];
            otHoursAndMinutes[1] += data.get(i).getRegularAndOtHours()[1][1];
            totalPay = totalPay + data.get(i).getTotalPay();

        }
        int otMinutes = (otHoursAndMinutes[0] * 60) + otHoursAndMinutes[1];

        salary.setText("Salary : ₹ " + totalPay);
        minutesRemaining.setText("OT Remaining : " + otMinutes % 60 + " Minutes");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                return true;
            case R.id.about:
                startActivity(new Intent(Home.this, About.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(Home.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
