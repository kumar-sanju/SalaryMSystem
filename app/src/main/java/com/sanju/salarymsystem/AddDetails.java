package com.sanju.salarymsystem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AddDetails extends AppCompatActivity {
    static final int DATE_DIALOG_ID = 0;
    static final int IN_TIME_DIALOG_ID = 1;
    static final int OUT_TIME_DIALOG_ID = 2;

    Toolbar toolbar;
    TextView date, inTime, outTime, totalHoursWorked, regularHoursWorked, otHoursWorked;
    Button addDetails;

    boolean isInTimeSet, isOutTimeSet;

    int[] cTotalHoursWorked, cRegularHoursWorked, cOtHoursWorked;
    SimpleDateFormat dateFormat, timeFormat;
    GregorianCalendar current, selected, start, end;

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            selected.set(year, monthOfYear, dayOfMonth);
            setDateOnView(date, selected);
        }
    };

    private TimePickerDialog.OnTimeSetListener onInTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            isInTimeSet = true;
            start.set(selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            setTimeOnView(inTime, start);
            if (isOutTimeSet) {
                calculateWorkedHours();
            }
        }
    };
    private TimePickerDialog.OnTimeSetListener onOutTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            isOutTimeSet = true;
            end.set(selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            boolean isValid = calculateWorkedHours();
            if (isValid) {
                setTimeOnView(outTime, end);
            }
        }
    };


    private static int[] getHoursAndMinutes(long minutes) {
        int[] data = new int[]{0, 0};
        if (minutes < -1) {
            return data;
        }
        data[0] = (int) minutes / 60;
        data[1] = (int) minutes % 60;
        return data;
    }

    private boolean calculateWorkedHours() {
        long difference = end.getTimeInMillis() - start.getTimeInMillis();
        if (end.getTimeInMillis() <= start.getTimeInMillis()) {
            createToast("Invalid Time Selected", Toast.LENGTH_SHORT);
            resetViews();
            return false;
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference);
        cTotalHoursWorked = getHoursAndMinutes(minutes);
        totalHoursWorked.setText("Total : " + cTotalHoursWorked[0] + " Hours " + cTotalHoursWorked[1] + " Minutes");
        calculateRegularAndOt(minutes);
        return true;
    }

    private void resetViews() {
        outTime.setText("Click to select Out Time");
        totalHoursWorked.setText("Total Hours Worked");
        regularHoursWorked.setText("Regular Hours Worked");
        otHoursWorked.setText("OT Hours Worked");

        cTotalHoursWorked[0] = 0;
        cTotalHoursWorked[1] = 0;
        cOtHoursWorked[0] = 0;
        cOtHoursWorked[1] = 0;
        cRegularHoursWorked[0] = 0;
        cRegularHoursWorked[1] = 0;
    }

    private void setTimeOnView(TextView view, GregorianCalendar time) {
        if (view.getId() == R.id.in_time) {
            time.setTimeZone(TimeZone.getDefault());
            String formatted = timeFormat.format(time.getTime());
            inTime.setText("In Time : " + formatted);
        } else if (view.getId() == R.id.out_time) {
            String formatted = timeFormat.format(time.getTime());
            outTime.setText("Out Time : " + formatted);
        }
    }

    private void setDateOnView(TextView date, GregorianCalendar selected) {
        String formatted = dateFormat.format(selected.getTime());
        date.setText("Date : " + formatted);
    }

    private void calculateRegularAndOt(long minutes) {
        regularHoursWorked.setText("Regular Hours Worked");
        otHoursWorked.setText("OT Hours Worked");
        if (minutes < 240) {
            cRegularHoursWorked = getHoursAndMinutes(0);
            cOtHoursWorked = getHoursAndMinutes(minutes);
        } else if (minutes == 240) {
            cRegularHoursWorked = getHoursAndMinutes(240);
            cOtHoursWorked = getHoursAndMinutes(0);
        } else if (minutes == 480) {
            cRegularHoursWorked = getHoursAndMinutes(480);
            cOtHoursWorked = getHoursAndMinutes(0);
        } else if (minutes > 480) {
            cRegularHoursWorked = getHoursAndMinutes(480);
            minutes = minutes - 480;
            cOtHoursWorked = getHoursAndMinutes(minutes);

        } else if (minutes > 240 && minutes < 480) {
            cRegularHoursWorked = getHoursAndMinutes(240);
            minutes = minutes - 240;
            cOtHoursWorked = getHoursAndMinutes(minutes);
        }
        regularHoursWorked.setText("Regular : " + cRegularHoursWorked[0] + " Hours " + cRegularHoursWorked[1] + " Minutes");
        otHoursWorked.setText("OT : " + cOtHoursWorked[0] + " Hours " + cOtHoursWorked[1] + " Minutes");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        setToolbar();
        initialize();
        addDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInTimeSet && isOutTimeSet) {
                    SalaryDbHelper dbHelper = new SalaryDbHelper(AddDetails.this);
                    long id = dbHelper.insertDetails(selected.getTimeInMillis(), start.getTimeInMillis(), end.getTimeInMillis(), Salary.getRegularPay(), Salary.getOtPay());
                    if (id > 0) {
                        createToast("Details have been added into database :-)\t", Toast.LENGTH_SHORT);
                        startActivity(new Intent(AddDetails.this, Home.class));
                        finish();
                    }
                } else {
                    createToast("Set Details to add to Database", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Details");
    }

    private void initialize() {
        date = (TextView) findViewById(R.id.date);
        inTime = (TextView) findViewById(R.id.in_time);
        outTime = (TextView) findViewById(R.id.out_time);
        totalHoursWorked = (TextView) findViewById(R.id.total_hours_worked);
        otHoursWorked = (TextView) findViewById(R.id.ot_hours_worked);
        regularHoursWorked = (TextView) findViewById(R.id.regular_hours_worked);
        addDetails = (Button) findViewById(R.id.add_details);

        current = new GregorianCalendar();
        selected = new GregorianCalendar();
        start = new GregorianCalendar();
        end = new GregorianCalendar();

        isInTimeSet = false;
        isOutTimeSet = false;

        cTotalHoursWorked = new int[]{0, 0};
        cRegularHoursWorked = new int[]{0, 0};
        cOtHoursWorked = new int[]{0, 0};

        dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getDefault());
        setDateOnView(date, selected);
    }

    public void dialogDecision(View v) {
        switch (v.getId()) {
            case R.id.date:
                showDialog(DATE_DIALOG_ID);
                break;
            case R.id.in_time:
                showDialog(IN_TIME_DIALOG_ID);
                break;
            case R.id.out_time:
                if (isInTimeSet) {
                    showDialog(OUT_TIME_DIALOG_ID);
                } else {
                    createToast("Set In-Time First", Toast.LENGTH_SHORT);
                }
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, datePickerListener, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH));
            case IN_TIME_DIALOG_ID:
                return new TimePickerDialog(this, onInTimeSetListener, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false);
            case OUT_TIME_DIALOG_ID:
                return new TimePickerDialog(this, onOutTimeSetListener, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false);
        }
        return null;
    }

    private void createToast(String message, int duration) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
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
                startActivity(new Intent(AddDetails.this, About.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(AddDetails.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                Intent i = new Intent(AddDetails.this, Home.class);
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
        Intent i = new Intent(AddDetails.this, Home.class);
        startActivity(i);
        finish();
    }
}
