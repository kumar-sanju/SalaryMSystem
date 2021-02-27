package com.sanju.salarymsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewDetails extends AppCompatActivity {
    Toolbar toolbar;
    Button display;
    DatePicker fromPicker, toPicker;
    GregorianCalendar calendar, start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);
        setToolbar();
        initialize();
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewDetails.this, Overview.class);
                intent.putExtra("FROM_DATE", start.getTimeInMillis());
                intent.putExtra("TO_DATE", end.getTimeInMillis());
                startActivity(intent);
            }
        });

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Details");
    }

    private void initialize() {
        display = (Button) findViewById(R.id.display);
        calendar = new GregorianCalendar();
        fromPicker = (CustomDatePicker) findViewById(R.id.fromPicker);
        toPicker = (CustomDatePicker) findViewById(R.id.toPicker);
        fromPicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                start.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            }
        });
        toPicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                end.set(year, monthOfYear, dayOfMonth, 23, 0, 0);
            }
        });
        start = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1, 0, 0);
        end = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 23, 0);
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
                startActivity(new Intent(ViewDetails.this, About.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(ViewDetails.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                Intent i = new Intent(ViewDetails.this, Home.class);
                startActivity(i);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ViewDetails.this, Home.class);
        startActivity(i);
        finish();
    }
}
