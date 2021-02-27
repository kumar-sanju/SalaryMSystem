package com.sanju.salarymsystem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Information extends AppCompatActivity {
    static final int DATE_DIALOG_ID = 0;
    static final int IN_TIME_DIALOG_ID = 1;
    static final int OUT_TIME_DIALOG_ID = 2;

    long id;

    Toolbar toolbar;
    TextView date, inTime, outTime, totalHoursWorked, regularHoursWorked, otHoursWorked;
    Button edit, delete;

    int[] cTotalHoursWorked, cRegularHoursWorked, cOtHoursWorked;
    SimpleDateFormat dateFormat, timeFormat;
    GregorianCalendar current, selected, start, end;
    SalaryDbHelper dbHelper;
    boolean isEditable = false;
    private Data fetched;
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
            start.set(selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            boolean isValid = getTimeDifference() >= 0;
            if (isValid) {
                setTimeOnView(inTime, start);
                calculateWorkedHours();
            } else {
                start.setTimeInMillis(fetched.getInTime());
                createToast("Invalid Time Selected", Toast.LENGTH_SHORT);
            }
        }
    };
    private TimePickerDialog.OnTimeSetListener onOutTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end.set(selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            boolean isValid = getTimeDifference() >= 0;
            if (isValid) {
                setTimeOnView(outTime, end);
                calculateWorkedHours();
            } else {
                end.setTimeInMillis(fetched.getOutTime());
                createToast("Invalid Time Selected", Toast.LENGTH_SHORT);
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

    private long getTimeDifference() {
        long difference = end.getTimeInMillis() - start.getTimeInMillis();
        if (end.getTimeInMillis() <= start.getTimeInMillis()) {
            return -1;
        }
        return difference;
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
        if (view.getId() == R.id.info_in_time) {
            time.setTimeZone(TimeZone.getDefault());
            String formatted = timeFormat.format(time.getTime());
            inTime.setText("In Time : " + formatted);
        } else if (view.getId() == R.id.info_out_time) {
            String formatted = timeFormat.format(time.getTime());
            outTime.setText("Out Time : " + formatted);
            calculateWorkedHours();

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
        setContentView(R.layout.activity_information);
        setToolbar();
        initialize();
        edit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String state = (String) edit.getText();
                if (state.contentEquals("Edit")) {
                    edit.setText("Update");
                    isEditable = true;
                } else if (state.contentEquals("Update")) {
                    SalaryDbHelper dbHelper = new SalaryDbHelper(Information.this);
                    long affected = dbHelper.updateDetails(id, selected.getTimeInMillis(), start.getTimeInMillis(), end.getTimeInMillis(), 269, 33);
                    if (affected > 0) {
                        createToast("Database has been Updated :-)\t", Toast.LENGTH_SHORT);
                        startActivity(new Intent(Information.this, Overview.class));
                        finish();
                    }
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Information.this);
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SalaryDbHelper dbHelper = new SalaryDbHelper(Information.this);
                        long affected = dbHelper.deleteDetails(id);
                        if (affected > 0) {
                            createToast("Details deleted from Database", Toast.LENGTH_SHORT);
                            startActivity(new Intent(Information.this, Overview.class));
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("No", null);
                builder.setMessage("Are you sure you want to delete current Information from Database ?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initialize() {
        date = (TextView) findViewById(R.id.info_date);
        inTime = (TextView) findViewById(R.id.info_in_time);
        outTime = (TextView) findViewById(R.id.info_out_time);
        totalHoursWorked = (TextView) findViewById(R.id.info_total_hours_worked);
        otHoursWorked = (TextView) findViewById(R.id.info_ot_hours_worked);
        regularHoursWorked = (TextView) findViewById(R.id.info_regular_hours_worked);
        edit = (Button) findViewById(R.id.info_edit_details);
        delete = (Button) findViewById(R.id.info_delete_details);

        id = getIntent().getLongExtra("ID", 0);

        dbHelper = new SalaryDbHelper(this);

        current = new GregorianCalendar();
        selected = new GregorianCalendar();
        start = new GregorianCalendar();
        end = new GregorianCalendar();

        fetched = dbHelper.getData(id);
        selected.setTimeInMillis(fetched.getDay());
        start.setTimeInMillis(fetched.getInTime());
        end.setTimeInMillis(fetched.getOutTime());

        cTotalHoursWorked = new int[]{0, 0};
        cRegularHoursWorked = new int[]{0, 0};
        cOtHoursWorked = new int[]{0, 0};

        dateFormat = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getDefault());
        setDateOnView(date, selected);
        setTimeOnView(inTime, start);
        setTimeOnView(outTime, end);
    }

    public void dialogDecision(View v) {
        if (!isEditable) {
            createToast("Click Edit button to Edit Data", Toast.LENGTH_SHORT);
            return;
        } else {
            switch (v.getId()) {
                case R.id.date:
                    showDialog(DATE_DIALOG_ID);
                    break;
                case R.id.info_in_time:
                    showDialog(IN_TIME_DIALOG_ID);
                    break;
                case R.id.info_out_time:
                    showDialog(OUT_TIME_DIALOG_ID);
                    break;
            }
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, datePickerListener, selected.get(Calendar.YEAR), selected.get(Calendar.MONTH), selected.get(Calendar.DAY_OF_MONTH));
            case IN_TIME_DIALOG_ID:
                return new TimePickerDialog(this, onInTimeSetListener, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), false);
            case OUT_TIME_DIALOG_ID:
                return new TimePickerDialog(this, onOutTimeSetListener, end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE), false);
        }
        return null;
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
                startActivity(new Intent(Information.this, About.class));
                return true;
            case R.id.settings:
                Intent intent = new Intent(Information.this, UserSettingActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                startActivity(new Intent(Information.this, Overview.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Information");
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Information.this, Overview.class));
        finish();
    }
}
